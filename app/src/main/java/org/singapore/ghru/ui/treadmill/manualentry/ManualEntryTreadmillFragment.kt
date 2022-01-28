package org.singapore.ghru.ui.treadmill.manualentry

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import io.reactivex.disposables.CompositeDisposable

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ManualEntryBodyMeasurementFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.treadmill.ecgcheck.EcgCheckDialogFragment
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment
import org.singapore.ghru.vo.ECGData
import javax.inject.Inject

class ManualEntryTreadmillFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<ManualEntryBodyMeasurementFragmentBinding>()

    @Inject
    lateinit var viewModel: ManualEntryTreadmillViewModel

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    var meta: Meta? = null
    var abnormalStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            meta = arguments?.getParcelable<Meta>("meta")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            StationCheckRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    val bundle = bundleOf("participant" to participantRequest)
                    findNavController().navigate(R.id.action_manualScanFragment_to_contraindicationFragment, bundle)
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
        val dataBinding = DataBindingUtil.inflate<ManualEntryBodyMeasurementFragmentBinding>(
            inflater,
            R.layout.manual_entry_body_measurement_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonContinue.singleClick {

            handleContinue()
            view?.hideKeyboard()
        }
        binding.buttonBack.singleClick {
            binding.root.hideKeyboard()
            navController().popBackStack()
        }
        binding.editTextCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textLayoutCode.error = ""
            }
        })

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta

                if (!participantResource.data?.stationStatus!!)
                {
                    viewModel.setScreeningIdEcgTrace(binding.editTextCode.text.toString())
                }
                else
                {
                    val stationCheckDialogFragment = EcgCheckDialogFragment()
                    stationCheckDialogFragment.arguments = bundleOf("participant" to participantRequest)
                    stationCheckDialogFragment.show(fragmentManager!!)
                }
            }
            else if (participantResource?.status == Status.ERROR)
            {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
            }

            binding.executePendingBindings()
        })

        viewModel.getTraceStatus.observe(this, Observer { participantResource ->

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
                        findNavController().navigate(R.id.action_manualScanFragment_to_contraindicationFragment, bundle)
                    }
                    else if (participantResource.data.data.trace_status!!.equals("Abnormal"))
                    {
                        ecgErrorDialog("Abnormal ECG", getString(R.string.ecg_error_complete))
                    }
                }
                else {
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

//        viewModel.participantEcg.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantRequest = participantResource.data?.data
//                participantRequest?.meta = meta
//                if (participantResource.data?.stationStatus!!)
//                {
//                    if (participantResource.data.statusCode == 100 && participantResource.data.is_cancelled == 0)
//                    {
//                        //set abnormal validation
//                        viewModel.setScreeningIdEcgTrace(binding.editTextCode.text.toString())
//                    }
//                    else
//                    {
//                        ecgErrorDialog1()
//                    }
//                }
//                else
//                {
//                    ecgErrorDialog1()
//                }
//            } else if (participantResource?.status == Status.ERROR) {
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
//                errorDialogFragment.show(fragmentManager!!)
//            }
//            binding.executePendingBindings()
//        })

//        viewModel.participantOffline?.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantRequest = participantResource.data
//                participantRequest?.meta = meta
//                val bundle = bundleOf("participant" to participantRequest)
//                findNavController().navigate(R.id.action_manualScanFragment_to_contraindicationFragment, bundle)
//            } else if (participantResource?.status == Status.ERROR) {
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage("The Paticipant ID is not found")
//                errorDialogFragment.show(fragmentManager!!)
//                //Crashlytics.logException(Exception(participantResource.toString()))
//            }
//            binding.executePendingBindings()
//        })

        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.editTextCode.requestFocus()

        viewModel.checkoutParticipant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS)
            {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta
                if (!participantResource.data?.stationStatus!!)
                {
                    viewModel.setScreeningId(binding.editTextCode.text.toString())
                }
                else
                {
                    if (participantResource.data.is_cancelled == 1)
                    {
                        viewModel.setScreeningId(binding.editTextCode.text.toString())
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

    fun handleContinue() {
        val checkSum = validateChecksum(binding.editTextCode.text.toString(), Constants.TYPE_PARTICIPANT)
        if (!checkSum.error) {
            activity?.runOnUiThread {

                if (isNetworkAvailable()) {
                    //viewModel.setScreeningIdEcg(binding.editTextCode.text.toString())
                    viewModel.setCheckoutScreeningId(binding.editTextCode.text.toString())
                } else {
                    //viewModel.setScreeningIdOffline(binding.editTextCode.text.toString())
                }
            }
        } else {

            binding.textLayoutCode.error = getString(R.string.invalid_code) //checkSum.message
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.hideKeyboard()
                navController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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
                    findNavController().navigate(R.id.action_manualScanFragment_to_contraindicationFragment, bundle)
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

}
