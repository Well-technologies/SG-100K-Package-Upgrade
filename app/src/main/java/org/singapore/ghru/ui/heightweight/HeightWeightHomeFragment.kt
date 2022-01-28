package org.singapore.ghru.ui.heightweight


import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
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
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.HeightWeightHomeFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BodyMeasurementDataEventType
import org.singapore.ghru.event.BodyMeasurementDataRxBus
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.contraindication.HeightWeightQuestionnaireViewModel
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.heightweight.height.reason.ReasonDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import org.singapore.ghru.vo.request.BodyMeasurement
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class HeightWeightHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: HeightWeightHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: HeightWeightHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager


    private var height: BodyMeasurementDataNew? = null
    private var hipWaist: BodyMeasurementDataNew? = null
    private var bodyComposition: BodyMeasurementDataNew? = null

    private var bodyMeasurement: BodyMeasurement? = null

    private var participantRequest: ParticipantRequest? = null

    private var hl7Readings: HL7Readings? = null

    var user: User? = null
    //var meta: Meta? = null

    private lateinit var questionnaireViewModel: HeightWeightQuestionnaireViewModel

    private lateinit var bodyMeasurementHeightValue: BodyMeasurementValue
    private lateinit var bodyMeasurementWeightValue: BodyMeasurementValue

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    private lateinit var bodyMeasurementHeightData: BodyMeasurementDataManualAuto
    private lateinit var bodyMeasurementWeightData: BodyMeasurementDataManualAuto

    private lateinit var bodyMeasurementWR: BodyMeasurementWithoutReadings
    private lateinit var bodyMeasurementManualAuto: BodyMeasurementManualAuto

    private var isManual : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("participant")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        bodyMeasurementHeightValue = BodyMeasurementValue.build()
        bodyMeasurementWeightValue = BodyMeasurementValue.build()

        disposables.add(
            BodyMeasurementDataRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    // if (result == null) {
                    Timber.d(result.bodyMeasurementData.toString())
                    when (result.eventType) {
                        BodyMeasurementDataEventType.HEIGHT -> {
                            height = result.bodyMeasurementData
                            navController().popBackStack()
                            //binding.linearLayoutHeightx.visibility = View.VISIBLE
                        }
                        BodyMeasurementDataEventType.HIP_WAIST -> {
                            hipWaist = result.bodyMeasurementData
                            navController().popBackStack()
                            binding.linearLayoutHipWaistX.visibility = View.VISIBLE
                        }
                        BodyMeasurementDataEventType.BODY_COMOSITION -> {
                            bodyComposition = result.bodyMeasurementData
                            navController().popBackStack()
                            binding.linearLayoutBodyComposition.visibility = View.VISIBLE
                        }

                    }
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
        val dataBinding = DataBindingUtil.inflate<HeightWeightHomeFragmentBinding>(
            inflater,
            R.layout.height_weight_home_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.sample = sampleRequest
        binding.participant = participantRequest
        binding.bodyMeasurementHeightValue = bodyMeasurementHeightValue
        binding.bodyMeasurementWeightValue = bodyMeasurementWeightValue
        bodyMeasurementHeightData = BodyMeasurementDataManualAuto()
        bodyMeasurementWeightData = BodyMeasurementDataManualAuto()

        bodyMeasurementWR = BodyMeasurementWithoutReadings()

        Log.d("HEIGHT_WEIGHT_HOME", " ON_LOADING " + participantRequest?.meta + " END_TIME "+ participantRequest?.meta!!.endTime)


        binding.errorView.collapse()

        viewModel.sampleMangementPocess?.observe(this, Observer { sampleMangementPocess ->
            if (sampleMangementPocess?.status == Status.SUCCESS) {
                activity!!.finish()
            } else if (sampleMangementPocess?.status == Status.ERROR) {
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
            }
        })

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

//        binding.buttonSubmit.singleClick {
//
//            if(selectedDeviceID==null)
//            {
//                binding.textViewDeviceError.visibility = View.VISIBLE
//            }
//            else if (validateHight(bodyMeasurementHeightValue.value) && validateWeight(bodyMeasurementWeightValue.value))
//            {
//
//                bodyMeasurementHeightData.comment = bodyMeasurementHeightValue.comment
//                bodyMeasurementHeightData.deviceId = selectedDeviceID
//                bodyMeasurementHeightData.unit = "cm"
//                bodyMeasurementHeightData.value = bodyMeasurementHeightValue.value.toDouble()
//                println(bodyMeasurementHeightData.toString())
//
//                bodyMeasurementWeightData.comment = bodyMeasurementWeightValue.comment
//                bodyMeasurementWeightData.deviceId = selectedDeviceID
//                bodyMeasurementWeightData.unit = "kg"
//                bodyMeasurementWeightData.value = bodyMeasurementWeightValue.value.toDouble()
//                println(bodyMeasurementWeightData.toString())
//
//                bodyMeasurement = BodyMeasurement(height = bodyMeasurementHeightData, bodyComposition = bodyMeasurementWeightData )
//                bodyMeasurement!!.contraindications = getContraindications()
////                    HeightWeightMeasurement(height = height, weight = bodyComposition)
//
//                Log.d("HEIGHT_WEIGHT_HOME", " BEFORE_ASSIGN " + participantRequest?.meta + " END_TIME "+ participantRequest?.meta!!.endTime)
//
//                val endTime: String = convertTimeTo24Hours()
//                val endDate: String = getDate()
//                val endDateTime:String = endDate + " " + endTime
//
//                participantRequest!!.meta!!.endTime = endDateTime
//
//                Log.d("HEIGHT_WEIGHT_HOME", " AFTER_ASSIGN " + participantRequest?.meta + " END_TIME "+ participantRequest?.meta!!.endTime)
//
//                val heightWeightMeasurementMeta = BodyMeasurementMeta(meta = participantRequest?.meta, body = bodyMeasurement)
//                heightWeightMeasurementMeta.screeningId = participantRequest?.screeningId!!
//                if(isNetworkAvailable()){
//                    heightWeightMeasurementMeta.syncPending =false
//                }else{
//                    heightWeightMeasurementMeta.syncPending =true
//
//                }
//
//                viewModel.setBodyMeasurementMeta(heightWeightMeasurementMeta)
//
//                binding.progressBar.visibility = View.VISIBLE
//                binding.buttonSubmit.visibility = View.GONE
//            }
//            else
//            {
//                binding.errorView.expand()
//                binding.sampleValidationError = true
//                if (height == null) {
//                    updateProcessErrorUI(binding.heightEditText)
//                }
//
//                if (bodyComposition == null) {
//                    updateProcessErrorUI(binding.bodyCompositionTextView)
//
//                }
//                binding.executePendingBindings()
//            }
//
//            // -------------------------------------------------
//        }

        binding.noCaptured.isChecked = true
        viewModel.setHaveCaptured(false)

        binding.buttonSubmit.singleClick {

            if (validateNextButton())
            {
                if(selectedDeviceID==null)
                {
                    binding.textViewDeviceError.visibility = View.VISIBLE
                }
                else if (viewModel.haveManual.value!!)
                {
                    if (validateHight(bodyMeasurementHeightValue.value) && validateWeight(bodyMeasurementWeightValue.value))
                    {
                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime:String = endDate + " " + endTime

                        participantRequest!!.meta!!.endTime = endDateTime

                        bodyMeasurementHeightData.comment = bodyMeasurementWeightValue.comment
                        bodyMeasurementHeightData.deviceId = selectedDeviceID
                        bodyMeasurementHeightData.unit = "cm"
                        bodyMeasurementHeightData.value = bodyMeasurementHeightValue.value
                        println(bodyMeasurementHeightData.toString())

                        bodyMeasurementWeightData.comment = bodyMeasurementWeightValue.comment
                        bodyMeasurementWeightData.deviceId = selectedDeviceID
                        bodyMeasurementWeightData.unit = "kg"
                        bodyMeasurementWeightData.value = bodyMeasurementWeightValue.value
                        println(bodyMeasurementWeightData.toString())

                        val bodyMeasurementManualAuto = BodyMeasurementManualAuto(bodyMeasurementHeightData, bodyMeasurementWeightData)

                        bodyMeasurementManualAuto.contraindications = getContraindications()
                        bodyMeasurementManualAuto.is_captured = getStationQuestions()
                        bodyMeasurementManualAuto.isManualEntry = isManual!!

                        val bodyMeasurementMetaManualAuto = BodyMeasurementMetaManualAuto(meta = participantRequest?.meta, body = bodyMeasurementManualAuto)
                        bodyMeasurementMetaManualAuto.screeningId = participantRequest?.screeningId!!

                        if(!isNetworkAvailable())
                        {
                            bodyMeasurementMetaManualAuto.syncPending =true
                        }

                        viewModel.setBodyMeasurementManualAuto(bodyMeasurementMetaManualAuto, bodyMeasurementMetaManualAuto.screeningId)

                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonSubmit.visibility = View.GONE
                    }
                    else
                    {
                        binding.errorView.expand()
                        binding.sampleValidationError = true
                        if (height == null) {
                            updateProcessErrorUI(binding.heightEditText)
                        }

                        if (bodyComposition == null) {
                            updateProcessErrorUI(binding.bodyCompositionTextView)

                        }
                        binding.executePendingBindings()
                    }
                }
                else
                {
                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest!!.meta!!.endTime = endDateTime

                    bodyMeasurementHeightData.comment = bodyMeasurementWeightValue.comment
                    bodyMeasurementHeightData.deviceId = selectedDeviceID
                    bodyMeasurementHeightData.unit = "NA"
                    bodyMeasurementHeightData.value = "NA"

                    bodyMeasurementWeightData.comment = bodyMeasurementWeightValue.comment
                    bodyMeasurementWeightData.deviceId = selectedDeviceID
                    bodyMeasurementWeightData.unit = "NA"
                    bodyMeasurementWeightData.value = "NA"

                    val bodyMeasurementManualAuto = BodyMeasurementManualAuto(bodyMeasurementHeightData, bodyMeasurementWeightData)

                    bodyMeasurementManualAuto.contraindications = getContraindications()
                    bodyMeasurementManualAuto.is_captured = getStationQuestions()
                    bodyMeasurementManualAuto.isManualEntry = isManual!!

                    val bodyMeasurementMetaManualAuto = BodyMeasurementMetaManualAuto(meta = participantRequest?.meta, body = bodyMeasurementManualAuto)
                    bodyMeasurementMetaManualAuto.screeningId = participantRequest?.screeningId!!

                    if(!isNetworkAvailable())
                    {
                        bodyMeasurementMetaManualAuto.syncPending =true
                    }

                    viewModel.setBodyMeasurementManualAuto(bodyMeasurementMetaManualAuto, bodyMeasurementMetaManualAuto.screeningId)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE
                }

            }
            // -------------------------------------------------
        }

        viewModel.bodyMeasurementMetaManualAuto?.observe(this, Observer { sampleMangementPocess ->

            if(sampleMangementPocess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
            }

            if (sampleMangementPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(sampleMangementPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "HeightWeightMeasurementMeta",
                    BodyMeasurementMeta(meta = participantRequest?.meta, body = bodyMeasurement).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
            }
        })

        binding.radioGroupManual.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesManual)
            {
                // enable the manual entry
                isManual = true
                viewModel.setHaveManual(true)
                binding.radioGroupManualValue = false
                binding.heightLayout.visibility = View.VISIBLE
                binding.weightLayout.visibility = View.VISIBLE
                binding.capturedLayout.visibility = View.GONE
            }
            else
            {
                // disable the manual entry
                isManual = false
                viewModel.setHaveManual(false)
                binding.radioGroupManualValue = false
                binding.heightLayout.visibility = View.GONE
                binding.weightLayout.visibility = View.GONE
                binding.capturedLayout.visibility = View.VISIBLE
            }
            binding.executePendingBindings()
        }

        binding.noManual.isChecked = true

//        viewModel.bodyMeasurementMetaWR?.observe(this, Observer { sampleMangementPocess ->
//
//            if(sampleMangementPocess?.status == Status.LOADING){
//                binding.progressBar.visibility = View.VISIBLE
//                binding.buttonSubmit.visibility = View.GONE
//            }else{
//                binding.progressBar.visibility = View.GONE
//                binding.buttonSubmit.visibility = View.VISIBLE
//            }
//
//            if (sampleMangementPocess?.status == Status.SUCCESS) {
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                completedDialogFragment.show(fragmentManager!!)
//            } else if(sampleMangementPocess?.status == Status.ERROR){
//                Crashlytics.setString(
//                    "HeightWeightMeasurementMeta",
//                    BodyMeasurementMeta(meta = participantRequest?.meta, body = bodyMeasurement).toString()
//                )
//                Crashlytics.setString("participant", participantRequest.toString())
//                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
//            }
//        })

        binding.linearLayoutBodyComposition.singleClick {
            binding.sampleValidationError = false
            //updateProcessValidUI(binding.heightTextView)
            updateProcessValidUI(binding.bodyCompositionTextView)
            navController().navigate(R.id.action_HeightWeightHomeFragment_to_WeightFragment)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.height_and_weight)
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

        binding.buttonReadings.singleClick {

            viewModel.setHL7ScreeningId("AAA")

            binding.heightEditText.setText("")
            binding.weightEditText.setText("")
            binding.obDateTextView.visibility = View.INVISIBLE
            binding.obDateLabel.visibility = View.INVISIBLE
            binding.obDateTextView.setText("")

            viewModel.setHL7ScreeningId(participantRequest!!.screeningId)

            viewModel.getHL7Readings.observe(this, Observer { readings ->

                if (readings?.status == Status.SUCCESS) {
                    hl7Readings = readings.data!!.data
                    println(hl7Readings)

                    binding.heightEditText.setText(hl7Readings!!.values!!.height!!.value!!.toString())
                    binding.weightEditText.setText(hl7Readings!!.values!!.weight!!.value!!.toString())
                    binding.obDateTextView.visibility = View.VISIBLE
                    binding.obDateLabel.visibility = View.VISIBLE
                    binding.obDateTextView.setText(hl7Readings!!.values!!.observation_time)
                    Toast.makeText(activity!!, "Capture reading successfully done", Toast.LENGTH_LONG).show()
//                }
                } else if (readings?.status == Status.ERROR){
                    val errorDialogFragment = ErrorDialogFragment()
                    errorDialogFragment.setErrorMessage(readings.message!!.data!!.message.toString())
                    errorDialogFragment.show(fragmentManager!!)
                    binding.obDateTextView.visibility = View.INVISIBLE
                    binding.obDateLabel.visibility = View.INVISIBLE
                }
//            binding.executePendingBindings()
            })
        }

        onTextChanges(binding.heightEditText)
        onTextChanges(binding.weightEditText)

        binding.radioGroupCaptured.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noCaptured) {
                binding.radioGroupCapturedValue = false
                viewModel.setHaveCaptured(false)

            } else {
                binding.radioGroupCapturedValue = false
                viewModel.setHaveCaptured(true)

            }
            binding.executePendingBindings()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(HeightWeightQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications: MutableList<Map<String, String>> = mutableListOf()

        val haveRetina = questionnaireViewModel.haveUnaided.value

        val unaidedMap = mutableMapOf<String, String>()
        unaidedMap["id"] = "HWCI1"
        unaidedMap["question"] = getString(R.string.height_weight_unaided_question)
        unaidedMap["answer"] = if (haveRetina!!) "yes" else "no"

        contraindications.add(unaidedMap)

        return contraindications
    }

    private fun updateProcessErrorUI(view: TextView) {
        view.setTextColor(Color.parseColor("#FF5E45"))
        view.setDrawbleLeftColor("#FF5E45")
    }

    private fun updateProcessValidUI(view: TextView) {
        view.setTextColor(Color.parseColor("#00548F"))
        view.setDrawbleLeftColor("#00548F")
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.heightEditText) {
                    validateHight(binding.heightEditText.text.toString())
                }

                if(editText == binding.weightEditText) {
                    validateWeight(binding.weightEditText.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
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

    private fun validateWeight(weight: String): Boolean {

        try {
            val weightVal: Double = weight.toDouble()
            if (weightVal >= Constants.BD_WEIGHT_MIN_VAL && weightVal <= Constants.BD_WEIGHT_MAX_VAL) {
                binding.weightTextLayout.error = null
                viewModel.isValidWeight = false
                return true


            } else {
                viewModel.isValidWeight = true
                binding.weightTextLayout.error = getString(R.string.error_not_in_range)
                return false
            }


        } catch (e: Exception) {
            binding.weightTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

    private fun validateHight(hight: String): Boolean {

        try {

            val hightVal: Double = hight.toDouble()
            if (hightVal >= Constants.BD_HEIGHT_MIN_VAL && hightVal <= Constants.BD_HEIGHT_MAX_VAL) {

                binding.heightTextLayout.error = null
                viewModel.isValidHeight = false

                return true

            } else {
                viewModel.isValidHeight = true
                binding.heightTextLayout.error = getString(R.string.error_not_in_range)
                return false

            }

            //validateNextButton()

        } catch (e: Exception) {
            binding.heightTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

    private fun validateNextButton(): Boolean
    {
        if (viewModel.haveManual.value != null && !viewModel.haveManual.value!!)
        {
            if(viewModel.haveCaptured.value == null) {
                binding.radioGroupCapturedValue = true
                binding.executePendingBindings()
                return false
            }
            else if (viewModel.haveCaptured.value == false) {
                binding.radioGroupCapturedValue = false
                binding.executePendingBindings()

                val reasonDialogFragment = ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participantRequest, "questions" to getStationQuestions())
                reasonDialogFragment.show(fragmentManager!!)

                return false
            }
            else
            {
                return true
            }
        }
        else if (viewModel.haveManual.value != null && viewModel.haveManual.value!!)
        {
            if(viewModel.haveCaptured.value == null) {
                binding.radioGroupCapturedValue = true
                binding.executePendingBindings()
                return false
            }
            else
            {
                return true
            }
        }

        return false
    }

    private fun getStationQuestions(): MutableList<Map<String, String>> {
        val contraindications = mutableListOf<Map<String, String>>()

        val haveCaptured = viewModel.haveCaptured.value

        val capturedMap = mutableMapOf<String, String>()
        capturedMap["id"] = "HWSQ1"
        capturedMap["question"] = getString(R.string.height_captured)
        capturedMap["answer"] = if (haveCaptured!!) "yes" else "no"

        contraindications.add(capturedMap)

        return contraindications
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
