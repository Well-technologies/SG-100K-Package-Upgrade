package org.singapore.ghru.ui.checkout.paymentinformation.giroconfirmation

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.CheckoutCompletionFragmentBinding
import org.singapore.ghru.databinding.GiroConfirmationFragmentBinding
import org.singapore.ghru.databinding.ManualEntryBodyMeasurementFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.checkout.alreadycheckout.AlreadyCheckoutDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.statuscheck.StatusCheckDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.User
import org.singapore.ghru.vo.request.CheckoutRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.StudyList
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class GiroConfirmationFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<GiroConfirmationFragmentBinding>()

    @Inject
    lateinit var viewModel: GiroConfirmationViewModel

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    //var meta: Meta? = null
    //var user: User? = null
    var prefs : SharedPreferences? = null
    private var stList: StudyList? = null
    var amount:String? = null

    var status_code: String = ""
    var comment : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //meta = arguments?.getParcelable<Meta>("meta")!!
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            //meta = arguments?.getParcelable<Meta>("Meta")!!
            stList = arguments?.getParcelable<StudyList>("Study_list")!!
            amount = arguments?.getString("Amount")
            comment = arguments?.getString("Comment")
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<GiroConfirmationFragmentBinding>(
            inflater,
            R.layout.giro_confirmation_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        //appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

//        viewModel.setUser("user")
//        viewModel.user?.observe(this, Observer {
//
//                userData ->
//            if (userData?.data != null)
//            {
//                user = userData.data
//
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime:String = sDate + " " + sTime
//
//                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
//                //meta?.registeredBy = user?.id
//            }
//        })

        binding.backButton.singleClick {

            navController().popBackStack()
        }

        viewModel.chkPostComplete?.observe(this, Observer { chkPocess ->

            if (chkPocess?.status == Status.SUCCESS)
            {
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                findNavController().navigate(R.id.action_global_PaymentCompletionFragment, bundle)
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

        binding.confirmButton.singleClick {

            if (participantRequest != null)
            {

                Log.d("VOUCHER_DETAILS", "BEFORE_META")
                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                Log.d("VOUCHER_DETAILS", "AFTER_META_BEFORE_LIST")

                val chkRequest = CheckoutRequest(meta = participantRequest?.meta, comment=comment)
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
}