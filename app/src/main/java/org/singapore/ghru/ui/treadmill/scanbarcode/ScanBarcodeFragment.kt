package org.singapore.ghru.ui.treadmill.scanbarcode

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import io.reactivex.disposables.CompositeDisposable

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ScanBarcodePatientFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.treadmill.ecgcheck.EcgCheckDialogFragment
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ScanBarcodeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ScanBarcodePatientFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ScanBarcodeViewModel

    private lateinit var codeScanner: CodeScanner

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    var meta: Meta? = null
    var scanId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disposables.add(
            StationCheckRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    val bundle = bundleOf("participant" to participantRequest)
                    Navigation.findNavController(activity!!, R.id.container)
                        .navigate(R.id.action_scanBarcodeFragment_to_contraindicationFragment, bundle)
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ScanBarcodePatientFragmentBinding>(
            inflater,
            R.layout.scan_barcode_patient_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setUser("user")
        viewModel.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {

                val sTime: String = convertTimeTo24Hours()
                val sDate: String = getDate()
                val sDateTime:String = sDate + " " + sTime

                meta = Meta(collectedBy = userData.data?.id, startTime = sDateTime)
            }

        })
        codeScanner = CodeScanner(context!!, binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ONE_DIMENSIONAL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.startPreview()

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity!!, getString(R.string.scan_result) + ": ${it.text}", Toast.LENGTH_LONG).show()

                val checkSum = validateChecksum(it.text, Constants.TYPE_PARTICIPANT)
                if (!checkSum.error) {

                    scanId = it.text

                    if (isNetworkAvailable()) {
                        //viewModel.setScreeningId(scanId.toString())
                        viewModel.setCheckoutScreeningId(scanId)
                        //viewModel.setScreeningId(it.text)
                    } else {
                        //viewModel.setScreeningIdOffline(it.text)
                    }

                } else {
                    codeScanner.startPreview()
                    val errorDialogFragment = ErrorDialogFragment()
                    errorDialogFragment.setErrorMessage(getString(R.string.invalid_code))
                    errorDialogFragment.show(fragmentManager!!)
                    //Crashlytics.logException(Exception(getString(R.string.invalid_code)))
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(
                    activity!!, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        viewModel.participant.observe(this, Observer { participantResource ->
            if (participantResource?.status == Status.SUCCESS) {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta

                if (!participantResource.data?.stationStatus!!)
                {
                    viewModel.setScreeningIdEcgTrace(scanId.toString())
                }
                else
                {
                    val stationCheckDialogFragment = EcgCheckDialogFragment()
                    stationCheckDialogFragment.arguments = bundleOf("participant" to participantRequest)
                    stationCheckDialogFragment.show(fragmentManager!!)
                }

            } else if (participantResource?.status == Status.ERROR) {
                codeScanner.startPreview()
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
            }
        })

        viewModel.getTraceStatus.observe(this, Observer { participantResource ->

//            if (participantResource?.status == Status.SUCCESS)
//            {
//                if (participantResource.data?.data?.isCancelled == 1)
//                {
//                    ecgErrorDialog1()
//                }
//                else if(!participantResource.data?.data?.statusCode!!.equals("100"))
//                {
//                    ecgErrorDialog1()
//                }
//                else if (participantResource.data.data.trace_status != null)
//                {
//                    if (participantResource.data.data.trace_status!!.equals("Normal"))
//                    {
//                        val bundle = bundleOf("participant" to participantRequest)
//                        findNavController().navigate(R.id.action_scanBarcodeFragment_to_contraindicationFragment, bundle)
//                    }
//                    else
//                    {
//                        ecgErrorDialog()
//                    }
//                }
//                else
//                {
//                    Toast.makeText(activity, "Trace status null", Toast.LENGTH_LONG).show()
//                    val bundle = bundleOf("participant" to participantRequest)
//                    findNavController().navigate(R.id.action_scanBarcodeFragment_to_contraindicationFragment, bundle)
//                }
//
//            }
//            else if (participantResource?.status == Status.ERROR)
//            {
//                if (participantResource.message!!.data!!.message.equals("ECG is not started before."))
//                {
//                    ecgErrorDialog1()
//                }
//            }
            if (participantResource?.status == Status.SUCCESS)
            {
                if (participantResource.data?.data?.isCancelled == 1)
                {
                    ecgErrorDialog1(getString(R.string.ecg_error_complete1))
                }
                else if (participantResource.data?.data!!.trace_status != null)
                {
                    if (participantResource.data.data.trace_status!!.equals("Normal"))
                    {
                        //viewModel.setScreeningId(binding.editTextCode.text.toString())
                        val bundle = bundleOf("participant" to participantRequest)
                        findNavController().navigate(R.id.action_scanBarcodeFragment_to_contraindicationFragment, bundle)
                    }
                    else if (participantResource.data.data.trace_status!!.equals("Abnormal"))
                    {
                        ecgErrorDialog("Abnormal ECG", getString(R.string.ecg_error_complete))
                    }
                }
                else
                {
                    ecgErrorDialog("Trace status Error", getString(R.string.ecg_trace_error))
                }
            }
            else if (participantResource?.status == Status.ERROR)
            {
                if (participantResource.message!!.data!!.message.equals("ECG is not started before."))
                {
                    ecgErrorDialog1(getString(R.string.ecg_error_not_start))
                }
            }
            binding.executePendingBindings()
        })

        binding.buttonManualEntry.singleClick {
            val bundle = bundleOf("meta" to meta)
            findNavController().navigate(R.id.action_scanBarcodeFragment_to_manualScanFragment, bundle)
        }

        viewModel.checkoutParticipant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS)
            {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta
                if (!participantResource.data?.stationStatus!!)
                {
                    viewModel.setScreeningId(scanId)
                }
                else
                {
                    if (participantResource.data.is_cancelled == 1)
                    {
                        viewModel.setScreeningId(scanId)
                    }
                    else
                    {
                        val stationCheckDialogFragment = CheckoutCheckDialogFragment()
                        stationCheckDialogFragment.show(fragmentManager!!)
                    }
                }
            } else if (participantResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
            }
            binding.executePendingBindings()
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        viewModel.participant.removeObservers(this)
        viewModel.participantOffline?.removeObservers(this)
        codeScanner.releaseResources()
    }

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(now.time.toLocaleString())
            output = outputformat.format(date)
            return output
        }catch(p: ParseException){
            return ""
        }
    }

    private fun getDate(): String
    {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputformat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    private fun ecgErrorDialog(title: String, message : String)
    {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(activity!!)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->
                {
                    //viewModel.setScreeningId(binding.editTextCode.text.toString())
                    val bundle = bundleOf("participant" to participantRequest)
                    findNavController().navigate(R.id.action_scanBarcodeFragment_to_contraindicationFragment, bundle)
                    dialog.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE ->
                {
                    val reasonDialogFragment = ReasonDialogFragment()
                    reasonDialogFragment.arguments = bundleOf(
                        "participant" to participantRequest,
                        "skipped" to false)
                    reasonDialogFragment.show(fragmentManager!!)
                    dialog.dismiss()
                    //activity!!.finish()
                }
            }
        }

        builder.setPositiveButton(getString(R.string.app_yes),dialogClickListener)
        builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)
        builder.setCancelable(false)

        dialog = builder.create()

        dialog.show()
    }

    private fun ecgErrorDialog1(message:String)
    {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(activity!!)

        builder.setTitle("ECG Error")
        builder.setMessage(message)
        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->
                {
                    activity!!.finish()
                    dialog.dismiss()
                }
//                DialogInterface.BUTTON_NEGATIVE ->
//                {
//                    dialog.dismiss()
//                }
            }
        }

        builder.setPositiveButton(getString(R.string.app_button_ok),dialogClickListener)
        //builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)

        builder.setCancelable(false)

        dialog = builder.create()

        dialog.show()

    }

