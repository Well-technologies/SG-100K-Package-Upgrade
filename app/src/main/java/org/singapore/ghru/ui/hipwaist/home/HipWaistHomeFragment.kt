package org.singapore.ghru.ui.hipwaist.home


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
import org.singapore.ghru.databinding.HipWaistHomeFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.*
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.contraindication.HeightWeightQuestionnaireViewModel
import org.singapore.ghru.ui.hipwaist.contraindication.HipWaistQuestionnaireViewModel
import org.singapore.ghru.ui.hipwaist.reason.ReasonDialogFragment
import org.singapore.ghru.ui.spirometry.questionnaire.SpiroQuestionnaireViewModel
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


class HipWaistHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: HipWaistHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: HipWaistHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager

    private var hipData: HipWaistData? = null
    private var waistData: HipWaistData? = null
    private var hipWaistData: HipWaistTests? = null

    private var participantRequest: ParticipantRequest? = null

    private lateinit var questionnaireViewModel: HipWaistQuestionnaireViewModel

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            HipRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    hipData = result

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            WaistRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    waistData = result

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
        val dataBinding = DataBindingUtil.inflate<HipWaistHomeFragmentBinding>(
            inflater,
            R.layout.hip_waist_home_fragment,
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

        Log.d("HOME_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        if (hipData != null)
        {
                binding.hipCompleteView.visibility = View.VISIBLE
                binding.linearLayoutHip.background = resources.getDrawable(R.drawable.ic_process_complete_bg, null)

        }

        if (waistData != null)
        {
            binding.waistCompleteView.visibility = View.VISIBLE
            binding.linearLayoutWaist.background =
                resources.getDrawable(R.drawable.ic_process_complete_bg, null)
        }

        Log.d("HIP_WAIST_HOME","DATA:" + hipData.toString()  + ", AND " + waistData)
        binding.errorView.collapse()

        viewModel.sampleMangementPocess?.observe(this, Observer { sampleMangementPocess ->
            // Timber.d(sampleMangementPocess.toString())
            if (sampleMangementPocess?.status == Status.SUCCESS) {
                activity!!.finish()
            } else if (sampleMangementPocess?.status == Status.ERROR) {
                binding.progressBar.visibility = View.GONE
                binding.buttonSubmit.visibility = View.VISIBLE
                //Crashlytics.logException(Exception(sampleMangementPocess.message?.message))
                //var error = accessToken.dat
            }
        })

        viewModel.hipMeasurementMetaOffline?.observe(this, Observer { sampleMangementPocess ->

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
                    HipWaistRequest(meta = participantRequest?.meta!!, body = hipWaistData).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
            }
        })




        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest, "contraindications" to getContraindications())
            reasonDialogFragment.show(fragmentManager!!)
        }

        binding.buttonSubmit.singleClick {

            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (hipData != null && waistData != null) {
                hipWaistData = HipWaistTests(
                    hip = hipData,
                    waist = waistData,
                    comment = binding.comment.text.toString(),
                    device_id = selectedDeviceID)

                hipWaistData!!.contraindications = getContraindications()

                Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                Log.d("HOME_FRAG", "AFTER_ASSING: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val hipWaistRequest = HipWaistRequest(meta = participantRequest?.meta, body = hipWaistData)
                hipWaistRequest.screeningId = participantRequest?.screeningId!!
                if(isNetworkAvailable()){
                    hipWaistRequest.syncPending =false
                }else{
                    hipWaistRequest.syncPending =true

                }

                viewModel.setHipMeasurementMeta(hipWaistRequest)

                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSubmit.visibility = View.GONE

            } else {
                binding.errorView.expand()
                binding.sampleValidationError = true
                if (hipData == null) {
                    updateProcessErrorUI(binding.hipTextView)
                }

                if (waistData == null) {
                    updateProcessErrorUI(binding.waistTextView)

                }
                binding.executePendingBindings()
            }
        }

        binding.linearLayoutHip.singleClick {

            binding.sampleValidationError = false
            updateProcessValidUI(binding.hipTextView)
            updateProcessValidUI(binding.waistTextView)

            val bundle = Bundle()
            bundle.putParcelable("ParticipantRequest", participantRequest)
            if (hipData != null)
            {
                bundle.putParcelable("HipData", hipData)
            }
            navController().navigate(R.id.action_HipWaistHomeFragment_to_HipFragment, bundle)
        }



        binding.linearLayoutWaist.singleClick {
            binding.sampleValidationError = false
            updateProcessValidUI(binding.hipTextView)
            updateProcessValidUI(binding.waistTextView)

            val bundle = Bundle()
            bundle.putParcelable("ParticipantRequest", participantRequest)
            if (waistData != null)
            {
                bundle.putParcelable("WaistData", waistData)
            }
            navController().navigate(R.id.action_HipWaistHomeFragment_to_WaistFragment, bundle)
        }

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.hip)
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(HipWaistQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications: MutableList<Map<String, String>> = mutableListOf()

        val haveColostomy = questionnaireViewModel.haveColostomy.value
        val haveUnaided = questionnaireViewModel.haveUnaided.value

        val unaidedMap = mutableMapOf<String, String>()
        unaidedMap["id"] = "WHCI1"
        unaidedMap["question"] = getString(R.string.hip_waist_unaided_question)
        unaidedMap["answer"] = if (haveUnaided!!) "yes" else "no"

        contraindications.add(unaidedMap)

        val colostomyMap = mutableMapOf<String, String>()
        colostomyMap["id"] = "WHCI2"
        colostomyMap["question"] = getString(R.string.hip_waist_colostomy_question)
        colostomyMap["answer"] = if (haveColostomy!!) "yes" else "no"

        contraindications.add(colostomyMap)

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

//    private fun validateDifference():Boolean
//    {
//        val intHipArray = hipData!!.value!!.map(String::toInt)
//        val intWaistArray = waistData!!.value!!.map(String::toInt)
//
//        val maxHip = intHipArray.max() ?: 0
//        val maxWaist = intWaistArray.max() ?:0
//
//        val minHip = intHipArray.min() ?: 0
//        val minWaist = intWaistArray.min() ?:0
//
//        val minimumWaist = maxWaist - minWaist
//        val minimumHip = maxHip - minHip
//        val minimumWaistHip = maxWaist - minHip
//        val minimumHipWaist = maxHip - minWaist
//
//        Log.d("HIPWAISTHOME" , "DATA: " + " MIN_WAIST: " + minimumWaist + "min_hip: " + "min_Waist_hip: " + minimumWaistHip + "min_hip_waist: " + minimumHipWaist)
//
//        if ((minimumWaist > 5) || (minimumHip > 5) || (minimumWaistHip > 5) || (minimumHipWaist > 5))
//        {
//            // Dialog
//            return false
//        }
//
//        return true
//    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()


}
