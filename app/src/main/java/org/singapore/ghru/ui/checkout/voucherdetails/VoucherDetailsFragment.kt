package org.singapore.ghru.ui.checkout.voucherdetails

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.VoucherDetailsFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.ui.checkout.voucherdetails.uniqeerror.UniqueVoucherDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.CheckoutRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.StudyList
import org.singapore.ghru.vo.request.VoucherRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class VoucherDetailsFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<VoucherDetailsFragmentBinding>()

    @Inject
    lateinit var viewModel: VoucherDetailsViewModel

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    private var voucherRequest: VoucherRequest? = null

    //var meta: Meta? = null

    var status_code: String = ""
    var voucher1:String? = null
    private var stList: StudyList? = null
    var amount:String? = null
    private val vouList:ArrayList<String> = arrayListOf()
    private var comment : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            //meta = arguments?.getParcelable<Meta>("Meta")!!
            stList = arguments?.getParcelable<StudyList>("Study_list")!!
            amount = arguments?.getString("Amount")
            comment = arguments?.getString("Comment")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<VoucherDetailsFragmentBinding>(
            inflater,
            R.layout.voucher_details_fragment,
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

//        if (voucherRequest != null)
//        {
//            binding.participant = voucherRequest
//        }


        binding.setLifecycleOwner(this)

        binding.voucher1Icon.singleClick {

            val bundle = bundleOf("ParticipantRequest" to participantRequest)
            findNavController().navigate(R.id.action_global_VoucherScanFragment, bundle)
        }

        binding.voucher2Icon.singleClick {

            val bundle = bundleOf("ParticipantRequest" to participantRequest)
            findNavController().navigate(R.id.action_global_VoucherScanOneFragment, bundle)
        }

        binding.voucher3Icon.singleClick {

            val bundle = bundleOf("ParticipantRequest" to participantRequest)
            findNavController().navigate(R.id.action_global_VoucherScanTwoFragment, bundle)
        }

        binding.voucher1EditText.setText(Prefs.getString("VoucherID", null))
        binding.voucher2EditText.setText(Prefs.getString("VoucherIDOne", null))
        binding.voucher3EditText.setText(Prefs.getString("VoucherIDTwo", null))
        //Prefs.clear()

        binding.buttonSubmit.singleClick {

            if (binding.voucher1EditText.text.toString() != "")
            {
                vouList.add(binding.voucher1EditText.text.toString())
            }

            if (binding.voucher2EditText.text.toString() != "")
            {
                vouList.add(binding.voucher2EditText.text.toString())
            }

            if (binding.voucher3EditText.text.toString() != "")
            {
                vouList.add(binding.voucher3EditText.text.toString())
            }

            if (participantRequest != null)
            {
                if (vouList.size > 0)
                {
                    if (validateNextButton())
                    {
                        Log.d("VOUCHER_DETAILS", "BEFORE_META")
                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime:String = endDate + " " + endTime

                        participantRequest?.meta?.endTime = endDateTime

//                    Log.d("VOUCHER_DETAILS", "AFTER_META_BEFORE_LIST")
//                    Log.d("VOUCHER_DETAILS", "AFTER_META_BEFORE_LIST")

                        var comment1 = comment + " " + binding.comment.text.toString()

                        val chkRequest = CheckoutRequest(meta = participantRequest?.meta, comment=comment1)
                        chkRequest.vouchers = vouList
                        chkRequest.studies = stList
                        chkRequest.study_amount = amount

                        chkRequest.screeningId = participantRequest?.screeningId!!
                        if(isNetworkAvailable()){
                            chkRequest.syncPending =false
                        }else{
                            chkRequest.syncPending =true
                        }

                        //Log.d("FFQ_CONFIRAMTION", "DATA:" + chkRequest)

                        Log.d("VOUCHER_DETAILS", "AFTER_REQUEST_BEFORE_SET_ID")

                        viewModel.setPostChk(chkRequest, participantRequest!!.screeningId)
                    }
                }
                else
                {
                    Toast.makeText(activity!!, "Please add at least one voucher", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.chkPostComplete?.observe(this, Observer { chkPocess ->

            if (chkPocess?.status == Status.SUCCESS)
            {
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                findNavController().navigate(R.id.action_global_PaymentCompletionFragment, bundle)
                Prefs.clear()
            }
            else if(chkPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "CheckoutRequest",
                    CheckoutRequest(meta = participantRequest?.meta, comment = "").toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + chkPocess.message.toString()))
            }
        })

        binding.buttonCancel.singleClick {

            navController().popBackStack()
        }

//        if (voucher1 != null)
//        {
//            binding.voucher1EditText.setText(voucher1)
//        }

        onTextChanges(binding.voucher1EditText)
        onTextChanges(binding.voucher2EditText)
        onTextChanges(binding.voucher3EditText)
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

    fun navController() = findNavController()

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.voucher1EditText)
                {
                    validateFirstVoucher()
                }

                if(editText == binding.voucher2EditText)
                {
                    validateSecondVoucher()
                }

                if(editText == binding.voucher3EditText)
                {
                    validateThirdVoucher()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun validateSecondVoucher():Boolean
    {
        if (!binding.voucher2EditText.text.toString().equals(""))
        {
            if (binding.voucher1EditText.text.toString() == binding.voucher2EditText.text.toString())
            {
                binding.voucher2Layout.error = getString(R.string.error_invalid_input)
                return false
            }

            if (binding.voucher2EditText.text.toString() == binding.voucher3EditText.text.toString())
            {
                binding.voucher2Layout.error = getString(R.string.error_invalid_input)
                return false
            }

            binding.voucher2Layout.error = null
            return true
        }
        else
        {
            binding.voucher2Layout.error = null
            return false
        }
    }

    private fun  validateFirstVoucher():Boolean
    {
        if (!binding.voucher1EditText.text.toString().equals(""))
        {
            if (binding.voucher1EditText.text.toString() == binding.voucher3EditText.text.toString()) {
                binding.voucher1Layout.error = getString(R.string.error_invalid_input)
                return false
            }

            if (binding.voucher1EditText.text.toString() == binding.voucher2EditText.text.toString()) {
                binding.voucher1Layout.error = getString(R.string.error_invalid_input)
                return false
            }

            binding.voucher1Layout.error = null
            return true
        }
        else
        {
            binding.voucher1Layout.error = null
            return false
        }

    }

    private fun validateThirdVoucher():Boolean
    {
        if (!binding.voucher3EditText.text.toString().equals(""))
        {
            if (binding.voucher1EditText.text.toString() == binding.voucher3EditText.text.toString())
            {
                binding.voucher3Layout.error = getString(R.string.error_invalid_input)
                return false
            }

            if (binding.voucher2EditText.text.toString() == binding.voucher3EditText.text.toString())
            {
                binding.voucher3Layout.error = getString(R.string.error_invalid_input)
                return false
            }

            binding.voucher3Layout.error = null
            return true
        }
        else
        {
            binding.voucher3Layout.error = null
            return true
        }
    }

    private fun validateNextButton():Boolean{

        val voucher1 = binding.voucher1EditText.text.toString()
        val voucher2 = binding.voucher2EditText.text.toString()
        val voucher3 = binding.voucher3EditText.text.toString()

        if (voucher1.equals("") && voucher2.equals("") && voucher3.equals(""))
        {
            val uniqueError = UniqueVoucherDialogFragment()
            uniqueError.arguments = bundleOf("Message" to "At least one voucher need to be added.")
            uniqueError.show(fragmentManager!!)
            //Toast.makeText(activity!!, "At least one voucher need to be added.", Toast.LENGTH_LONG).show()
            return false
        }
        else if (voucher1.equals("") && voucher2.equals("") && !voucher3.equals(""))
        {
            return true
        }
        else if (voucher1.equals("") && !voucher2.equals("") && voucher3.equals(""))
        {
            return true
        }
        else if (voucher1.equals("") && !voucher2.equals("") && !voucher3.equals(""))
        {
            if (voucher2 == voucher3)
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "Voucher two and three can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "Voucher two and three can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else
            {
                return true
            }
        }
        else if (!voucher1.equals("") && voucher2.equals("") && voucher3.equals(""))
        {
            return true
        }
        else if (!voucher1.equals("") && voucher2.equals("") && !voucher3.equals(""))
        {
            if (voucher1 == voucher3)
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "Voucher one and three can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "Voucher one and three can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else
            {
                return true
            }
        }
        else if (!voucher1.equals("") && !voucher2.equals("") && voucher3.equals(""))
        {
            if (voucher1 == voucher2)
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "Voucher one and two can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "Voucher one and two can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else
            {
                return true
            }
        }
        else if (!voucher1.equals("") && !voucher2.equals("") && !voucher3.equals(""))
        {
            if ((voucher1 == voucher2)&&( voucher2== voucher3))
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "All vouchers can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "All vouchers can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else if (voucher1 == voucher2)
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "Voucher one and two can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "Voucher one and two can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else if (voucher2 == voucher3)
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "Voucher two and three can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "Voucher three and two can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else if (voucher1 == voucher3)
            {
                val uniqueError = UniqueVoucherDialogFragment()
                uniqueError.arguments = bundleOf("Message" to "Voucher one and three can not be same.")
                uniqueError.show(fragmentManager!!)
                //Toast.makeText(activity!!, "Voucher one and three can not be same.", Toast.LENGTH_LONG).show()
                return false
            }
            else
            {
                return true
            }
        }

        return false
    }
}