//    private fun ecgErrorDialog()
//    {
//        lateinit var dialog: AlertDialog
//        val builder = AlertDialog.Builder(activity!!)
//
//        builder.setTitle("ECG Error")
//        builder.setMessage(getString(R.string.ecg_error_complete))
//        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))
//
//        // On click listener for dialog buttons
//        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
//            when(which){
//                DialogInterface.BUTTON_POSITIVE ->
//                {
//                    val bundle = bundleOf("participant" to participantRequest)
//                    findNavController().navigate(R.id.action_scanBarcodeFragment_to_contraindicationFragment, bundle)
//                    dialog.dismiss()
//                }
//                DialogInterface.BUTTON_NEGATIVE ->
//                {
//                    dialog.dismiss()
//                    activity!!.finish()
//                }
//            }
//        }
//
//        builder.setPositiveButton(getString(R.string.app_yes),dialogClickListener)
//        builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)
//
//        dialog = builder.create()
//
//        dialog.show()
//    }
//
//    private fun ecgErrorDialog1()
//    {
//        lateinit var dialog: AlertDialog
//        val builder = AlertDialog.Builder(activity!!)
//
//        builder.setTitle("ECG not complete")
//        builder.setMessage(getString(R.string.ecg_error_complete1))
//        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))
//
//        // On click listener for dialog buttons
//        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
//            when(which){
//                DialogInterface.BUTTON_POSITIVE ->
//                {
//                    activity!!.finish()
//                    dialog.dismiss()
//                }
////                DialogInterface.BUTTON_NEGATIVE ->
////                {
////                    dialog.dismiss()
////                }
//            }
//        }
//
//        builder.setPositiveButton(getString(R.string.app_button_ok),dialogClickListener)
//        //builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)
//
//        dialog = builder.create()
//
//        dialog.show()
//    }

}
