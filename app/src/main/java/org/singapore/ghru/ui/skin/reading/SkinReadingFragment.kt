package org.singapore.ghru.ui.skin.reading


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
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
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.SkinReadingFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.skin.contra.SkinQuestionsViewModel
import org.singapore.ghru.ui.skin.partialreadings.PartialReadingsDialogFragment
import org.singapore.ghru.ui.skin.reason.ReasonDialogFragment
import org.singapore.ghru.ui.ultrasound.missingvalue.UltraMissingDialogFragment
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
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class SkinReadingFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors


    var binding by autoCleared<SkinReadingFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: SkinReadingViewModel

    private var participant: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null
    private var selectedDeviceIDTwo: String? = null
    private var selectedDeviceIDThree: String? = null

    @Inject
    lateinit var jobManager: JobManager

    private lateinit var questionnaireViewModel: SkinQuestionsViewModel
    private var isPartial : Boolean? = null
    private var isManual : Boolean? = false

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
        val dataBinding = DataBindingUtil.inflate<SkinReadingFragmentBinding>(
            inflater,
            R.layout.skin_reading_fragment,
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

        viewModel.skinPostComplete?.observe(this, Observer { participant ->

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
                if (isManual != null && !isManual!!)
                {
                    binding.textViewDeviceError.visibility = View.VISIBLE
                    binding.textViewDeviceErrorTewl.visibility = View.GONE
                }
                else
                {
                    binding.textViewDeviceErrorTewl.visibility = View.VISIBLE
                    binding.textViewDeviceError.visibility = View.GONE
                }
            }
            else if (selectedDeviceIDTwo==null)
            {
                if (isManual != null && !isManual!!)
                {
                    binding.textViewDeviceError1.visibility = View.VISIBLE
                    binding.textViewDeviceErrorTewl.visibility = View.GONE
                }
                else
                {
                    binding.textViewDeviceErrorHydration.visibility = View.VISIBLE
                    binding.textViewDeviceError1.visibility = View.GONE
                }
            }
            else if (selectedDeviceIDThree==null)
            {
                if (isManual != null && !isManual!!)
                {
                    binding.textViewDeviceError2.visibility = View.VISIBLE
                    binding.textViewDeviceErrorPh.visibility = View.GONE
                }
                else
                {
                    binding.textViewDeviceErrorPh.visibility = View.VISIBLE
                    binding.textViewDeviceError2.visibility = View.GONE
                }
            }
            else if (validateStationQuestions())
            {
                if (validateMissingValues())
                {
                    if (isManual != null && isManual!!)
                    {
                        if (validateInputs())
                        {
                            val endTime: String = convertTimeTo24Hours()
                            val endDate: String = getDate()
                            val endDateTime:String = endDate + " " + endTime

                            participant?.meta?.endTime = endDateTime

                            val skinRequest = SkinRequest(
                                device_id = selectedDeviceID,
                                device_id_two = selectedDeviceIDTwo,
                                device_id_three = selectedDeviceIDThree,
                                comment=binding.comment.text.toString(),
                                meta = participant?.meta)
                            skinRequest.screeningId = participant?.screeningId!!
                            skinRequest.contraindications = getContraindications()
                            skinRequest.stationQuestions = getConfirmQuestions()
                            skinRequest.tewl_readings = getTewlReadings()
                            skinRequest.hydration_readings = getHydrationReadings()
                            skinRequest.ph_readings = getPhReadings()

                            skinRequest.isManualEntry = true

                            if(!isNetworkAvailable())
                            {
                                skinRequest.syncPending =true
                            }

                            if (isPartial!!)
                            {
                                skinRequest.isPartialSubmission = true
                                val partialDialogFragment = PartialReadingsDialogFragment()
                                partialDialogFragment.arguments = bundleOf(
                                    "participant" to participant,
                                    "skinRequest" to skinRequest
                                )
                                partialDialogFragment.show(fragmentManager!!)
                            }
                            else
                            {
                                skinRequest.isPartialSubmission = false
                                viewModel.setPostSkin(skinRequest, participant!!.screeningId)
                            }
                        }
                        else
                        {
                            Toast.makeText(activity!!, "Please add at least one value", Toast.LENGTH_LONG).show()
                        }
                    }
                    else
                    {
                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime:String = endDate + " " + endTime

                        participant?.meta?.endTime = endDateTime

                        val skinRequest = SkinRequest(
                            device_id = selectedDeviceID,
                            device_id_two = selectedDeviceIDTwo,
                            device_id_three = selectedDeviceIDThree,
                            comment=binding.comment.text.toString(),
                            meta = participant?.meta)
                        skinRequest.screeningId = participant?.screeningId!!
                        skinRequest.contraindications = getContraindications()
                        skinRequest.stationQuestions = getConfirmQuestions()
                        //skinRequest.readings = getReadings()
                        skinRequest.isManualEntry = false

                        if(!isNetworkAvailable())
                        {
                            skinRequest.syncPending =true
                        }

                        skinRequest.isPartialSubmission = false
                        viewModel.setPostSkin(skinRequest, participant!!.screeningId)

                    }
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
                "ParticipantRequest" to participant,
                "contraindications" to getContraindications(),
                "comment" to binding.comment.text.toString(),
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)

            //binding.textInputEditText1.setText(setTwoDecimals(binding.textInputEditText1.toString()))
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter_Device_list = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter_Device_list);
        binding.deviceIdSpinner1.setAdapter(adapter_Device_list)
        binding.deviceIdSpinner2.setAdapter(adapter_Device_list)

        binding.deviceIdSpinnerTewl.setAdapter(adapter_Device_list);
        binding.deviceIdSpinnerHydration.setAdapter(adapter_Device_list)
        binding.deviceIdSpinnerPh.setAdapter(adapter_Device_list)

        viewModel.setStationName(Measurements.Skin)
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

        binding.deviceIdSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceIDTwo = null
                } else {
                    binding.textViewDeviceError1.visibility = View.GONE
                    selectedDeviceIDTwo = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.deviceIdSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceIDThree = null
                } else {
                    binding.textViewDeviceError2.visibility = View.GONE
                    selectedDeviceIDThree = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.deviceIdSpinnerTewl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceID = null
                } else {
                    binding.textViewDeviceErrorTewl.visibility = View.GONE
                    selectedDeviceID = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.deviceIdSpinnerHydration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceIDTwo = null
                } else {
                    binding.textViewDeviceErrorHydration.visibility = View.GONE
                    selectedDeviceIDTwo = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.deviceIdSpinnerPh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceIDThree = null
                } else {
                    binding.textViewDeviceErrorPh.visibility = View.GONE
                    selectedDeviceIDThree = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.radioGroupManual.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.manualYes)
            {
                // enable the manual entry
                isManual = true
                viewModel.setHaveManual(true)
                binding.radioGroupManualValue = false
                binding.manualReadingLayout.visibility = View.VISIBLE

                binding.tewlSpinnerLayout.visibility = View.VISIBLE
                binding.hydrationSpinnerLayout.visibility = View.VISIBLE
                binding.phSpinnerLayout.visibility = View.VISIBLE

                binding.spinnerLayout1.visibility = View.GONE
                binding.spinnerLayout2.visibility = View.GONE
                binding.spinnerLayout3.visibility = View.GONE
            }
            else
            {
                // disable the manual entry
                isManual = false
                viewModel.setHaveManual(false)
                binding.radioGroupManualValue = false
                binding.manualReadingLayout.visibility = View.GONE

                binding.tewlSpinnerLayout.visibility = View.GONE
                binding.hydrationSpinnerLayout.visibility = View.GONE
                binding.phSpinnerLayout.visibility = View.GONE

                binding.spinnerLayout1.visibility = View.VISIBLE
                binding.spinnerLayout2.visibility = View.VISIBLE
                binding.spinnerLayout3.visibility = View.VISIBLE
            }
            binding.executePendingBindings()
        }

        binding.manualNo.isChecked = true

        binding.radioGroupVipoMeter.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesVipo)
            {
                binding.radioGroupVipometerValue = false
                viewModel.setHaveVapometer("right")
            }
            else if ((radioGroup.checkedRadioButtonId == R.id.noVipo))
            {
                binding.radioGroupVipometerValue = false
                viewModel.setHaveVapometer("left")
            }
            else
            {
                binding.radioGroupVipometerValue = false
                viewModel.setHaveVapometer("N/A")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupMoisture.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesMoisture)
            {
                binding.radioGroupMoistureValue = false
                viewModel.setHaveMoisture("right")
            }
            else if (radioGroup.checkedRadioButtonId == R.id.noMoisture)
            {
                binding.radioGroupMoistureValue = false
                viewModel.setHaveMoisture("left")
            }
            else
            {
                binding.radioGroupMoistureValue = false
                viewModel.setHaveMoisture("N/A")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupPh.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesPh)
            {
                binding.radioGroupPhValue = false
                viewModel.setHavePH("right")
            }
            else if (radioGroup.checkedRadioButtonId == R.id.noPh)
            {
                binding.radioGroupPhValue = false
                viewModel.setHavePH("left")
            }
            else
            {
                binding.radioGroupPhValue = false
                viewModel.setHavePH("N/A")
            }
            binding.executePendingBindings()

        }

        setDefaultValues()

        //onTextChanges(binding.textInputEditText1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(SkinQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun validateInputs() : Boolean
    {
        if (binding.textInputEditText1.text!!.toString().isNotEmpty()
            && binding.textInputEditText2.text!!.toString().isNotEmpty()
            && binding.textInputEditText3.text!!.toString().isNotEmpty()
            && binding.textInputEditText4.text!!.toString().isNotEmpty()
            && binding.textInputEditText5.text!!.toString().isNotEmpty()
            && binding.textInputEditText6.text!!.toString().isNotEmpty()
            && binding.textInputEditText7.text!!.toString().isNotEmpty()
            && binding.textInputEditText8.text!!.toString().isNotEmpty()
            && binding.textInputEditText9.text!!.toString().isNotEmpty())
        {
            isPartial = false
            return true

        }
        else if (binding.textInputEditText1.text!!.toString().isNotEmpty()
            || binding.textInputEditText2.text!!.toString().isNotEmpty()
            || binding.textInputEditText3.text!!.toString().isNotEmpty()
            || binding.textInputEditText4.text!!.toString().isNotEmpty()
            || binding.textInputEditText5.text!!.toString().isNotEmpty()
            || binding.textInputEditText6.text!!.toString().isNotEmpty()
            || binding.textInputEditText7.text!!.toString().isNotEmpty()
            || binding.textInputEditText8.text!!.toString().isNotEmpty()
            || binding.textInputEditText9.text!!.toString().isNotEmpty())
        {
            isPartial = true
            return true
        }

        isPartial = null
        return false
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadSkinLesson = questionnaireViewModel.hadSkinLesson.value

        var skinMap = mutableMapOf<String, String>()
        skinMap["id"] = getString(R.string.skin_contra_id)
        skinMap["question"] = getString(R.string.skin_contra_question)
        skinMap["answer"] = hadSkinLesson!!

        contraindications.add(skinMap)

        return contraindications
    }

    private fun getConfirmQuestions(): MutableList<Map<String, String>> {
        var questions: MutableList<Map<String, String>> = mutableListOf()

        val haveVapometer = viewModel.haveVapometer.value
        val haveMoisture = viewModel.haveMoisture.value
        val havePh = viewModel.havePh.value

        var vipoMap = mutableMapOf<String, String>()
        vipoMap["id"] = getString(R.string.skin_station_question_id_1)
        vipoMap["question"] = getString(R.string.skin_station_question_text_1)
        vipoMap["answer"] = haveVapometer!!

        questions.add(vipoMap)

        var moistureMap = mutableMapOf<String, String>()
        moistureMap["id"] = getString(R.string.skin_station_question_id_2)
        moistureMap["question"] = getString(R.string.skin_station_question_text_2)
        moistureMap["answer"] = haveMoisture!!

        questions.add(moistureMap)

        var phMap = mutableMapOf<String, String>()
        phMap["id"] = getString(R.string.skin_station_question_id_3)
        phMap["question"] = getString(R.string.skin_station_question_text_3)
        phMap["answer"] = havePh!!

        questions.add(phMap)

        return questions
    }

    private fun getTewlReadings(): MutableList<Map<String, String>> {
        val readings: MutableList<Map<String, String>> = mutableListOf()

        val reading1 = if (!binding.textInputEditText1.text!!.toString().equals("")) binding.textInputEditText1.text.toString() else "NA"
        val reading2 = if (!binding.textInputEditText2.text!!.toString().equals("")) binding.textInputEditText2.text.toString() else "NA"
        val reading3 = if (!binding.textInputEditText3.text!!.toString().equals("")) binding.textInputEditText3.text.toString() else "NA"

        val reading1Map = mutableMapOf<String, String>()
        reading1Map["id"] = getString(R.string.skin_reading_id_1)
        reading1Map["reading"] = getString(R.string.skin_reading_text_1)
        reading1Map["value"] = reading1

        readings.add(reading1Map)

        val reading2Map = mutableMapOf<String, String>()
        reading2Map["id"] = getString(R.string.skin_reading_id_2)
        reading2Map["reading"] = getString(R.string.skin_reading_text_2)
        reading2Map["value"] = reading2

        readings.add(reading2Map)

        val reading3Map = mutableMapOf<String, String>()
        reading3Map["id"] = getString(R.string.skin_reading_id_3)
        reading3Map["reading"] = getString(R.string.skin_reading_text_3)
        reading3Map["value"] = reading3

        readings.add(reading3Map)
        return readings
    }

    private fun getHydrationReadings(): MutableList<Map<String, String>> {
        val readings: MutableList<Map<String, String>> = mutableListOf()

        val reading4 = if (!binding.textInputEditText4.text!!.toString().equals("")) binding.textInputEditText4.text.toString() else "NA"
        val reading5 = if (!binding.textInputEditText5.text!!.toString().equals("")) binding.textInputEditText5.text.toString() else "NA"
        val reading6 = if (!binding.textInputEditText6.text!!.toString().equals("")) binding.textInputEditText6.text.toString() else "NA"

        val reading4Map = mutableMapOf<String, String>()
        reading4Map["id"] = getString(R.string.skin_reading_id_4)
        reading4Map["reading"] = getString(R.string.skin_reading_text_4)
        reading4Map["value"] = reading4

        readings.add(reading4Map)

        val reading5Map = mutableMapOf<String, String>()
        reading5Map["id"] = getString(R.string.skin_reading_id_5)
        reading5Map["reading"] = getString(R.string.skin_reading_text_5)
        reading5Map["value"] = reading5

        readings.add(reading5Map)

        val reading6Map = mutableMapOf<String, String>()
        reading6Map["id"] = getString(R.string.skin_reading_id_6)
        reading6Map["reading"] = getString(R.string.skin_reading_text_6)
        reading6Map["value"] = reading6

        readings.add(reading6Map)

        return readings
    }
    private fun getPhReadings(): MutableList<Map<String, String>> {
        val readings: MutableList<Map<String, String>> = mutableListOf()

        val reading7 = if (!binding.textInputEditText7.text!!.toString().equals("")) binding.textInputEditText7.text.toString() else "NA"
        val reading8 = if (!binding.textInputEditText8.text!!.toString().equals("")) binding.textInputEditText8.text.toString() else "NA"
        val reading9 = if (!binding.textInputEditText9.text!!.toString().equals("")) binding.textInputEditText9.text.toString() else "NA"

        val reading7Map = mutableMapOf<String, String>()
        reading7Map["id"] = getString(R.string.skin_reading_id_7)
        reading7Map["reading"] = getString(R.string.skin_reading_text_7)
        reading7Map["value"] = reading7

        readings.add(reading7Map)

        val reading8Map = mutableMapOf<String, String>()
        reading8Map["id"] = getString(R.string.skin_reading_id_8)
        reading8Map["reading"] = getString(R.string.skin_reading_text_8)
        reading8Map["value"] = reading8

        readings.add(reading8Map)

        val reading9Map = mutableMapOf<String, String>()
        reading9Map["id"] = getString(R.string.skin_reading_id_9)
        reading9Map["reading"] = getString(R.string.skin_reading_text_9)
        reading9Map["value"] = reading9

        readings.add(reading9Map)

        return readings
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
        if(viewModel.haveManual.value == null)
        {
            binding.radioGroupManualValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveVapometer.value == null)
        {
            binding.radioGroupVipometerValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveMoisture.value == null)
        {
            binding.radioGroupMoistureValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.havePh.value == null)
        {
            binding.radioGroupPhValue = true
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
        val hadSkinLesson = questionnaireViewModel.hadSkinLesson.value

        if (hadSkinLesson == "right")
        {
            binding.noVipo.isChecked = true
            binding.noMoisture.isChecked = true
            binding.noPh.isChecked = true
        }
        else
        {
            binding.yesVipo.isChecked = true
            binding.yesMoisture.isChecked = true
            binding.yesPh.isChecked = true
        }

        binding.textInputEditText1.addDecimalLimiter(1)
        binding.textInputEditText2.addDecimalLimiter(1)
        binding.textInputEditText3.addDecimalLimiter(1)
        binding.textInputEditText4.addDecimalLimiter(1)
        binding.textInputEditText5.addDecimalLimiter(1)
        binding.textInputEditText6.addDecimalLimiter(1)

        binding.textInputEditText7.addDecimalLimiter()
        binding.textInputEditText8.addDecimalLimiter()
        binding.textInputEditText9.addDecimalLimiter()
    }

    private fun validateMissingValues(): Boolean
    {
        if(viewModel.haveVapometer.value == "N/A" && viewModel.haveMoisture.value == "N/A" && viewModel.havePh.value == "N/A")
        {
            return false
        }

        return true
    }

//    private fun setTwoDecimals(string: String) : String
//    {
//        var convertedValue : String? = null
//        val decimalFormat = DecimalFormat("##.##")
//        decimalFormat.maximumFractionDigits = 2
//        decimalFormat.maximumFractionDigits = 2
//
//        convertedValue = decimalFormat.format(string.toDouble())
//
//        return convertedValue
//    }

//    private fun onTextChanges(editText: EditText) {
//        editText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                if(editText == binding.textInputEditText1)
//                {
//
//                }
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//        })
//    }

    fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {

        var str = string
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count{ ".".contains(it) }

        if (decimalCount > 1)
            return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }

    fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

        this.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val str = this@addDecimalLimiter.text!!.toString()
                if (str.isEmpty()) return
                val str2 = decimalLimiter(str, maxLimit)

                if (str2 != str) {
                    this@addDecimalLimiter.setText(str2)
                    val pos = this@addDecimalLimiter.text!!.length
                    this@addDecimalLimiter.setSelection(pos)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
