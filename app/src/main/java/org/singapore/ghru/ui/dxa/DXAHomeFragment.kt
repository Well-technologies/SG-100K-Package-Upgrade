package org.singapore.ghru.ui.dxa

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.google.gson.GsonBuilder

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.DXAHomeFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.dxa.completed.CompletedDialogFragment
import org.singapore.ghru.ui.dxa.contra.DXAQuestionsViewModel
import org.singapore.ghru.ui.dxa.missingvalues.DXAMissingDialogFragment
import org.singapore.ghru.ui.dxa.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DXAHomeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<DXAHomeFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: DXAHomeViewModel

    private var participantRequest: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    private var whole_body: String? = null
    private var lumbar_spine: String? = null
    private var hip: String? = null
    private var hip_used: String? = null
    private var implants: String? = null
    private var surgery: String? = null
    //private var injury: String? = null

    private var isImplant : Boolean? = null
    private var isSurgery : Boolean? = null
    //private var isInjury : Boolean? = null

    @Inject
    lateinit var jobManager: JobManager

    private lateinit var questionnaireViewModel: DXAQuestionsViewModel
    var bodyMeasurementMeta: BodyMeasurementMetaNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            bodyMeasurementMeta = arguments?.getParcelable<BodyMeasurementMetaNew>("BodyMeasurementData")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<DXAHomeFragmentBinding>(
            inflater,
            R.layout.d_x_a_home_fragment,
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
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.participant = participantRequest
        binding.bodyMeasurement = bodyMeasurementMeta

        if ( bodyMeasurementMeta!!.body!!.height!!.value != null)
        {
            binding.heightTextView.setText(bodyMeasurementMeta!!.body!!.height!!.value!!.toString() + " " + bodyMeasurementMeta!!.body!!.height!!.unit!!.toString())
        }

        if ( bodyMeasurementMeta!!.body!!.bodyComposition!!.value != null)
        {
            binding.weightTextView.setText(bodyMeasurementMeta!!.body!!.bodyComposition!!.value!!.toString() + " " + bodyMeasurementMeta!!.body!!.bodyComposition!!.unit!!.toString())
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.DXA)
        viewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedDeviceID = null
                } else {
                    binding.textViewDeviceError.visibility = View.GONE
                    selectedDeviceID = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        //Log.d("DXA_GUIDE", "BODY_DATA: " + bodyMeasurementMeta!!.body!!.height!!.value)

        viewModel.dxaComplete?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {
                Crashlytics.setString("comment", binding.comment.text.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("dxaComplete " + participant.message.toString()))
                binding.executePendingBindings()
            }
        })

        Log.d("HOME_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        binding.nextButton.singleClick {
            if(selectedDeviceID==null) {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (validateDXA())
            {
                if (validateMissingValues())
                {
                    if (isImplant!! || isSurgery!!)
                    {
                        if (binding.comment.text.toString().equals(""))
                        {
                            Toast.makeText(activity!!, "Please add a comment", Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                            val endTime: String = convertTimeTo24Hours()
                            val endDate: String = getDate()
                            val endDateTime:String = endDate + " " + endTime

                            participantRequest?.meta?.endTime =  endDateTime

                            Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                            val mDXABody = DXABody(DXABodyData(binding.comment.text.toString(),
                                selectedDeviceID!!,whole_body!!, lumbar_spine!!, hip!!, hip_used!!,
                                implants, surgery))
                            mDXABody.contraindications = getContraindications()
                            val gson = GsonBuilder().setPrettyPrinting().create()
                            viewModel.setParticipantComplete(
                                participantRequest!!, isNetworkAvailable(),
                                gson.toJson(mDXABody)
                            )
                        }
                    }
                    else
                    {
                        Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime:String = endDate + " " + endTime

                        participantRequest?.meta?.endTime =  endDateTime

                        Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                        val mDXABody = DXABody(DXABodyData(binding.comment.text.toString(),
                            selectedDeviceID!!,whole_body!!, lumbar_spine!!, hip!!, hip_used!!,
                            implants, surgery))
                        mDXABody.contraindications = getContraindications()
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        viewModel.setParticipantComplete(
                            participantRequest!!, isNetworkAvailable(),
                            gson.toJson(mDXABody)
                        )
                    }
                }
                else
                {
                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime =  endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

//                    val dxaBodyData = (DXABodyData(binding.comment.text.toString(),
//                        selectedDeviceID!!,whole_body!!, lumbar_spine!!, hip!!, hip_used!!,
//                        implants, surgery))

                    val missingDialogFragment = DXAMissingDialogFragment()
//                    missingDialogFragment.arguments = bundleOf(
//                        "participant" to participantRequest,
//                        "isNetwork" to isNetworkAvailable(),
//                        "dxaBodyData" to dxaBodyData,
//                        "contraindications" to getContraindications())
                    missingDialogFragment.show(fragmentManager!!)
                }
            }
        }

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("ParticipantRequest" to participantRequest, "contraindications" to getContraindications())
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.radioGroupWholeBody.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesWholeBody) {
                whole_body = "yes"
                binding.radioGroupWholeBodyValue = false;
            } else {
                whole_body = "no"
                binding.radioGroupWholeBodyValue = false;
            }
            binding.executePendingBindings()
        }
        binding.radioGroupLumbar.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesLumbar) {
                lumbar_spine = "yes"
                binding.radioGroupLumbarValue = false
            } else {
                lumbar_spine = "no"
                binding.radioGroupLumbarValue = false
            }
            binding.executePendingBindings()
        }
        binding.radioGroupHip.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesHip) {
                hip = "yes"
                binding.radioGroupHipValue = false
                binding.radioGroupHipUsed.visibility = View.VISIBLE
                binding.textHipUsed.visibility = View.VISIBLE
            } else {
                hip = "no"
                hip_used = "NA"
                binding.radioGroupHipUsed.visibility = View.GONE
                binding.textHipUsed.visibility = View.GONE
                binding.radioGroupHipValue = false
            }
            binding.executePendingBindings()
        }
        binding.radioGroupHipUsed.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.leftHipUsed) {
                hip_used = "left"
                binding.radioGroupHipUsedValue = false
            } else {
                hip_used = "right"
                binding.radioGroupHipUsedValue = false
            }
            binding.executePendingBindings()
        }

        binding.radioGroupImplant.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesImplant) {
                implants = "yes"
                binding.radioGroupImplantValue = false
                isImplant = true
            } else {
                implants = "no"
                binding.radioGroupImplantValue = false
                isImplant = false
            }
            binding.executePendingBindings()
        }
        binding.radioGroupSurgery.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesSurgery) {
                surgery = "yes"
                binding.radioGroupSurgeryValue = false
                isSurgery = true
            } else {
                surgery = "no"
                binding.radioGroupSurgeryValue = false
                isSurgery = false
            }
            binding.executePendingBindings()
        }
