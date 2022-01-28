package org.singapore.ghru.ui.octa.reading


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
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.OctaReadingBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.octa.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.octa.reading.reason.ReasonDialogFragment
import org.singapore.ghru.ui.octa.contra.OctaQuestionsViewModel
import org.singapore.ghru.ui.ultrasound.missingvalue.UltraMissingDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.OctaRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class OctaReadingFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors


    var binding by autoCleared<OctaReadingBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: OctaReadingViewModel

    private var participant: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    @Inject
    lateinit var jobManager: JobManager

    private lateinit var questionnaireViewModel: OctaQuestionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<OctaReadingBinding>(
            inflater,
            R.layout.octa_reading,
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

        viewModel.octaPostComplete?.observe(this, Observer { participant ->

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
            //print(participant.toString())
            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (validateFundoscopy())
            {
                if (validateMissingValues())
                {
                    Log.d("READING_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participant?.meta?.endTime =  endDateTime

                    Log.d("READING_FRAG", "AFTER_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                    val octaRequest = OctaRequest(device_id = selectedDeviceID, comment=binding.comment.text.toString(), meta = participant?.meta)
                    octaRequest.screeningId = participant?.screeningId!!
                    octaRequest.contraindications = getContraindications()
                    octaRequest.stationQuestions = getConfirmQuestions()

                    if(isNetworkAvailable()){
                        octaRequest.syncPending =false
                    }
                    viewModel.setPostOcta(octaRequest, participant!!.screeningId)
                }
                else
                {
                    val missingDialogFragment = UltraMissingDialogFragment()
                    missingDialogFragment.show(fragmentManager!!)
                }
            }
        }
        binding.buttonCancel.singleClick {

            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participant,
                "contraindications" to getContraindications(),
                "skipped" to false,
                "comment" to binding.comment.text.toString())
            reasonDialogFragment.show(fragmentManager!!)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter_Device_list = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter_Device_list);

        viewModel.setStationName(Measurements.Octa)
        //viewModel.setStationName(Measurements.DXA)
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

        binding.radioGroupSmallPupil.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.smallPupilLeft) {
                viewModel.setHaveObsveed("Left")
                binding.radioGroupSmallPupilValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.smallPupilRight) {
                viewModel.setHaveObsveed("Right")
                binding.radioGroupSmallPupilValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.smallPupilBoth) {
                viewModel.setHaveObsveed("Both")
                binding.radioGroupSmallPupilValue = false
            } else {
                viewModel.setHaveObsveed("No")
                binding.radioGroupSmallPupilValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupParticipantMovement.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.participantMovementLeft) {
                viewModel.setHaveMovement("Left")
                binding.radioGroupParticipantMovementValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.participantMovementRight) {
                viewModel.setHaveMovement("Right")
                binding.radioGroupParticipantMovementValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.participantMovementBoth) {
                viewModel.setHaveMovement("Both")
                binding.radioGroupParticipantMovementValue = false
            } else {
                viewModel.setHaveMovement("No")
                binding.radioGroupParticipantMovementValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupEyeHistory.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.eyeHistoryLeft) {
                viewModel.setHaveSurgery("Left")
                binding.radioGroupEyeHistoryValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.eyeHistoryRight) {
                viewModel.setHaveSurgery("Right")
                binding.radioGroupEyeHistoryValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.eyeHistoryBoth) {
                viewModel.setHaveSurgery("Both")
                binding.radioGroupEyeHistoryValue = false
            } else {
                viewModel.setHaveSurgery("No")
                binding.radioGroupEyeHistoryValue = false
            }
            binding.executePendingBindings()
        }

        binding.radioGroupPossibleCataract.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.possibleCataractYes) {
                viewModel.setHaveCataract("Yes")
                binding.radioGroupPossibleCataractValue = false
            }
            else
            {
                viewModel.setHaveCataract("No")
                binding.radioGroupPossibleCataractValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupExport.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noExport) {
                binding.radioGroupExportValue = false
                viewModel.setHaveExport("No")

            } else {
                binding.radioGroupExportValue = false
                viewModel.setHaveExport("Yes")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupDialation.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noDialation) {
                binding.radioGroupDialtionValue = false
                viewModel.setHaveDilatation("No")

            } else {
                binding.radioGroupDialtionValue = false
                viewModel.setHaveDilatation("Yes")
            }
            binding.executePendingBindings()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(OctaQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadSurgery = questionnaireViewModel.hadSurgery.value
        val havePain = questionnaireViewModel.haveEyePain.value
        val haveEyeRedness = questionnaireViewModel.hadEyeRedness.value

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = getString(R.string.octa_surgery_question_id_1)
        surgeryMap["question"] = getString(R.string.octa_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var painMap = mutableMapOf<String, String>()
        painMap["id"] = getString(R.string.octa_surgery_question_id_2)
        painMap["question"] = getString(R.string.funduscopy_symptoms_question)
        painMap["answer"] = havePain!!

        contraindications.add(painMap)

        var rednessMap = mutableMapOf<String, String>()
        rednessMap["id"] = getString(R.string.octa_surgery_question_id_3)
        rednessMap["question"] = getString(R.string.funduscopy_redness_question)
        rednessMap["answer"] = haveEyeRedness!!

        contraindications.add(rednessMap)

        return contraindications
    }

    private fun getConfirmQuestions(): MutableList<Map<String, String>> {
        var questions: MutableList<Map<String, String>> = mutableListOf()

        val haveExport = viewModel.haveExport.value
        val haveDilatation = viewModel.haveDilatation.value
        val haveObserved = viewModel.haveObsveed.value
        val haveMovement = viewModel.haveMovement.value
        val haveSurgery = viewModel.haveSurgery.value
        val haveCataract = viewModel.haveCataract.value

        var exportMap = mutableMapOf<String, String>()
        exportMap["id"] = getString(R.string.octa_station_question_id_1)
        exportMap["question"] = getString(R.string.octa_que_exported)
        exportMap["answer"] = haveExport!!

        questions.add(exportMap)

        var dilatationMap = mutableMapOf<String, String>()
        dilatationMap["id"] = getString(R.string.octa_station_question_id_2)
        dilatationMap["question"] = getString(R.string.fundo_que_dialation)
        dilatationMap["answer"] = haveDilatation!!

        questions.add(dilatationMap)

        var observedMap = mutableMapOf<String, String>()
        observedMap["id"] = getString(R.string.octa_station_question_id_3)
        observedMap["question"] = getString(R.string.small_pupil_observed)
        observedMap["answer"] = haveObserved!!

        questions.add(observedMap)

        var movementMap = mutableMapOf<String, String>()
        movementMap["id"] = getString(R.string.octa_station_question_id_4)
        movementMap["question"] = getString(R.string.particiant_movement)
        movementMap["answer"] = haveMovement!!

        questions.add(movementMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = getString(R.string.octa_station_question_id_5)
        surgeryMap["question"] = getString(R.string.history_of_eye)
        surgeryMap["answer"] = haveSurgery!!

        questions.add(surgeryMap)

        var cataractMap = mutableMapOf<String, String>()
        cataractMap["id"] = getString(R.string.octa_station_question_id_5)
        cataractMap["question"] = getString(R.string.possible_cataract)
        cataractMap["answer"] = haveCataract!!

        questions.add(cataractMap)

        return questions
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    private fun validateFundoscopy(): Boolean {
        if(viewModel.haveExport.value == null)
        {
            binding.radioGroupExportValue = true
            binding.executePendingBindings()
            return false

        }
        else if(viewModel.haveDilatation.value == null)
        {
            binding.radioGroupDialtionValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveObsveed.value == null)
        {
            binding.radioGroupSmallPupilValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveMovement.value == null)
        {
            binding.radioGroupParticipantMovementValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveSurgery.value == null)
        {
            binding.radioGroupEyeHistoryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveCataract.value == null)
        {
            binding.radioGroupPossibleCataractValue = true
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

    private fun validateMissingValues(): Boolean
    {
        if(viewModel.haveExport.value == "No")
        {
            return false
        }
        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
