package org.singapore.ghru.ui.ultrasound

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.google.gson.GsonBuilder

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.UltraSoundReadingFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.ultrasound.completed.CompletedDialogFragment
import org.singapore.ghru.ui.ultrasound.contraindications.UltrasoundContraViewModel
import org.singapore.ghru.ui.ultrasound.missingvalue.UltraMissingDialogFragment
import org.singapore.ghru.ui.ultrasound.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.UltraBody
import org.singapore.ghru.vo.request.UltraBodyData
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UltraSoundReadingFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<UltraSoundReadingFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: UltraSoundReadingViewModel

    private var participantRequest: ParticipantRequest? = null

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    @Inject
    lateinit var jobManager: JobManager

    private var images: String? = null

    private var images2d: String? = null

    private lateinit var questionnaireViewModel: UltrasoundContraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<UltraSoundReadingFragmentBinding>(
            inflater,
            R.layout.ultra_sound_reading_fragment,
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
            ViewModelProviders.of(this).get(UltrasoundContraViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.participant = participantRequest

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter)

        viewModel.setStationName(Measurements.ULTRASOUND)
        //viewModel.setStationName(Measurements.DXA)
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

        viewModel.ultrasoundComplete?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {
                Crashlytics.setString("comment", binding.comment.text.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("ultrasoundComplete " + participant.message.toString()))
                binding.executePendingBindings()
            }
        })

        Log.d("READING_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        binding.nextButton.singleClick {
            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (validateUltrasound())
            {
                if (validateMissingValue())
                {
                    Log.d("READING_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime =  endDateTime

                    Log.d("READING_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val mUltraBody = UltraBody(
                        UltraBodyData(binding.comment.text.toString(),selectedDeviceID!!, images!!, images2d!!),
                        getContraindications())
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    viewModel.setParticipantComplete(
                        participantRequest!!, isNetworkAvailable(),
                        gson.toJson(mUltraBody)
                    )
                }
                else
                {
//                    Log.d("READING_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)
//
//                    val endTime: String = convertTimeTo24Hours()
//                    val endDate: String = getDate()
//                    val endDateTime:String = endDate + " " + endTime
//
//                    participantRequest?.meta?.endTime =  endDateTime
//
//                    Log.d("READING_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)
//
//                    val ultraBodyData = UltraBodyData(binding.comment.text.toString(),selectedDeviceID!!, images!!, images2d!!)

                    val missingDialogFragment = UltraMissingDialogFragment()
//                    missingDialogFragment.arguments = bundleOf(
//                        "participant" to participantRequest,
//                        "isNetwork" to isNetworkAvailable(),
//                        "ultraBodyData" to ultraBodyData,
//                        "contraindications" to getContraindications())
                    missingDialogFragment.show(fragmentManager!!)
                }
            }
        }

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participantRequest,
                "contraindications" to getContraindications(),
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.radioGroupImages.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesImages) {
                images = "yes"
                binding.radioGroupImagesValue = false;
            } else {
                images = "no"
                binding.radioGroupImagesValue = false;
            }
            binding.executePendingBindings()
        }

        binding.radioGroupImages2d.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.yesImages2d) {
                images2d = "yes"
                binding.radioGroupImagesValue2d = false
            } else {
                images2d = "no"
                binding.radioGroupImagesValue2d = false
            }
            binding.executePendingBindings()
        }
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadSurgery = questionnaireViewModel.hadSurgery.value

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = "CUCI1"
        surgeryMap["question"] = getString(R.string.ultra_neck_injury_question)
        surgeryMap["answer"] = if (hadSurgery!!) "yes" else "no"

        contraindications.add(surgeryMap)

        return contraindications
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    private fun validateUltrasound(): Boolean {
        if(images == null)
        {
            binding.radioGroupImagesValue = true
            binding.executePendingBindings()
            return false

        }
        else if(images2d == null)
        {
            binding.radioGroupImagesValue2d = true
            binding.executePendingBindings()
            return false

        }

        return true
    }

    private fun validateMissingValue(): Boolean {
        if(images == "no" && images2d == "no")
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
