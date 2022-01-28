package org.singapore.ghru.ui.spirometry.tests


//import com.nuvoair.sdk.launcher.LauncherEthnicityType
//import com.nuvoair.sdk.launcher.LauncherSexType
//import com.nuvoair.sdk.launcher.NuvoairLauncherManager
//import com.nuvoair.sdk.launcher.NuvoairLauncherProfile
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.google.gson.GsonBuilder
import com.nuvoair.sdk.launcher.*
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.SpirometryTestsFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.SpirometryDeviceRecordTestRxBus
import org.singapore.ghru.event.SpirometryListRecordTestRxBus
import org.singapore.ghru.event.SpirometryRecordTestRxBus
import org.singapore.ghru.ui.spirometry.cancel.CancelDialogFragment
import org.singapore.ghru.ui.spirometry.questionnaire.SpiroQuestionnaireViewModel
import org.singapore.ghru.ui.spirometry.tests.checkmeasurement.CheckMeasurementDialogFragment
import org.singapore.ghru.ui.spirometry.tests.completed.CompletedDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.*
//import org.southasia.ghru.vo.Date
import org.singapore.ghru.vo.request.BodyMeasurementMeta
import org.singapore.ghru.vo.request.BodyMeasurementMeta1
import org.singapore.ghru.vo.request.ParticipantRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import kotlin.collections.ArrayList

class TestFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager

    var binding by autoCleared<SpirometryTestsFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var participant: ParticipantRequest? = null

    private lateinit var linearLayoutManager: LinearLayoutManager

    private var recordList: ArrayList<SpirometryRecord> = ArrayList()
    private lateinit var adapter: TestRecordAdapter
    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager


    @Inject
    lateinit var viewModel: TestModel

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    var bodyMeasurementMeta: BodyMeasurementMeta? = null

    private lateinit var questionnaireViewModel: SpiroQuestionnaireViewModel

    // lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    var nuvoairLauncherMeasurement: NuvoairLauncherMeasurement? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<SpirometryTestsFragmentBinding>(
            inflater,
            R.layout.spirometry_tests_fragment,
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

        adapter = TestRecordAdapter(recordList)
        binding.recyclerView.adapter = adapter


        disposables.add(
            SpirometryRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->

                    Timber.d(result.toString())
                    if (!recordList.contains(result)) {
                        recordList.add(result)
                        adapter.notifyDataSetChanged()
                    }

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            SpirometryListRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Timber.d(result.toString())
                    //  if (!recordList.contains(result)) {
                    recordList.addAll(result)
                    adapter.notifyDataSetChanged()
                    //  }

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )

        disposables.add(
            SpirometryDeviceRecordTestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    nuvoairLauncherMeasurement = result
                    result.sessions.forEach {
                        val sp = SpirometryRecord()
                        sp.fev.value = it.feV1.toString()
                        sp.fvc.value = it.fvc.toString()
                        sp.ratio.value = it.ratio.toString()
                        sp.pEFR.value = it.pef.toString()

                        var error = it.errors
                        recordList.add(sp)
                    }
                    adapter.notifyDataSetChanged()

                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )


        deviceListName.clear();
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter)
        viewModel.setStationName(Measurements.SPIROMETRY)
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
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participant

        validateNextButton()

        Log.d("TEST_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        viewModel.sync?.observe(this, Observer { commonResponce ->


            if (commonResponce?.status == Status.SUCCESS) {
                //println(user)
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (commonResponce?.status == Status.ERROR) {
                //Crashlytics.logException(Exception(commonResponce.message?.message))
                Crashlytics.setString("bodyMeasurementMeta", bodyMeasurementMeta.toString())
                Crashlytics.setString("recordList", recordList.toString())
                Crashlytics.logException(Exception("sample collection " + commonResponce.message.toString()))
                Timber.d(commonResponce.message?.message)
            }
        })

        viewModel.setParticipant(participant!!, isNetworkAvailable())

        viewModel.bodyMeasurementMeta?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                bodyMeasurementMeta = participant.data!!.data!!.station
                Log.d("TEST_FRAGMENT", "BODY_MEASUREMENT_META_DATA: Success")
            } else if (participant?.status == Status.ERROR) {
                Log.d("TEST_FRAGMENT", "BODY_MEASUREMENT_META_DATA: Failed")
            }

        })


        viewModel.spirometryRequest?.observe(this, Observer { commonResponce ->


            if (commonResponce?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(commonResponce?.status == Status.ERROR){
                Crashlytics.setString("bodyMeasurementMeta", bodyMeasurementMeta.toString())
                Crashlytics.setString("recordList", recordList.toString())
                Crashlytics.logException(Exception("sample collection " + commonResponce.message.toString()))
                Timber.d(commonResponce.message?.message)
            }
        })

        binding.completeButton.singleClick {
            
            if(selectedDeviceID==null)
            {
                binding.textViewDeviceError.visibility = View.VISIBLE
            }
            else if (recordList.count() > 0) {

                Log.d("TEST_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participant?.meta?.endTime = endDateTime

                Log.d("TEST_FRAG", "AFTER_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                binding.linearLayoutErrorMessage.collapse()
                Timber.d(recordList.toString())
//                if (isNetworkAvailable()) {
//                    viewModel.setData(
//                        participant,
//                        recordList,
//                        binding.comment.text.toString(),
//                        selectedDeviceID,
//                        binding.textFieldTurbineID.text.toString(),
//                        nuvoairLauncherMeasurement = nuvoairLauncherMeasurement
//                    )
//                } else {
//
//                    val mSpirometryTesList = ArrayList<SpirometryTest>()
//                    recordList.forEachIndexed { index, spirometryRecord ->
//                        mSpirometryTesList.add(
//                            SpirometryTest(
//                                testNumber = index,
//                                fev = spirometryRecord.fev.value.toString(),
//                                fvc = spirometryRecord.fvc.value.toString(),
//                                ratio = spirometryRecord.ratio.value.toString(),
//                                pev = spirometryRecord.pEFR.value.toString()
//                            )
//                        )
//                    }
//                    val mSpirometryTests = SpirometryTests(
//                        tests = mSpirometryTesList,
//                        device_id = selectedDeviceID,
//                        turbine_id = binding.textFieldTurbineID.text.toString(),
//                        deviceData = nuvoairLauncherMeasurement
//                    )
//                    val mSpirometryData = SpirometryData(body = mSpirometryTests)
//                    val gson = GsonBuilder().setPrettyPrinting().create()
//                    val mSpirometryRequest = SpirometryRequest(
//                        data = gson.toJson(mSpirometryData),
//                        comment = binding.comment.text.toString(),
//                        meta = participant?.meta
//                    )
//                    //jobManager.addJobInBackground(SyncSpirometryJob(participant, mSpirometryRequest))
//
//                }


                val mSpirometryTesList = ArrayList<SpirometryTest>()
                recordList.forEachIndexed { index, spirometryRecord ->
                    mSpirometryTesList.add(
                        SpirometryTest(
                            testNumber = index,
                            fev = spirometryRecord.fev.value.toString(),
                            fvc = spirometryRecord.fvc.value.toString(),
                            ratio = spirometryRecord.ratio.value.toString(),
                            pev = spirometryRecord.pEFR.value.toString()
                        )
                    )
                }
                val mSpirometryTests = SpirometryTests(
                    tests = mSpirometryTesList,
                    device_id = selectedDeviceID,
                    deviceData = nuvoairLauncherMeasurement,
                    contraindications = getContraindications()
                )
                val mSpirometryData = SpirometryData(body = mSpirometryTests)
                val gson = GsonBuilder().setPrettyPrinting().create()
                val mSpirometryRequest = SpirometryRequest(
                    data = gson.toJson(mSpirometryData),
                    comment = binding.comment.text.toString(),
                    meta = participant?.meta
                )
                mSpirometryRequest.screeningId = participant?.screeningId!!
                mSpirometryRequest.syncPending = !isNetworkAvailable()

                if (recordList.count() >= 3)
                {
                    mSpirometryRequest.partially_able = "No"
                    viewModel.setSpirometryRequest(mSpirometryRequest)
                }
                else
                {
                    mSpirometryRequest.partially_able = "Yes"
                    val reasonDialogFragment = CheckMeasurementDialogFragment()
                    reasonDialogFragment.arguments = bundleOf(
                        "spiroRequest" to mSpirometryRequest

                    )
                    reasonDialogFragment.show(fragmentManager!!)
                }
            }
            else
            {
                Toast.makeText(activity!!, "Please add at least one measurement", Toast.LENGTH_LONG).show()
            }
        }

//        binding.previousButton.setOnClickListener {
//
//            navController().popBackStack()
//
//        }
        binding.buttonAddTest.setOnClickListener {

//            if (bodyMeasurementMeta?.body?.height != null && bodyMeasurementMeta?.body?.bodyComposition != null)
//            {
//                binding.linearLayoutErrorMessage.collapse()
//                navController().navigate(R.id.action_testFragment_to_recordFragment)
//            }
//            else
//            {
//                Toast.makeText(activity!!, "Height and weight mandatory for this station ", Toast.LENGTH_LONG)
//                    .show()
//            }

            binding.linearLayoutErrorMessage.collapse()
            navController().navigate(R.id.action_testFragment_to_recordFragment)

        }
        binding.buttonCancel.setOnClickListener {
            val cancelDialogFragment = CancelDialogFragment()
            cancelDialogFragment.arguments = bundleOf(
                "participant" to participant,
                "contraindications" to getContraindications(),
                "skipped" to false)
            cancelDialogFragment.show(fragmentManager!!)
        }

        binding.connectButton.singleClick {
            startTest()
        }
        if(isAinaPackageAvailable(context!!))
        {
            binding.imgWarning.visibility = View.GONE
            binding.txtWarning.visibility = View.GONE

            binding.connectButton.visibility = View.VISIBLE;
        }
        else
        {
            binding.imgWarning.visibility = View.VISIBLE
            binding.txtWarning.visibility = View.VISIBLE

            binding.connectButton.visibility = View.GONE;
        }

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->

            Log.d("TEST_CHECK_OUT", "DATA: " + recordList.size)

            if (recordList.size >0)
            {
                Log.d("TEST_CHECK_IN", "DATA: " + recordList.size)
                viewModel.setPartiallyAble(isChecked)
                //binding.progressBar.visibility = View.VISIBLE
                validateNextButton()
            }
            else
            {
                Toast.makeText(activity!!, "Please add at least one measurement", Toast.LENGTH_LONG).show()

            }

            viewModel.setPartiallyAble(isChecked)
            //binding.progressBar.visibility = View.VISIBLE
            validateNextButton()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(SpiroQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun validateNextButton() {

        if (recordList.count() >= 3) {
            binding.completeButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.completeButton.setDrawableRightColor("#0A1D53")
            binding.completeButton.isEnabled = true
        }
        else if (recordList.count() >= 1)
        {

            if (viewModel.hasPartiallyAble.value != null && viewModel.hasPartiallyAble.value!!)
            {
                binding.completeButton.setTextColor(Color.parseColor("#0A1D53"))
                binding.completeButton.setDrawableRightColor("#0A1D53")
                binding.completeButton.isEnabled = true
            }
            else
            {
                binding.completeButton.setTextColor(Color.parseColor("#AED6F1"))
                binding.completeButton.setDrawableRightColor("#AED6F1")
                binding.completeButton.isEnabled = false
            }

        }
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications: MutableList<Map<String, String>> = mutableListOf()

        val haveRetina = questionnaireViewModel.haveRetina.value
        val haveEyeSurgery = questionnaireViewModel.haveEyeSurgery.value
        val haveOther = questionnaireViewModel.haveOtherSurgery.value
        val haveMycardial = questionnaireViewModel.haveMyocardialInfarction.value
        val haveStroke = questionnaireViewModel.haveSufferedStroke.value
        val haveChestInfection = questionnaireViewModel.haveChestInfection.value
        val haveTuberculosis = questionnaireViewModel.haveTuberculosis.value
        val havePneumothorax = questionnaireViewModel.havePneumothorax.value

        val retinaMap = mutableMapOf<String, String>()
        retinaMap["id"] = "SPCI1"
        retinaMap["question"] = getString(R.string.spiro_retina_question)
        retinaMap["answer"] = if (haveRetina!!) "yes" else "no"
        contraindications.add(retinaMap)

        val eyeSurgeryMap = mutableMapOf<String, String>()
        eyeSurgeryMap["id"] = "SPCI2"
        eyeSurgeryMap["question"] = getString(R.string.spiro_eye_surgery_question)
        eyeSurgeryMap["answer"] = if (haveEyeSurgery!!) "yes" else "no"
        contraindications.add(eyeSurgeryMap)

        val otherSurgeryMap = mutableMapOf<String, String>()
        otherSurgeryMap["id"] = "SPCI3"
        otherSurgeryMap["question"] = getString(R.string.spiro_other_surgery_question)
        otherSurgeryMap["answer"] = if (haveOther!!) "yes" else "no"
        contraindications.add(otherSurgeryMap)

        val mycardialMap = mutableMapOf<String, String>()
        mycardialMap["id"] = "SPCI4"
        mycardialMap["question"] = getString(R.string.spiro_myocardial_infarction_question)
        mycardialMap["answer"] = if (haveMycardial!!) "yes" else "no"
        contraindications.add(mycardialMap)

        val strokeMap = mutableMapOf<String, String>()
        strokeMap["id"] = "SPCI5"
        strokeMap["question"] = getString(R.string.spiro_stroke_question)
        strokeMap["answer"] = if (haveStroke!!) "yes" else "no"
        contraindications.add(strokeMap)

        val chestMap = mutableMapOf<String, String>()
        chestMap["id"] = "SPCI6"
        chestMap["question"] = getString(R.string.spiro_chest_infection_question)
        chestMap["answer"] = if (haveChestInfection!!) "yes" else "no"
        contraindications.add(chestMap)

        val tbMap = mutableMapOf<String, String>()
        tbMap["id"] = "SPCI7"
        tbMap["question"] = getString(R.string.spiro_tuberculosis_question)
        tbMap["answer"] = if (haveTuberculosis!!) "yes" else "no"
        contraindications.add(tbMap)

        val pneumothoraxMap = mutableMapOf<String, String>()
        pneumothoraxMap["id"] = "SPCI8"
        pneumothoraxMap["question"] = getString(R.string.spiro_pneumothorax_question)
        pneumothoraxMap["answer"] = if (havePneumothorax!!) "yes" else "no"
        contraindications.add(pneumothoraxMap)

        return contraindications
    }

    private fun startTest() = if (bodyMeasurementMeta?.body != null) {
        val gender: LauncherSexType = if (participant?.gender.equals("male")) {
            LauncherSexType.MALE
        } else {
            LauncherSexType.FEMALE
        }
        if (bodyMeasurementMeta?.body?.height != null && bodyMeasurementMeta?.body?.bodyComposition != null) {
            try {
                NuvoairLauncherManager.getInstance().launch(
                    activity,
                    NuvoairLauncherProfile(
                        participant?.screeningId, // name
                        participant?.age?.dob, // birth day
                        bodyMeasurementMeta?.body?.height?.value?.toInt()!!, //hight
                        bodyMeasurementMeta?.body?.bodyComposition?.value?.toInt()!!, //waight KG
                        LauncherEthnicityType.OTHER,
                        gender

                    )
                )

            } catch (e: NuvoairLauncherException) {
                Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Toast.makeText(activity!!, e.message, Toast.LENGTH_LONG).show()

            }
        } else {
            Toast.makeText(activity!!, "Height and weight mandatory for this station ", Toast.LENGTH_LONG)
                .show()
        }

    } else {
        Toast.makeText(activity!!, getString(R.string.spirometry_error), Toast.LENGTH_LONG).show()
    }

    fun isAinaPackageAvailable(context: Context): Boolean {
        val packages: List<ApplicationInfo>
        val pm: PackageManager
        pm = context.getPackageManager()
        packages = pm.getInstalledApplications(0)
        for (packageInfo in packages) {
            if (packageInfo.packageName.contains("se.pond.air"))
                return true
        }
        return false
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

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
}

interface TestRecordCallback {
    fun onRecordReady(fev: String)

}