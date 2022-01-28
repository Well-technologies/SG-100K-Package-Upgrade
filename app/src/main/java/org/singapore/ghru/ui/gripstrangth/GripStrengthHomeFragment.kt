package org.singapore.ghru.ui.gripstrangth


import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.GripStrengthHomeFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.*
import org.singapore.ghru.ui.gripstrangth.contraindication.GripStrengthQuestionnaireViewModel
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.contraindication.HeightWeightQuestionnaireViewModel
import org.singapore.ghru.ui.gripstrangth.reason.ReasonDialogFragment
import org.singapore.ghru.ui.gripstrangth.valuecheckdialog.ValueCheckDialogFragment
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
import kotlin.collections.ArrayList
import kotlin.math.max


class GripStrengthHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: GripStrengthHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: GripStrengthHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager


    private var leftGrip: GripStrengthData? = null
    private var rightGrip: GripStrengthData? = null
    private var reading3: GripStrengthData? = null
    private var gripStrength: GripStrengthTests? = null

    private var participantRequest: ParticipantRequest? = null

    //var user: User? = null
    //var meta: Meta? = null

    private lateinit var questionnaireViewModel: GripStrengthQuestionnaireViewModel

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    private var selectedSlot: String? = null
    private var slotList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
//            gripStrengthRequest = arguments?.getParcelable<GripStrengthRequest>("participant")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            LeftGripRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    leftGrip = result

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            RightGripRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    rightGrip = result

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            Reading3RecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    reading3 = result

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
        val dataBinding = DataBindingUtil.inflate<GripStrengthHomeFragmentBinding>(
            inflater,
            R.layout.grip_strength_home_fragment,
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

//        viewModel.setUser("user")
//        viewModel.user?.observe(this, Observer { userData ->
//            if (userData?.data != null) {
//                // setupNavigationDrawer(userData.data)
//                user = userData.data
//
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime:String = sDate + " " + sTime
//
//                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
//                //meta?.registeredBy = user?.id
//            }
//
//        })

        if (leftGrip != null) {
                binding.gripCompleteView.visibility = View.VISIBLE
                binding.linearLayoutGrip.background =
                    resources.getDrawable(R.drawable.ic_process_complete_bg, null)
        }

        if (rightGrip != null) {
                binding.strengthCompleteView.visibility = View.VISIBLE
                binding.linearLayoutStrength.background =
                    resources.getDrawable(R.drawable.ic_process_complete_bg, null)
        }

        if (reading3 != null) {
            binding.reading3CompleteView.visibility = View.VISIBLE
            binding.linearLayoutReading3.background =
                resources.getDrawable(R.drawable.ic_process_complete_bg, null)
        }

        Log.d("GRIP_STRENGTH_HOME","DATA:" + leftGrip.toString()  + ", AND " + rightGrip + ", and " + reading3)
        binding.errorView.collapse()

        viewModel.gripMeasurementMetaOffline?.observe(this, Observer { sampleMangementPocess ->

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
                Log.d("ok","ok")
            } else if(sampleMangementPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "HeightWeightMeasurementMeta",
                    GripStrengthRequest(meta = participantRequest?.meta, body = gripStrength).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
            }
        })

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

        Log.d("HOME_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        binding.buttonSubmit.singleClick {

            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (selectedSlot == null)
            {
                binding.barTextViewSlotError.visibility = View.VISIBLE
            }
            else if (validateNextButton())
            {
                if (leftGrip != null && rightGrip != null && reading3 != null)
                {
                    gripStrength = GripStrengthTests(
                        reading1 = leftGrip,
                        reading2 = rightGrip,
                        reading3 = reading3,
                        comment = binding.comment.text.toString(),
                        device_id = selectedDeviceID
                    )
                    gripStrength!!.contraindications = getContraindications()
                    gripStrength!!.stationQuestions = getQuestions()

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val gripStrengthRequest = GripStrengthRequest(meta = participantRequest?.meta, body = gripStrength)
                    gripStrengthRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        gripStrengthRequest.syncPending =false
                    }else{
                        gripStrengthRequest.syncPending =true

                    }

                    if ( validateValueDiffer())
                    {
                        viewModel.setGripMeasurementMeta(gripStrengthRequest)

                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonSubmit.visibility = View.GONE
                    }
                    else
                    {
                        val valueCheckDialogFragment = ValueCheckDialogFragment()
                        valueCheckDialogFragment.arguments = bundleOf("GripStrengthRequest" to  gripStrengthRequest)
                        valueCheckDialogFragment.show(fragmentManager!!)
                        //Toast.makeText(activity!!, "invalid values", Toast.LENGTH_LONG).show()
                    }

                } else
                {
                    binding.errorView.expand()
                    binding.sampleValidationError = true
                    if (leftGrip == null) {
                        updateProcessErrorUI(binding.gripTextView)
                    }

                    if (rightGrip == null) {
                        updateProcessErrorUI(binding.strengthTextView)

                    }
                    if (reading3 == null) {
                        updateProcessErrorUI(binding.reading3TextView)

                    }
                    binding.executePendingBindings()
                }
            }
        }

        binding.linearLayoutGrip.singleClick {

            binding.sampleValidationError = false
            updateProcessValidUI(binding.gripTextView)
            updateProcessValidUI(binding.strengthTextView)
            updateProcessValidUI(binding.reading3TextView)
            val bundle = Bundle()

            if (leftGrip != null)
            {
                bundle.putParcelable("Reading1", leftGrip)
                navController().navigate(R.id.action_GripStrengthHomeFragment_to_GripFragment, bundle)
            }
            else
            {
                navController().navigate(R.id.action_GripStrengthHomeFragment_to_GripFragment)
            }
        }

        binding.linearLayoutStrength.singleClick {
            binding.sampleValidationError = false
            updateProcessValidUI(binding.gripTextView)
            updateProcessValidUI(binding.strengthTextView)
            updateProcessValidUI(binding.reading3TextView)
            val bundle = Bundle()
            if (rightGrip != null)
            {
                bundle.putParcelable("Reading2", rightGrip)
                navController().navigate(R.id.action_GripStrengthHomeFragment_to_StrengthFragment, bundle)
            }
            else
            {
                navController().navigate(R.id.action_GripStrengthHomeFragment_to_StrengthFragment)
            }
        }

        binding.linearLayoutReading3.singleClick {
            binding.sampleValidationError = false
            updateProcessValidUI(binding.gripTextView)
            updateProcessValidUI(binding.reading3TextView)
            updateProcessValidUI(binding.strengthTextView)
            val bundle = Bundle()
            if (reading3 != null)
            {
                bundle.putParcelable("Reading3", reading3)
                navController().navigate(R.id.action_GripStrengthHomeFragment_to_Reading3Fragment, bundle)
            }
            else
            {
                navController().navigate(R.id.action_GripStrengthHomeFragment_to_Reading3Fragment)
            }
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.grip_strength)
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

        binding.radioGroupDominant.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.bothDominant) {
                binding.radioGroupDominantValue = false
                viewModel.setDominant("both")
            } else if (radioGroup.checkedRadioButtonId == R.id.leftDominant) {
                binding.radioGroupDominantValue = false
                viewModel.setDominant("left")
            } else if (radioGroup.checkedRadioButtonId == R.id.rightDominant) {
                binding.radioGroupDominantValue = false
                viewModel.setDominant("right")
            }
            binding.executePendingBindings()
        }

        slotList.clear()
        slotList.add(getString(R.string.unknown_slot))
        slotList.add("1")
        slotList.add("2")
        slotList.add("3")
        slotList.add("4")
        slotList.add("5")
        val adapter1 = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, slotList)
        binding.slotSpinner.setAdapter(adapter1)

        binding.slotSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedSlot = null
                } else {
                    binding.barTextViewSlotError.visibility = View.GONE
                    selectedSlot = binding.slotSpinner.selectedItem.toString()
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(GripStrengthQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveDominant.value == null) {
            binding.radioGroupDominantValue = true
            binding.executePendingBindings()
            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications: MutableList<Map<String, String>> = mutableListOf()

        val haveInjury = questionnaireViewModel.haveInjury.value
        val haveSevere = questionnaireViewModel.haveSevere.value

        val injuryMap = mutableMapOf<String, String>()
        injuryMap["id"] = "HGCI1"
        injuryMap["question"] = getString(R.string.grip_injury_question)
        injuryMap["answer"] = haveInjury!!

        contraindications.add(injuryMap)

        val severeMap = mutableMapOf<String, String>()
        severeMap["id"] = "HGCI2"
        severeMap["question"] = getString(R.string.grip_severe_question)
        severeMap["answer"] = haveSevere!!

        contraindications.add(severeMap)

        return contraindications
    }

    private fun getQuestions(): MutableList<Map<String, String>> {
        val gripStationQuestions = mutableListOf<Map<String, String>>()
        val slot = selectedSlot
        val dominant = viewModel.haveDominant.value

        val dominantMap = mutableMapOf<String, String>()
        dominantMap["id"] = "GSSQ1"
        dominantMap["question"] = getString(R.string.hand_grip_question1)
        dominantMap["answer"] = dominant!!

        gripStationQuestions.add(dominantMap)

        val slotMap = mutableMapOf<String, String>()
        slotMap["id"] = "GSSQ2"
        slotMap["question"] = getString(R.string.hand_grip_question2)
        slotMap["answer"] = slot!!

        gripStationQuestions.add(slotMap)

        return gripStationQuestions
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

    private fun validateValueDiffer(): Boolean
    {
        val leftValues : ArrayList<Double> = ArrayList()
        val rightValues : ArrayList<Double> = ArrayList()

        if (leftGrip?.left_grip?.value != "NA")
        {
            leftValues.add(leftGrip?.left_grip?.value!!.toDouble())
        }
        else
        {
            leftValues.add(0.0)
        }

        if (rightGrip?.left_grip?.value != "NA")
        {
            leftValues.add(rightGrip?.left_grip?.value!!.toDouble())
        }
        else
        {
            leftValues.add(0.0)
        }

        if (reading3?.left_grip?.value != "NA")
        {
            leftValues.add(reading3?.left_grip?.value!!.toDouble())
        }
        else
        {
            leftValues.add(0.0)
        }

        if (leftGrip?.right_grip?.value != "NA")
        {
            rightValues.add(leftGrip?.right_grip?.value!!.toDouble())
        }
        else
        {
            rightValues.add(0.0)
        }

        if (rightGrip?.right_grip?.value != "NA")
        {
            rightValues.add(rightGrip?.right_grip?.value!!.toDouble())
        }
        else
        {
            rightValues.add(0.0)
        }

        if (reading3?.right_grip?.value != "NA")
        {
            rightValues.add(reading3?.right_grip?.value!!.toDouble())
        }
        else
        {
            rightValues.add(0.0)
        }

        val maxLeft = leftValues.max() ?: 0.0
        val minLeft = leftValues.min() ?: 0.0
        val leftDiff = maxLeft - minLeft

        val maxRight = rightValues.max() ?: 0.0
        val minRight = rightValues.min() ?: 0.0
        val rightDiff = maxRight - minRight

        Log.d("GRIP_HOME", " DATA: LEFT_DIFF - " + leftDiff + " RIGHT_DIFF - " + rightDiff )

        if ((leftDiff > 7.0) || (rightDiff > 7.0))
        {
            // message
            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()


}
