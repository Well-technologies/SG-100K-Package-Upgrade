package org.singapore.ghru.ui.vicorder.stationquestion


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.google.gson.GsonBuilder
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.FundosReadingBinding
import org.singapore.ghru.databinding.StationQuestionFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.fundoscopy.questions.FundoscopyQuestionsViewModel
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.vicorder.reason.ReasonDialogFragment
import org.singapore.ghru.ui.vicorder.contra.VicorderQuestionsViewModel
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class StationQuestionFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors


    var binding by autoCleared<StationQuestionFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: StationQuestionViewModel

    private var participant: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    @Inject
    lateinit var jobManager: JobManager

    private lateinit var questionnaireViewModel: VicorderQuestionsViewModel
    var bodyMeasurementMeta: BodyMeasurementMetaNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            bodyMeasurementMeta = arguments?.getParcelable<BodyMeasurementMetaNew>("BodyMeasurementData")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<StationQuestionFragmentBinding>(
            inflater,
            R.layout.station_question_fragment,
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
        binding.participant = participant
        binding.bodyMeasurement = bodyMeasurementMeta

        if ( bodyMeasurementMeta!!.body!!.height!!.value != null)
        {
            binding.heightTextView.setText(bodyMeasurementMeta!!.body!!.height!!.value!!.toString() + " " + bodyMeasurementMeta!!.body!!.height!!.unit!!.toString())
        }

        if ( bodyMeasurementMeta!!.body!!.bodyComposition!!.value != null)
        {
            binding.weightTextView.setText(bodyMeasurementMeta!!.body!!.bodyComposition!!.value!!.toString() + " " + bodyMeasurementMeta!!.body!!.bodyComposition!!.unit!!.toString())
        }

        viewModel.vicPostComplete?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {
                Crashlytics.setString("comment", binding.comment.text.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("fundoscopyComplete " + participant.message.toString()))
                binding.executePendingBindings()
            }
        })

        Log.d("READING_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        binding.nextButton.singleClick {
            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (validateStationQuestions())
            {
                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participant?.meta?.endTime = endDateTime

                val vicRequest = VicorderRequest(device_id = selectedDeviceID, comment=binding.comment.text.toString(), meta = participant?.meta)
                vicRequest.screeningId = participant?.screeningId!!
                vicRequest.contraindications = getContraindications()
                vicRequest.stationQuestions = getConfirmQuestions()

                if(isNetworkAvailable()){
                    vicRequest.syncPending =false
                }else{
                    vicRequest.syncPending =true
                }

                viewModel.setPostVic(vicRequest, participant!!.screeningId)

            }
        }
        binding.buttonCancel.singleClick {

            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "ParticipantRequest" to participant,
                "contraindications" to getContraindications(),
                "comment" to binding.comment.text.toString(),
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter_Device_list = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter_Device_list);

        viewModel.setStationName(Measurements.Vicorder)
        viewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter_Device_list.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
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

        binding.radioGroupArmCuffPlacement.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.leftArmCuffPlacement)
            {
                binding.radioGroupArmCuffPlacementValue = false
                viewModel.setHaveACP("left")
            }
            else
            {
                binding.radioGroupArmCuffPlacementValue = false
                viewModel.setHaveACP("right")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupArmCuffSize.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.acsArmCuffSize)
            {
                binding.radioGroupArmCuffSizeValue = false
                viewModel.setHaveACS("acs")
            }
            else if (radioGroup.checkedRadioButtonId == R.id.acmArmCuffSize)
            {
                binding.radioGroupArmCuffSizeValue = false
                viewModel.setHaveACS("acm")
            }
            else if(radioGroup.checkedRadioButtonId == R.id.aclArmCuffSize)
            {
                binding.radioGroupArmCuffSizeValue = false
                viewModel.setHaveACS("acl")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupThighCuffPlacement.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.leftThighCuffPlacement)
            {
                binding.radioGroupThighCuffPlacementValue = false
                viewModel.setHaveTCP("left")
            }
            else
            {
                binding.radioGroupThighCuffPlacementValue = false
                viewModel.setHaveTCP("right")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupThighCuffSize.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.tcsThighCuffSize)
            {
                binding.radioGroupThighCuffSizeValue = false
                viewModel.setHaveTCS("tcs")
            }
            else if (radioGroup.checkedRadioButtonId == R.id.tcmThighCuffSize)
            {
                binding.radioGroupThighCuffSizeValue = false
                viewModel.setHaveTCS("tcm")
            }
            else if(radioGroup.checkedRadioButtonId == R.id.tclThighCuffSize)
            {
                binding.radioGroupThighCuffSizeValue = false
                viewModel.setHaveTCS("tcl")
            }
            binding.executePendingBindings()

        }

        setDefaultValues()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(VicorderQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadFistula = questionnaireViewModel.hadFistula.value
        val haveMastectomy = questionnaireViewModel.haveMastectomy.value
        val haveLymph = questionnaireViewModel.hadLymph.value
        val haveTrauma = questionnaireViewModel.haveTrauma.value

        var FistulaMap = mutableMapOf<String, String>()
        FistulaMap["id"] = "VICI1"
        FistulaMap["question"] = getString(R.string.vicorder_fistula)
        FistulaMap["answer"] = hadFistula!!

        contraindications.add(FistulaMap)

        var mastectomyMap = mutableMapOf<String, String>()
        mastectomyMap["id"] = "VICI2"
        mastectomyMap["question"] = getString(R.string.vicorder_mastectomy)
        mastectomyMap["answer"] = haveMastectomy!!

        contraindications.add(mastectomyMap)

        var lymphMap = mutableMapOf<String, String>()
        lymphMap["id"] = "VICI3"
        lymphMap["question"] = getString(R.string.vicorder_lymph)
        lymphMap["answer"] = haveLymph!!

        contraindications.add(lymphMap)

        var traumaMap = mutableMapOf<String, String>()
        traumaMap["id"] = "VICI4"
        traumaMap["question"] = getString(R.string.vicorder_trauma)
        traumaMap["answer"] = haveTrauma!!

        contraindications.add(traumaMap)

        return contraindications
    }

    private fun getConfirmQuestions(): MutableList<Map<String, String>> {
        var questions: MutableList<Map<String, String>> = mutableListOf()

        val haveACP = viewModel.haveACP.value
        val haveACS = viewModel.haveACS.value
        val haveTCP = viewModel.haveTCP.value
        val haveTCS = viewModel.haveTCS.value

        var acpMap = mutableMapOf<String, String>()
        acpMap["id"] = "VISQ1"
        acpMap["question"] = getString(R.string.vicorder_arm_cuff_placement)
        acpMap["answer"] = haveACP!!

        questions.add(acpMap)

        var acsMap = mutableMapOf<String, String>()
        acsMap["id"] = "VISQ2"
        acsMap["question"] = getString(R.string.vicorder_arm_cuff_size)
        acsMap["answer"] = haveACS!!

        questions.add(acsMap)

        var tcpMap = mutableMapOf<String, String>()
        tcpMap["id"] = "VISQ3"
        tcpMap["question"] = getString(R.string.vicorder_thigh_cuff_placement)
        tcpMap["answer"] = haveTCP!!

        questions.add(tcpMap)

        var tcsMap = mutableMapOf<String, String>()
        tcsMap["id"] = "VISQ4"
        tcsMap["question"] = getString(R.string.vicorder_thigh_cuff_size)
        tcsMap["answer"] = haveTCS!!

        questions.add(tcsMap)

        return questions
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    private fun validateStationQuestions(): Boolean
    {
        if(viewModel.haveACP.value == null)
        {
            binding.radioGroupArmCuffPlacementValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveACS.value == null)
        {
            binding.radioGroupArmCuffSizeValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveTCP.value == null)
        {
            binding.radioGroupThighCuffPlacementValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveTCS.value == null)
        {
            binding.radioGroupThighCuffSizeValue = true
            binding.executePendingBindings()
            return false
        }
        else
        {
            return true
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

    private fun setDefaultValues()
    {
        val hadFistula = questionnaireViewModel.hadFistula.value
        val haveMastectomy = questionnaireViewModel.haveMastectomy.value
        val hadLymph = questionnaireViewModel.hadLymph.value
        val haveTrauma = questionnaireViewModel.haveTrauma.value

        if (hadFistula == "right" || haveMastectomy == "right" || hadLymph == "right" || haveTrauma == "right")
        {
            binding.leftArmCuffPlacement.isChecked = true
            binding.leftThighCuffPlacement.isChecked = true
        }
        else
        {
            binding.rightArmCuffPlacement.isChecked = true
            binding.rightThighCuffPlacement.isChecked = true
        }

        binding.acmArmCuffSize.isChecked = true
        binding.tcmThighCuffSize.isChecked = true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
