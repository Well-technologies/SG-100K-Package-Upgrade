package org.singapore.ghru.ui.fundoscopy.reading


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
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.FundosReadingBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.fundoscopy.questions.FundoscopyQuestionsViewModel
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.fundoscopy.reading.missingvalues.FundoMissingDialogFragment
import org.singapore.ghru.ui.fundoscopy.reading.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class FundoscopyReadingFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors


    var binding by autoCleared<FundosReadingBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var fundoscopyReadingViewModel: FundoscopyReadingViewModel

    private var participant: ParticipantRequest? = null

    private var adapter by autoCleared<AssetAdapter>()

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    @Inject
    lateinit var jobManager: JobManager

    //private var didDilation: Boolean? = null
    //private var cataractObservation : String = ""
    private var smallPupil : String = ""
    private var participantMovement : String = ""
    private var eyeHistory : String = ""
    private var eyeMissing : String = ""
    private var possiblecataract : String = ""

    private lateinit var questionnaireViewModel: FundoscopyQuestionsViewModel

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
        val dataBinding = DataBindingUtil.inflate<FundosReadingBinding>(
            inflater,
            R.layout.fundos_reading,
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
        binding.viewModel = fundoscopyReadingViewModel
        binding.participant = participant

        val adapter = AssetAdapter(dataBindingComponent, appExecutors) { homeemumerationlistItem ->

        }

        this.adapter = adapter
        binding.assetList.adapter = adapter
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.assetList.setLayoutManager(layoutManager)
        fundoscopyReadingViewModel.asserts?.observe(this, Observer { assertsResource ->
            if (assertsResource?.status == Status.SUCCESS) {
                println(assertsResource.data?.data)
                if (assertsResource.data != null) {
                    adapter.submitList(assertsResource.data.data)
                    binding.icSync.visibility = View.GONE
                    binding.icText.visibility = View.GONE

                } else {
                    adapter.submitList(emptyList())
                    binding.icSync.visibility = View.VISIBLE
                    binding.icText.visibility = View.VISIBLE
                }
            }
        })
        binding.syncLayout.singleClick {
            fundoscopyReadingViewModel.setParticipant(
                participant!!,
                binding.comment.text.toString(),
                selectedDeviceID!!
            )

        }

        fundoscopyReadingViewModel.fundoscopyComplete?.observe(this, Observer { participant ->

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
                Log.d("READING_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participant?.meta?.endTime =  endDateTime

                if (validateMandatoryValues())
                {
                    Log.d("READING_FRAG", "AFTER_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)
                    fundoscopyReadingViewModel.setParticipantComplete(
                        participant!!,
                        binding.comment.text.toString(),
                        selectedDeviceID!!,
                        isNetworkAvailable(),
                        getContraindications(),
                        getConfirmQuestions()
                    )
                }
                else
                {
                    val missingDialogFragment = FundoMissingDialogFragment()
//                    missingDialogFragment.arguments = bundleOf(
//                        "is_cancel" to false,
//                        "participant" to participant,
//                        "comment" to binding.comment.text.toString(),
//                        "isNetwork" to isNetworkAvailable(),
//                        "contraindications" to getContraindications(),
//                        "questions" to getConfirmQuestions(),
//                        "selectedDeviceID" to selectedDeviceID)
                    missingDialogFragment.show(fragmentManager!!)
                }
            }
        }
        binding.buttonCancel.singleClick {

            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participant,
                "contraindications" to getContraindications(),
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter_Device_list = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter_Device_list);

        fundoscopyReadingViewModel.setStationName(Measurements.FUNDOSCOPY)
        //viewModel.setStationName(Measurements.DXA)
        fundoscopyReadingViewModel.stationDeviceList?.observe(this, Observer {
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
                smallPupil = "Left"
                binding.radioGroupSmallPupilValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.smallPupilRight) {
                smallPupil = "Right"
                binding.radioGroupSmallPupilValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.smallPupilBoth) {
                smallPupil = "Both"
                binding.radioGroupSmallPupilValue = false
            } else {
                smallPupil = "No"
                binding.radioGroupSmallPupilValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupParticipantMovement.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.participantMovementLeft) {
                participantMovement = "Left"
                binding.radioGroupParticipantMovementValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.participantMovementRight) {
                participantMovement = "Right"
                binding.radioGroupParticipantMovementValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.participantMovementBoth) {
                participantMovement = "Both"
                binding.radioGroupParticipantMovementValue = false
            } else {
                participantMovement = "No"
                binding.radioGroupParticipantMovementValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupEyeHistory.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.eyeHistoryLeft) {
                eyeHistory = "Left"
                binding.radioGroupEyeHistoryValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.eyeHistoryRight) {
                eyeHistory = "Right"
                binding.radioGroupEyeHistoryValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.eyeHistoryBoth) {
                eyeHistory = "Both"
                binding.radioGroupEyeHistoryValue = false
            } else {
                eyeHistory = "No"
                binding.radioGroupEyeHistoryValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupEyeMissing.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.eyeMissingLeft) {
                eyeMissing = "Left"
                binding.radioGroupEyeMissingValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.eyeMissingRight) {
                eyeMissing = "Right"
                binding.radioGroupEyeMissingValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.eyeMissingBoth) {
                eyeMissing = "Both"
                binding.radioGroupEyeMissingValue = false
            } else {
                eyeMissing = "No"
                binding.radioGroupEyeMissingValue = false
            }
            binding.executePendingBindings()
        }

        binding.radioGroupPossibleCataract.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.possibleCataractLeft) {
                possiblecataract = "Left"
                binding.radioGroupPossibleCataractValue = false
            } else if (radioGroup.checkedRadioButtonId == R.id.possibleCataractRight) {
                possiblecataract = "Right"
                binding.radioGroupPossibleCataractValue = false
            } else if(radioGroup.checkedRadioButtonId == R.id.possibleCataractBoth) {
                possiblecataract = "Both"
                binding.radioGroupPossibleCataractValue = false
            } else {
                possiblecataract = "No"
                binding.radioGroupPossibleCataractValue = false
            }
            binding.executePendingBindings()

        }

        binding.radioGroupLeftEye.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noLeftEye) {
                binding.radioGroupLeftEyeValue = false
                fundoscopyReadingViewModel.setHaveLeftEye(false)

            } else {
                binding.radioGroupLeftEyeValue = false
                fundoscopyReadingViewModel.setHaveLeftEye(true)
            }
            binding.executePendingBindings()
        }

        binding.radioGroupRightEye.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noRightEye) {
                binding.radioGroupRightEyeValue = false
                fundoscopyReadingViewModel.setHaveRightEye(false)

            } else {
                binding.radioGroupRightEyeValue = false
                fundoscopyReadingViewModel.setHaveRightEye(true)
            }
            binding.executePendingBindings()
        }

        binding.radioGroupExport.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noExport) {
                binding.radioGroupExportValue = false
                fundoscopyReadingViewModel.setHaveExported(false)

            } else {
                binding.radioGroupExportValue = false
                fundoscopyReadingViewModel.setHaveExported(true)
            }
            binding.executePendingBindings()
        }

        binding.radioGroupDialation.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noDialation) {
                binding.radioGroupDialtionValue = false
                fundoscopyReadingViewModel.setHaveDialtion(false)

            } else {
                binding.radioGroupDialtionValue = false
                fundoscopyReadingViewModel.setHaveDialtion(true)
            }
            binding.executePendingBindings()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(FundoscopyQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadSurgery = questionnaireViewModel.hadSurgery.value
        val haveSymptoms = questionnaireViewModel.haveEyePain.value
        val haveEyeRedness = questionnaireViewModel.hadEyeRedness.value
//        val haveBlurring = questionnaireViewModel.haveBlurring.value
//        val haveDouble = questionnaireViewModel.haveDouble.value

        var symptomsMap = mutableMapOf<String, String>()
        symptomsMap["id"] = "FCI2"
        symptomsMap["question"] = getString(R.string.funduscopy_symptoms_question)
        symptomsMap["answer"] = haveSymptoms!!

        contraindications.add(symptomsMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = "FCI1"
        surgeryMap["question"] = getString(R.string.funduscopy_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var rednessMap = mutableMapOf<String, String>()
        rednessMap["id"] = "FCI3"
        rednessMap["question"] = getString(R.string.funduscopy_redness_question)
        rednessMap["answer"] = haveEyeRedness!!

        contraindications.add(rednessMap)

//        var blurringMap = mutableMapOf<String, String>()
//        blurringMap["id"] = "FCI4"
//        blurringMap["question"] = getString(R.string.funduscopy_blurring_question)
//        blurringMap["answer"] = haveBlurring!!
//
//        contraindications.add(blurringMap)
//
//        var doubleMap = mutableMapOf<String, String>()
//        doubleMap["id"] = "FCI5"
//        doubleMap["question"] = getString(R.string.funduscopy_double_question)
//        doubleMap["answer"] = haveDouble!!
//
//        contraindications.add(doubleMap)

        return contraindications
    }

    private fun getConfirmQuestions(): MutableList<Map<String, String>> {
        var questions: MutableList<Map<String, String>> = mutableListOf()

        val haveLeftEye = fundoscopyReadingViewModel.haveLeftEye.value
        val haveRightEye = fundoscopyReadingViewModel.haveRightEye.value
        val haveExport = fundoscopyReadingViewModel.haveExport.value
        val haveDialation = fundoscopyReadingViewModel.haveDialation.value

        var leftMap = mutableMapOf<String, String>()
        leftMap["id"] = "FCSQ1"
        leftMap["question"] = getString(R.string.fundo_que_left_eye)
        leftMap["answer"] = if (haveLeftEye!!) "yes" else "no"

        questions.add(leftMap)

        var rightMap = mutableMapOf<String, String>()
        rightMap["id"] = "FCSQ2"
        rightMap["question"] = getString(R.string.fundo_que_right_eye)
        rightMap["answer"] = if (haveRightEye!!) "yes" else "no"

        questions.add(rightMap)

        var exportMap = mutableMapOf<String, String>()
        exportMap["id"] = "FCSQ3"
        exportMap["question"] = getString(R.string.fundo_que_exported)
        exportMap["answer"] = if (haveExport!!) "yes" else "no"

        questions.add(exportMap)

        var dialtionMap = mutableMapOf<String, String>()
        dialtionMap["id"] = "FCSQ4"
        dialtionMap["question"] = getString(R.string.fundo_que_dialation)
        dialtionMap["answer"] = if (haveDialation!!) "yes" else "no"

        questions.add(dialtionMap)

        var smallMap = mutableMapOf<String, String>()
        smallMap["id"] = "FCSQ5"
        smallMap["question"] = getString(R.string.small_pupil_observed)
        smallMap["answer"] = smallPupil

        questions.add(smallMap)

        var movementMap = mutableMapOf<String, String>()
        movementMap["id"] = "FCSQ6"
        movementMap["question"] = getString(R.string.particiant_movement)
        movementMap["answer"] = participantMovement

        questions.add(movementMap)

        var historyMap = mutableMapOf<String, String>()
        historyMap["id"] = "FCSQ7"
        historyMap["question"] = getString(R.string.history_of_eye)
        historyMap["answer"] = eyeHistory

        questions.add(historyMap)

        var missingMap = mutableMapOf<String, String>()
        missingMap["id"] = "FCSQ8"
        missingMap["question"] = getString(R.string.eye_missing)
        missingMap["answer"] = eyeMissing

        questions.add(missingMap)

        var possibleMap = mutableMapOf<String, String>()
        possibleMap["id"] = "FCSQ9"
        possibleMap["question"] = getString(R.string.possible_cataract)
        possibleMap["answer"] = possiblecataract

        questions.add(possibleMap)

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
        if(smallPupil == "")
        {
            binding.radioGroupSmallPupilValue = true
            binding.executePendingBindings()
            return false

        }
        else if(participantMovement == "")
        {
            binding.radioGroupParticipantMovementValue = true
            binding.executePendingBindings()
            return false
        }
        else if(eyeHistory == "")
        {
            binding.radioGroupEyeHistoryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(eyeMissing == "")
        {
            binding.radioGroupEyeMissingValue = true
            binding.executePendingBindings()
            return false
        }
        else if(possiblecataract == "")
        {
            binding.radioGroupPossibleCataractValue = true
            binding.executePendingBindings()
            return false
        }
        else if(fundoscopyReadingViewModel.haveLeftEye.value == null)
        {
            binding.radioGroupLeftEyeValue = true
            binding.executePendingBindings()
            return false
        }
        else if(fundoscopyReadingViewModel.haveRightEye.value == null)
        {
            binding.radioGroupRightEyeValue = true
            binding.executePendingBindings()
            return false
        }
        else if(fundoscopyReadingViewModel.haveExport.value == null)
        {
            binding.radioGroupExportValue = true
            binding.executePendingBindings()
            return false
        }
        else if(fundoscopyReadingViewModel.haveDialation.value == null)
        {
            binding.radioGroupDialtionValue = true
            binding.executePendingBindings()
            return false
        }
        else
        {
            return true
        }
    }

    private fun validateMandatoryValues(): Boolean {
        val haveLeftEye = fundoscopyReadingViewModel.haveLeftEye.value
        val haveRightEye = fundoscopyReadingViewModel.haveRightEye.value

        if (!haveLeftEye!! && !haveRightEye!!)
        {
            return false
        }

        return true
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
}
