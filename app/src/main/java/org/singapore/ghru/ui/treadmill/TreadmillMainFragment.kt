package org.singapore.ghru.ui.treadmill

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import io.reactivex.disposables.CompositeDisposable

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.TreadmillMainFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.TreadmillBPRecordRxBus
import org.singapore.ghru.ui.treadmill.adapter.TreadmillRecordAdapter
import org.singapore.ghru.ui.treadmill.beforetest.TreadmillBeforeTestViewModel
import org.singapore.ghru.ui.treadmill.contraindications.TreadmillContraViewModel
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.setDrawableRightColor
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.TreadmillBPData
import org.singapore.ghru.vo.request.TreadmillBody
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList

class TreadmillMainFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TreadmillMainFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: TreadmillMainViewModel

    private var participant: ParticipantRequest? = null

    private val disposables = CompositeDisposable()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: TreadmillRecordAdapter
    private var recordList: ArrayList<TreadmillBP> = ArrayList()

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null
    //var meta: Meta? = null

    private lateinit var questionnaireViewModel: TreadmillContraViewModel
    private lateinit var beforeTestViewModel: TreadmillBeforeTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }

        enumValues<Stage>().forEach {
            var bp = TreadmillBP(0)
            bp.stage.value = it.stage
            bp.diastolic.value = ""
            bp.systolic.value = ""
            bp.pulse.value = ""
            recordList.add(bp)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(TreadmillContraViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        beforeTestViewModel = activity?.run {
            ViewModelProviders.of(this).get(TreadmillBeforeTestViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<TreadmillMainFragmentBinding>(
            inflater,
            R.layout.treadmill_main_fragment,
            container,
            false
        )
        binding = dataBinding

        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager

        adapter = TreadmillRecordAdapter(recordList)
        binding.recyclerView.adapter = adapter

        adapter.setOnItemClickListener { it ->
            val bundle = bundleOf("ParticipantRequest" to participant, "bp" to it)
            navController().navigate(R.id.action_mainFragment_to_bpFragment, bundle)
        }

        setNextButton()

        disposables.add(
            TreadmillBPRecordRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    setNextButton()
                    adapter.notifyDataSetChanged()

                }, { error ->
                    print(error)
                    error.printStackTrace()
                }))

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.participant = participant

        binding.previousButton.singleClick {
            navController().popBackStack()
        }

        binding.textViewSkip.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participant,
                "contraindications" to getContraindications(),
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)
        }



        binding.nextButton.singleClick {
            if(selectedDeviceID==null) {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else {

//                Log.d("MAIN_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)
//
//                val endTime: String = convertTimeTo24Hours()
//                val endDate: String = getDate()
//                val endDateTime:String = endDate + " " + endTime
//
//                participant?.meta?.endTime =  endDateTime
//
//                Log.d("MAIN_FRAG", "AFTER_DESIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                var bpRecordList: ArrayList<TreadmillBPData> = ArrayList()
                for(bp in recordList) {
                    val bpData = TreadmillBPData(bp.systolic.value, bp.diastolic.value, bp.pulse.value, bp.stage.value)
                    bpRecordList.add(bpData)
                }

                val mTreadmillBody = TreadmillBody(binding.comment.text.toString(), selectedDeviceID!!, getBeforeTestQuestions(),
                    null, bpRecordList, getContraindications())

                val bundle = Bundle()
                bundle.putParcelable("participant", participant)
                bundle.putParcelable("treadmillBody", mTreadmillBody)
                navController().navigate(R.id.action_mainFragment_to_afterTestFragment, bundle)
            }
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.TREADMILL)
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
    }

    private fun setNextButton() {
        if (validateNextButton()) {
            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.nextButton.setDrawableRightColor("#0A1D53")
            binding.nextButton.isEnabled = true
        } else {
            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"));
            binding.nextButton.setDrawableRightColor("#AED6F1")
            binding.nextButton.isEnabled = false
        }
    }

    private fun validateNextButton(): Boolean {
        for (bp in recordList) {
            if (bp.stage.value == "Resting (0%)" || bp.stage.value == "After Test")
            {
                if(bp.diastolic.value == "" || bp.systolic.value == "" || bp.pulse.value == "") {
                    return false
                }
            }

        }
        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val bloodPressure = questionnaireViewModel.bloodPressure.value
        val medical = questionnaireViewModel.medicalConditions.value
        val chestPain = questionnaireViewModel.chestPain.value
        val diabetes = questionnaireViewModel.diabetes.value
//        val oxygen = questionnaireViewModel.oxygen.value

        var bloodPressureMap = mutableMapOf<String, String>()
        bloodPressureMap["id"] = "TMCI1"
        bloodPressureMap["question"] = getString(R.string.treadmill_blood_pressure_question)
        bloodPressureMap["answer"] = if (bloodPressure!!) "yes" else "no"

        contraindications.add(bloodPressureMap)

        var medicalMap = mutableMapOf<String, String>()
        medicalMap["id"] = "TMCI2"
        medicalMap["question"] = getString(R.string.treadmill_medical_condition_question)
        medicalMap["answer"] = if (medical!!) "yes" else "no"

        contraindications.add(medicalMap)

        var chestMap = mutableMapOf<String, String>()
        chestMap["id"] = "TMCI3"
        chestMap["question"] = getString(R.string.treadmill_chest_pain_question)
        chestMap["answer"] = if (chestPain!!) "yes" else "no"

        contraindications.add(chestMap)

        var diabetesMap = mutableMapOf<String, String>()
        diabetesMap["id"] = "TMCI4"
        diabetesMap["question"] = "Have uncontrolled diabetes (blood glucose <4 mmol/L or >11 mmol/L) prior to the treadmill test?"
        diabetesMap["answer"] = if (diabetes!!) "yes" else "no"

        contraindications.add(diabetesMap)

//        var oxygenMap = mutableMapOf<String, String>()
//        oxygenMap["id"] = "TMCI5"
//        oxygenMap["question"] = "Does participant have SBP < 100 mmHg / DBP < 40 mmHg / HR < 50bpm (LES)"
//        oxygenMap["answer"] = if (oxygen!!) "yes" else "no"
//
//        contraindications.add(oxygenMap)
//
//        contraindications.add(oxygenMap)

        return contraindications
    }

    private fun getBeforeTestQuestions(): MutableList<Map<String, String>> {
        var questions = mutableListOf<Map<String, String>>()

        val arm = beforeTestViewModel.armPlacement.value

        var treadmillMap = mutableMapOf<String, String>()
        treadmillMap["id"] = "TMSQ5"
        treadmillMap["question"] = getString(R.string.treadmill_arm_question)
        treadmillMap["answer"] = arm!!

        questions.add(treadmillMap)

        return questions
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
