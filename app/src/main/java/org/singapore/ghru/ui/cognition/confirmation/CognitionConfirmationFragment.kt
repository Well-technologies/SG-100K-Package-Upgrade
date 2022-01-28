package org.singapore.ghru.ui.cognition.confirmation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.CognitionConfirmationFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.ui.cognition.contraindication.CognitionQuestionnaireViewModel
import org.singapore.ghru.ui.cognition.reason.ReasonDialogFragment
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class CognitionConfirmationFragment : Fragment() , Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<CognitionConfirmationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: CognitionConfirmationViewModel

    @Inject
    lateinit var questionnaireViewModel: CognitionQuestionnaireViewModel

    private var participantRequest: ParticipantRequest? = null

    private var cogData: CognitionDataNew? = null

    //var meta: Meta? = null
    //var user: User? = null

    private var assistanceRequired: Boolean? = false
    private var cognitionCompleted: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CognitionConfirmationFragmentBinding>(
            inflater,
            R.layout.cognition_confirmation_fragment,
            container,
            false
        )
        binding = dataBinding

        //setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(CognitionQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participantRequest
        binding.viewModel = viewModel

//        viewModel.setUser("user")
//        viewModel.user?.observe(this, Observer {
//
//                userData ->
//            if (userData?.data != null) {
//                user = userData.data
//
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime:String = sDate + " " + sTime
//
//                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
//            }
//        })

        viewModel.cogUpdateComplete?.observe(this, Observer { cognitionProcess ->

            if(cognitionProcess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
            }

            if (cognitionProcess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(cognitionProcess?.status == Status.ERROR){
                Crashlytics.setString(
                    "CognitionMeasurementMeta",
                    CognitionRequestNew(meta = participantRequest?.meta, body = cogData, status = "10").toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + cognitionProcess.message.toString()))
            }
        })

        binding.buttonSubmit.singleClick {
            if (validateNextButton()) {

                cogData = CognitionDataNew(
                    cognition_completed = cognitionCompleted!!,
                    assistance_required = assistanceRequired!!
                )
                //cogData!!.contraindications = getContraindications()

                Log.d("CONFIRM_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                Log.d("CONFIRM_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val cogRequest = CognitionRequestNew(meta = participantRequest?.meta, body = cogData, status = "10")
                cogRequest.screeningId = participantRequest?.screeningId!!
                if(isNetworkAvailable()){
                    cogRequest.syncPending =false
                }else{
                    cogRequest.syncPending =true
                }

                Log.d("FFQ_CONFIRAMTION", "DATA:" + cogRequest)

                viewModel.setUpdateCog(cogRequest, participantRequest?.screeningId)

                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE

            }
            else
            {
                binding.executePendingBindings()

            }
        }

        binding.buttonCancel.singleClick{
            val reasonDialogFragment = ReasonDialogFragment()
            //reasonDialogFragment.arguments = bundleOf("participant" to participantRequest, "contraindications" to getContraindications())
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.radioGroupStaff.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noStaff) {
                binding.radioGroupStaffValue = false
                viewModel.setHaveStaff(false)
                cognitionCompleted = false

            }
            else {
                binding.radioGroupStaffValue = false
                viewModel.setHaveStaff(true)
                cognitionCompleted = true
            }
            binding.executePendingBindings()
        }

        binding.radioGroupAssistance.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noAssistance) {
                binding.radioGroupAssistanceValue = false
                viewModel.setHaveAssistance(false)
                assistanceRequired = false

            } else {
                binding.radioGroupAssistanceValue = false
                viewModel.setHaveAssistance(true)
                assistanceRequired = true
            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {

        if (viewModel.haveStaff.value == null)
        {
            binding.radioGroupStaffValue = true
            binding.executePendingBindings()
            return false
        }

        if (viewModel.haveAssistance.value == null)
        {
            binding.radioGroupAssistanceValue = true
            binding.executePendingBindings()
            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

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

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications = mutableListOf<Map<String, String>>()

        val haveCommunicate = questionnaireViewModel.haveAbleToClick.value
        val communicationMap = mutableMapOf<String, String>()
        communicationMap["id"] = "COCI1"
        communicationMap["question"] = "Is participant able to click mouse on PC?"
        communicationMap["answer"] = if (haveCommunicate!!) "yes" else "no"

        contraindications.add(communicationMap)

        return contraindications
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
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

}