//        binding.radioGroupInjury.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.yesInjury) {
//                injury = "yes"
//                binding.radioGroupInjuryValue = false
//                isInjury = true
//            } else {
//                injury = "no"
//                binding.radioGroupInjuryValue = false
//                isInjury = false
//            }
//            binding.executePendingBindings()
//        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun validateDXA(): Boolean {
        if(whole_body == null) {
            binding.radioGroupWholeBodyValue = true
            binding.executePendingBindings()
            return false

        }
        else if(lumbar_spine == null) {
            binding.radioGroupLumbarValue = true
            binding.executePendingBindings()
            return false
        }
        else if(hip == null) {
            binding.radioGroupHipValue = true
            binding.executePendingBindings()
            return false
        }
        else if(hip_used == null) {
            binding.radioGroupHipUsedValue = true
            binding.executePendingBindings()
            return false
        }
        else if(implants == null)
        {
            binding.radioGroupImplantValue = true
            binding.executePendingBindings()
            return false
        }
        else if(surgery == null) {
            binding.radioGroupSurgeryValue = true
            binding.executePendingBindings()
            return false
        }
//        else if(injury == null) {
//            binding.radioGroupInjuryValue = true
//            binding.executePendingBindings()
//            return false
//        }

        return true
    }

    private fun validateMissingValues(): Boolean {
        if(whole_body == "no" && lumbar_spine == "no" && hip == "no")
        {
            return false
        }

        return true
    }

    private fun convertTimeTo24Hours(): String {
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

    private fun getDate(): String {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(DXAQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>>
    {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadXray = questionnaireViewModel.hadXray.value

        var xrayMap = mutableMapOf<String, String>()
        xrayMap["id"] = "DCI1"
        xrayMap["question"] = getString(R.string.dxa_xray_question)
        xrayMap["answer"] = if (hadXray!!) "yes" else "no"

        contraindications.add(xrayMap)

        return contraindications
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
