package org.singapore.ghru.ui.ecg.trace


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
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.TraceFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.ecg.questions.ECGQuestionnaireViewModel
import org.singapore.ghru.ui.ecg.trace.complete.CompleteDialogFragment
import org.singapore.ghru.ui.ecg.trace.completed.CompletedDialogFragment
import org.singapore.ghru.ui.ecg.trace.reason.ReasonDialogFragment
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

class TraceFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TraceFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var verifyIDViewModel: TraceViewModel

    private var participant: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    private lateinit var questionnaireViewModel: ECGQuestionnaireViewModel

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
        val dataBinding = DataBindingUtil.inflate<TraceFragmentBinding>(
            inflater,
            R.layout.trace_fragment,
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(ECGQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participant
        // binding.viewModel = verifyIDViewModel
        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participant,
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)
        }

        Log.d("TRACE_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        verifyIDViewModel.eCGSaveRemote?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {

                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {


                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("eCGSaveRemote " + participant.message.toString()))
                binding.progressBar.visibility = View.GONE
                binding.executePendingBindings()
            }
        })


        binding.buttonSubmit.singleClick {

            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else {
//                val status = if (binding.radioGroup.checkedRadioButtonId == R.id.normal) {
//                    getString(R.string.ecg_check_normal)
//                } else {
//                    getString(R.string.ecg_check_abnormal)
//                }

                val status = "A"

                Log.d("TRACE_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participant?.meta?.endTime = endDateTime

                Log.d("TRACE_FRAG", "AFTER_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                verifyIDViewModel.setECGRemote(
                    participant!!,
                    binding.comment.text.toString(),
                    selectedDeviceID!!
                    ,isNetworkAvailable())

                binding.progressBar.visibility = View.VISIBLE
//                val completeDialogFragment = CompleteDialogFragment()
////            val bundle = Bundle()
////            bundle.putParcelable("participant", participant)
////            bundle.putString("comment", binding.comment.text.toString())
////            bundle.putString("deviceId",selectedDeviceID)
//                completeDialogFragment.arguments = bundleOf(
//                    "participant" to participant,
//                    "comment" to binding.comment.text.toString(),
//                    "deviceId" to selectedDeviceID
//                )
//                completeDialogFragment.show(fragmentManager!!)
            }
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        verifyIDViewModel.setStationName(Measurements.ECG)
        verifyIDViewModel.stationDeviceList?.observe(this, Observer {
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
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val haveArteriovenous = questionnaireViewModel.haveArteriovenous.value
        val hadSurgery = questionnaireViewModel.hadSurgery.value
        val lymphRemoved = questionnaireViewModel.lymphRemoved.value
        val haveTrauma = questionnaireViewModel.haveTrauma.value
        val haveNeckInjury = questionnaireViewModel.haveNeckInjury.value
        val amputated = questionnaireViewModel.amputated.value

        var arteriovenousMap = mutableMapOf<String, String>()
        arteriovenousMap["question"] = "Arteriovenous Fistula in both arms?"
        arteriovenousMap["answer"] = if (haveArteriovenous!!) "yes" else "no"

        contraindications.add(arteriovenousMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["question"] = "Breast surgery (left or right)?"
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var lymphRemovedMap = mutableMapOf<String, String>()
        lymphRemovedMap["question"] = "Lymph node(s) removed from armpit?"
        lymphRemovedMap["answer"] = if (lymphRemoved!!) "yes" else "no"

        contraindications.add(lymphRemovedMap)

        var traumaMap = mutableMapOf<String, String>()
        traumaMap["question"] = "Trauma -> swelling (left/ right)?"
        traumaMap["answer"] = haveTrauma!!

        contraindications.add(traumaMap)

        var neckInjuryMap = mutableMapOf<String, String>()
        neckInjuryMap["question"] = "Have had recent neck injury or surgery?"
        neckInjuryMap["answer"] = if (haveNeckInjury!!) "yes" else "no"

        contraindications.add(neckInjuryMap)

        var amputatedMap = mutableMapOf<String, String>()
        amputatedMap["question"] = "Both Arms / Legs Amputated?"
        amputatedMap["answer"] = if (amputated!!) "yes" else "no"

        contraindications.add(amputatedMap)

        return contraindications
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

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
