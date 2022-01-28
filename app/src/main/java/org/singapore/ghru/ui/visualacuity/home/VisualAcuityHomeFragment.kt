package org.singapore.ghru.ui.visualacuity.home


import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
import org.singapore.ghru.databinding.VisualAcuityHomeFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.*
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.visualacuity.reason.ReasonDialogFragment
import org.singapore.ghru.ui.visualacuity.contraindication.VisualAcuityQuestionnaireViewModel
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject


class VisualAcuityHomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: VisualAcuityHomeFragmentBinding


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: VisualAcuityHomeViewModel

    private var sampleRequest: SampleRequest? = null

    private val disposables = CompositeDisposable()

    @Inject
    lateinit var jobManager: JobManager

//    private var hipData: HipWaistData? = null
//    private var waistData: HipWaistData? = null
//    private var hipWaistData: HipWaistTests? = null

    private var leftEyeData: VisualAcuityDataNew? = null
    private var rightEyeData: VisualAcuityDataNew? = null
    private var visualAcuityData: VisualAcuityTests? = null

    private var participantRequest: ParticipantRequest? = null
    private var visualAidValue: String? = null
    private var imageExported: Boolean? = false

    private var isPartial : Boolean? = false

    //var user: User? = null
    //var meta: Meta? = null

    private lateinit var questionnaireViewModel: VisualAcuityQuestionnaireViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            visualAidValue = arguments?.getString("visualAid")!!
//            imageExported = arguments?.getBoolean("imageExported")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

//        disposables.add(
//            LeftEyeRecordTestRxBus.getInstance().toObservable()
//                .subscribe({ result ->
//
//                    Timber.d(result.toString())
//                    leftEyeData = result
//
//                }, { error ->
//                    print(error)
//                    error.printStackTrace()
//                })
//        )
//
//        disposables.add(
//            RightEyeRecordTestRxBus.getInstance().toObservable()
//                .subscribe({ result ->
//
//                    Timber.d(result.toString())
//                    rightEyeData = result
//
//                }, { error ->
//                    print(error)
//                    error.printStackTrace()
//                })
//        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<VisualAcuityHomeFragmentBinding>(
            inflater,
            R.layout.visual_acuity_home_fragment,
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

        Log.d("VISUAL_ACUITY_HOME","DATA:" + visualAidValue.toString())
        binding.errorView.collapse()

        viewModel.visualMeasurementMetaOffline?.observe(this, Observer { sampleMangementPocess ->

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
                    "VisualAcuityMeasurementMeta",
                    VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData).toString()
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

            if (binding.leftNOREditText.text.toString().equals("0") && binding.rightNOREditText.text.toString().equals("0"))
            {
                if (validateLeftRow(binding.leftNOREditText.text.toString())
                    && validateRightRow(binding.rightNOREditText.text.toString())
                ) {
                    leftEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.leftNOREditText.text.toString(),
                        number_of_letters = binding.leftNOLEditText.text.toString(),
                        logmar_value = binding.leftLogmarEditText.text.toString())

                    rightEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.rightNOREditText.text.toString(),
                        number_of_letters = binding.rightNOLEditText.text.toString(),
                        logmar_value = binding.rightLogmarEditText.text.toString())

                    visualAcuityData = VisualAcuityTests(
                        left_eye = leftEyeData,
                        right_eye = rightEyeData,
                        comment = binding.comment.text.toString(),
                        visual_aid = visualAidValue
                    )

                    visualAcuityData!!.isPartialSubmission = isPartial!!

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                    visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        visualAcuityRequest.syncPending =false
                    }else{
                        visualAcuityRequest.syncPending =true

                    }

                    viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                } else {
                    binding.errorView.expand()
                    binding.visualAcuityError.setText(getString(R.string.error_visual_acuity))
                    binding.sampleValidationError = true
                    binding.executePendingBindings()
                }
            }
            else if (!binding.leftNOREditText.text.toString().equals("0") && binding.rightNOREditText.text.toString().equals("0")&& !binding.checkbox.isChecked)
            {
                if (validateLeftRow(binding.leftNOREditText.text.toString()) &&
                    validateLeftLetter(binding.leftNOLEditText.text.toString()) &&
                    validateRightRow(binding.rightNOREditText.text.toString())
                ) {
                    leftEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.leftNOREditText.text.toString(),
                        number_of_letters = binding.leftNOLEditText.text.toString(),
                        logmar_value = binding.leftLogmarEditText.text.toString())

                    rightEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.rightNOREditText.text.toString(),
                        number_of_letters = binding.rightNOLEditText.text.toString(),
                        logmar_value = binding.rightLogmarEditText.text.toString())

                    visualAcuityData = VisualAcuityTests(
                        left_eye = leftEyeData,
                        right_eye = rightEyeData,
                        comment = binding.comment.text.toString(),
                        visual_aid = visualAidValue
                    )
                    //visualAcuityData!!.contraindications = getContraindications()
                    visualAcuityData!!.isPartialSubmission = isPartial!!

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                    visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        visualAcuityRequest.syncPending =false
                    }else{
                        visualAcuityRequest.syncPending =true

                    }

                    viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                } else {
                    binding.errorView.expand()
                    binding.visualAcuityError.setText(getString(R.string.error_visual_acuity))
                    binding.sampleValidationError = true
                    binding.executePendingBindings()
                }
            }
            else if (binding.leftNOREditText.text.toString().equals("0") && !binding.rightNOREditText.text.toString().equals("0") && !binding.checkbox.isChecked)
            {
                if (validateLeftRow(binding.leftNOREditText.text.toString()) &&
                    validateRightRow(binding.rightNOREditText.text.toString()) &&
                    validateRightLetter(binding.rightNOLEditText.text.toString())
                ) {
                    leftEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.leftNOREditText.text.toString(),
                        number_of_letters = binding.leftNOLEditText.text.toString(),
                        logmar_value = binding.leftLogmarEditText.text.toString())

                    rightEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.rightNOREditText.text.toString(),
                        number_of_letters = binding.rightNOLEditText.text.toString(),
                        logmar_value = binding.rightLogmarEditText.text.toString())

                    visualAcuityData = VisualAcuityTests(
                        left_eye = leftEyeData,
                        right_eye = rightEyeData,
                        comment = binding.comment.text.toString(),
                        visual_aid = visualAidValue
                    )
                    //visualAcuityData!!.contraindications = getContraindications()
                    visualAcuityData!!.isPartialSubmission = isPartial!!

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                    visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        visualAcuityRequest.syncPending =false
                    }else{
                        visualAcuityRequest.syncPending =true

                    }

                    viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                } else {
                    binding.errorView.expand()
                    binding.visualAcuityError.setText(getString(R.string.error_visual_acuity))
                    binding.sampleValidationError = true
                    binding.executePendingBindings()
                }
            }
            else if(binding.checkbox.isChecked) // partial submission
            {
                if (binding.rightNOREditText.text.toString().equals("0") && binding.leftNOREditText.text.toString().isEmpty())
                {
                    if (validateRightRow(binding.rightNOREditText.text.toString()))
                    {
                        leftEyeData = VisualAcuityDataNew(
                            number_of_rows = "NA",
                            number_of_letters = "NA",
                            logmar_value = "NA")

                        rightEyeData = VisualAcuityDataNew(
                            number_of_rows = binding.rightNOREditText.text.toString(),
                            number_of_letters = binding.rightNOLEditText.text.toString(),
                            logmar_value = binding.rightLogmarEditText.text.toString())

                        visualAcuityData = VisualAcuityTests(
                            left_eye = leftEyeData,
                            right_eye = rightEyeData,
                            comment = binding.comment.text.toString(),
                            visual_aid = visualAidValue
                        )
                        //visualAcuityData!!.contraindications = getContraindications()
                        visualAcuityData!!.isPartialSubmission = isPartial!!

                        Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime:String = endDate + " " + endTime

                        participantRequest?.meta?.endTime = endDateTime

                        Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                        val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                        visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                        if(isNetworkAvailable()){
                            visualAcuityRequest.syncPending =false
                        }else{
                            visualAcuityRequest.syncPending =true

                        }

                        viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonSubmit.visibility = View.GONE

                    } else {
                        binding.errorView.expand()
                        binding.visualAcuityError.setText(getString(R.string.error_visual_acuity))
                        binding.sampleValidationError = true
                        binding.executePendingBindings()
                    }
                }
                else if (binding.rightNOREditText.text.toString().isEmpty() && binding.leftNOREditText.text.toString().equals("0"))
                {
                    if (validateLeftRow(binding.leftNOREditText.text.toString())
                    ) {
                        leftEyeData = VisualAcuityDataNew(
                            number_of_rows = binding.leftNOREditText.text.toString(),
                            number_of_letters = binding.leftNOLEditText.text.toString(),
                            logmar_value = binding.leftLogmarEditText.text.toString()
                        )

                        rightEyeData = VisualAcuityDataNew(
                            number_of_rows = "NA",
                            number_of_letters = "NA",
                            logmar_value = "NA"
                        )

                        visualAcuityData = VisualAcuityTests(
                            left_eye = leftEyeData,
                            right_eye = rightEyeData,
                            comment = binding.comment.text.toString(),
                            visual_aid = visualAidValue
                        )
                        //visualAcuityData!!.contraindications = getContraindications()
                        visualAcuityData!!.isPartialSubmission = isPartial!!

                        Log.d(
                            "HOME_FRAG",
                            "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime
                        )

                        val endTime: String = convertTimeTo24Hours()
                        val endDate: String = getDate()
                        val endDateTime: String = endDate + " " + endTime

                        participantRequest?.meta?.endTime = endDateTime

                        Log.d(
                            "HOME_FRAG",
                            "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime
                        )

                        val visualAcuityRequest = VisualAcuityRequest(
                            meta = participantRequest?.meta,
                            body = visualAcuityData
                        )
                        visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                        if (isNetworkAvailable()) {
                            visualAcuityRequest.syncPending = false
                        } else {
                            visualAcuityRequest.syncPending = true

                        }

                        viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                        binding.progressBar.visibility = View.VISIBLE
                        binding.buttonSubmit.visibility = View.GONE

                    } else {
                        binding.errorView.expand()
                        binding.visualAcuityError.setText(getString(R.string.error_visual_acuity))
                        binding.sampleValidationError = true
                        binding.executePendingBindings()
                    }
                }
                else if ((validateLeftRow(binding.leftNOREditText.text.toString()) &&
                    validateLeftLetter(binding.leftNOLEditText.text.toString())) &&
                    !(validateRightRow(binding.rightNOREditText.text.toString()) &&
                    validateRightLetter(binding.rightNOLEditText.text.toString()))
                ) {
                    leftEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.leftNOREditText.text.toString(),
                        number_of_letters = binding.leftNOLEditText.text.toString(),
                        logmar_value = getLogMarValue(binding.leftNOREditText.text.toString().toInt(), binding.leftNOLEditText.text.toString().toInt()))

                    rightEyeData = VisualAcuityDataNew(
                        number_of_rows = "NA",
                        number_of_letters = "NA",
                        logmar_value = "NA")

                    visualAcuityData = VisualAcuityTests(
                        left_eye = leftEyeData,
                        right_eye = rightEyeData,
                        comment = binding.comment.text.toString(),
                        visual_aid = visualAidValue
                    )
                    //visualAcuityData!!.contraindications = getContraindications()
                    visualAcuityData!!.isPartialSubmission = isPartial!!

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                    visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        visualAcuityRequest.syncPending =false
                    }else{
                        visualAcuityRequest.syncPending =true

                    }

                    viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                }
                else if (!(validateLeftRow(binding.leftNOREditText.text.toString()) &&
                            validateLeftLetter(binding.leftNOLEditText.text.toString())) &&
                    (validateRightRow(binding.rightNOREditText.text.toString()) &&
                            validateRightLetter(binding.rightNOLEditText.text.toString()))
                ) {
                    leftEyeData = VisualAcuityDataNew(
                        number_of_rows = "NA",
                        number_of_letters = "NA",
                        logmar_value = "NA")

                    rightEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.rightNOREditText.text.toString(),
                        number_of_letters = binding.rightNOLEditText.text.toString(),
                        logmar_value = getLogMarValue(binding.rightNOREditText.text.toString().toInt(), binding.rightNOLEditText.text.toString().toInt()))

                    visualAcuityData = VisualAcuityTests(
                        left_eye = leftEyeData,
                        right_eye = rightEyeData,
                        comment = binding.comment.text.toString(),
                        visual_aid = visualAidValue
                    )
                    //visualAcuityData!!.contraindications = getContraindications()
                    visualAcuityData!!.isPartialSubmission = isPartial!!

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                    visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        visualAcuityRequest.syncPending =false
                    }else{
                        visualAcuityRequest.syncPending =true

                    }

                    viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                }
                else {
                    binding.errorView.expand()
                    binding.visualAcuityError.setText(getString(R.string.error_visual_acuity_1))
                    binding.sampleValidationError = true
                    binding.executePendingBindings()
                }
            }
            else // both eye readings
            {
                if (validateLeftRow(binding.leftNOREditText.text.toString()) &&
                            validateLeftLetter(binding.leftNOLEditText.text.toString()) &&
                            validateRightRow(binding.rightNOREditText.text.toString()) &&
                            validateRightLetter(binding.rightNOLEditText.text.toString())
                ) {
                    leftEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.leftNOREditText.text.toString(),
                        number_of_letters = binding.leftNOLEditText.text.toString(),
                        logmar_value = getLogMarValue(binding.leftNOREditText.text.toString().toInt(), binding.leftNOLEditText.text.toString().toInt()))

                    rightEyeData = VisualAcuityDataNew(
                        number_of_rows = binding.rightNOREditText.text.toString(),
                        number_of_letters = binding.rightNOLEditText.text.toString(),
                        logmar_value = getLogMarValue(binding.rightNOREditText.text.toString().toInt(), binding.rightNOLEditText.text.toString().toInt()))


                    visualAcuityData = VisualAcuityTests(
                        left_eye = leftEyeData,
                        right_eye = rightEyeData,
                        comment = binding.comment.text.toString(),
                        visual_aid = visualAidValue
                    )
                    //visualAcuityData!!.contraindications = getContraindications()
                    visualAcuityData!!.isPartialSubmission = isPartial!!

                    Log.d("HOME_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participantRequest?.meta?.endTime = endDateTime

                    Log.d("HOME_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                    val visualAcuityRequest = VisualAcuityRequest(meta = participantRequest?.meta, body = visualAcuityData)
                    visualAcuityRequest.screeningId = participantRequest?.screeningId!!
                    if(isNetworkAvailable()){
                        visualAcuityRequest.syncPending =false
                    }else{
                        visualAcuityRequest.syncPending =true

                    }

                    viewModel.setVisualMeasurementMeta(visualAcuityRequest)

                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonSubmit.visibility = View.GONE

                }
                else
                {
                    Toast.makeText(activity!!, "Please check on partial submission", Toast.LENGTH_LONG).show()
                }
            }
        }

        onTextChanges(binding.leftNOREditText)
        onTextChanges(binding.leftNOLEditText)
        onTextChanges(binding.rightNOREditText)
        onTextChanges(binding.rightNOLEditText)

        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check, null)
            isPartial = isChecked
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(VisualAcuityQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications: MutableList<Map<String, String>> = mutableListOf()

        val haveEyeOccluded = questionnaireViewModel.haveEyeOccluded.value

        val colostomyMap = mutableMapOf<String, String>()
        colostomyMap["id"] = "VACI1"
        colostomyMap["question"] = getString(R.string.visual_acuity_question)
        colostomyMap["answer"] = if (haveEyeOccluded!!) "yes" else "no"
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

    private fun validateNextButton(): Boolean {
        if(viewModel.haveImageExport.value == null) {
            binding.radioGroupImageExportValue = true
            binding.executePendingBindings()
            return false
        }
        else
        {
            return true
        }
    }

    private fun validateLeftRow(value: String): Boolean {

        try {
            if (binding.leftNOREditText.text.toString().isNotEmpty())
            {
                val rowValue: Double = value.toDouble()
                if (value.equals("0"))
                {
                    binding.leftNOLTextLayout.isEnabled = false
                    binding.leftNOLEditText.setText("")
                    binding.leftNORTextLayout.error = null
                    return true
                }
                else {
                    binding.leftNOLTextLayout.isEnabled  = true

                    if (rowValue >= Constants.VISUAL_ROW_MIN_VAL && rowValue <= Constants.VISUAL_ROW_MAX_VAL) {
                        binding.leftNORTextLayout.error = null
                        return true

                    } else {
                        binding.leftNORTextLayout.error = getString(R.string.error_not_in_range)
                        return false
                    }
                }
            }
            else
            {
                return false
            }
        } catch (e: Exception) {
            binding.leftNORTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }
    }

    private fun validateRightRow(value: String): Boolean {

        try {
            if (binding.rightNOREditText.text.toString().isNotEmpty())
            {
                val rowValue: Double = value.toDouble()

                if (value.equals("0"))
                {
                    binding.rightNOLTextLayout.isEnabled  = false
                    binding.rightNOLEditText.setText("")
                    binding.leftNORTextLayout.error = null
                    return true
                }
                else
                {
                    binding.rightNOLTextLayout.isEnabled  = true

                    if (rowValue >= Constants.VISUAL_ROW_MIN_VAL && rowValue <= Constants.VISUAL_ROW_MAX_VAL) {
                        binding.rightNORTextLayout.error = null
                        return true

                    } else {
                        binding.rightNORTextLayout.error = getString(R.string.error_not_in_range)
                        return false
                    }
                }
            }
            else
            {
                return false
            }
        } catch (e: Exception) {
            binding.rightNORTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }
    }

    private fun validateLeftLetter(value: String): Boolean {

        var maxValue: Double? = null

        try {
            if (binding.leftNOLEditText.text.toString().isNotEmpty())
            {
                val rowValue: Double = value.toDouble()
                if (binding.leftNOREditText.text.toString().equals("0"))
                {
                    return true
                }
                else
                {
                    if (binding.leftNOREditText.text.toString().equals("14") && binding.leftNOREditText.text.toString().isNotEmpty())
                    {
                        maxValue = 5.0
                    }
                    else
                    {
                        maxValue = 2.0
                    }

                    if (rowValue >= Constants.VISUAL_LETTER_MIN_VAL && rowValue <= maxValue) {
                        binding.leftNOLTextLayout.error = null
                        return true

                    } else {
                        binding.leftNOLTextLayout.error = getString(R.string.error_not_in_range)
                        return false
                    }
                }
            }
            else
            {
                return false
            }
        } catch (e: Exception) {
            binding.leftNOLTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }
    }

    private fun validateRightLetter(value: String): Boolean {

        var maxValue: Double? = null

        try {
            if (binding.rightNOLEditText.text.toString().isNotEmpty())
            {
                val rowValue: Double = value.toDouble()
                if (binding.rightNOREditText.text.toString().equals("0"))
                {
                    return true
                }
                else
                {
                    if (binding.rightNOREditText.text.toString().equals("14") && binding.rightNOREditText.text.toString().isNotEmpty())
                    {
                        maxValue = 5.0
                    }
                    else
                    {
                        maxValue = 2.0
                    }

                    if (rowValue >= Constants.VISUAL_LETTER_MIN_VAL && rowValue <= maxValue) {
                        binding.rightNOLTextLayout.error = null
                        return true

                    } else {
                        binding.rightNOLTextLayout.error = getString(R.string.error_not_in_range)
                        return false
                    }
                }
            }
            else
            {
                return false
            }
        } catch (e: Exception) {
            binding.rightNOLTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.leftNOREditText) {
                    validateLeftRow(binding.leftNOREditText.text.toString())
                }

                if(editText == binding.leftNOLEditText) {
                    validateLeftLetter(binding.leftNOLEditText.text.toString())
                }

                if(editText == binding.rightNOREditText) {
                    validateRightRow(binding.rightNOREditText.text.toString())
                }

                if(editText == binding.rightNOLEditText) {
                    validateRightLetter(binding.rightNOLEditText.text.toString())
                }

                if (binding.rightNOREditText.text.toString().equals("0"))
                {
                    binding.rightLogmarEditText.setText("NA")
                }
                else
                {
                    if ((binding.rightNOREditText.text.toString().isNotEmpty()
                                && binding.rightNOLEditText.text.toString().isNotEmpty())
                        && validateRightLetter(binding.rightNOLEditText.text.toString()) && validateRightRow(binding.rightNOREditText.text.toString()))
                    {
                        binding.rightLogmarEditText.setText(getLogMarValue(binding.rightNOREditText.text.toString().toInt(), binding.rightNOLEditText.text.toString().toInt()))
                    }
                }

                if (binding.leftNOREditText.text.toString().equals("0"))
                {
                    binding.leftLogmarEditText.setText("NA")
                }
                else
                {
                    if ((binding.leftNOREditText.text.toString().isNotEmpty() && binding.leftNOLEditText.text.toString().isNotEmpty())
                        && validateLeftLetter(binding.leftNOLEditText.text.toString()) && validateLeftRow(binding.leftNOREditText.text.toString()))
                    {
                        binding.leftLogmarEditText.setText(getLogMarValue(binding.leftNOREditText.text.toString().toInt(), binding.leftNOLEditText.text.toString().toInt()))
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun getLogMarValue(noOfRows: Int, noOfLetter: Int) : String
    {
        var logmarScore : String? = null
        if (noOfRows == 0)
        {
            logmarScore = "NA"

            return logmarScore
        }
        else
        {
            var selectedRowValue: Double? = null
            val rowArray : ArrayList<Double> = ArrayList()
            rowArray.add(1.0)
            rowArray.add(0.9)
            rowArray.add(0.8)
            rowArray.add(0.7)
            rowArray.add(0.6)
            rowArray.add(0.5)
            rowArray.add(0.4)
            rowArray.add(0.3)
            rowArray.add(0.2)
            rowArray.add(0.1)
            rowArray.add(0.0)
            rowArray.add(-0.1)
            rowArray.add(-0.2)
            rowArray.add(-0.3)

            if (noOfRows == 1)
            {
                selectedRowValue = rowArray.get(0)
            }
            else if (noOfRows == 2)
            {
                selectedRowValue = rowArray.get(1)
            }
            else if (noOfRows == 3)
            {
                selectedRowValue = rowArray.get(2)
            }
            else if (noOfRows == 4)
            {
                selectedRowValue = rowArray.get(3)
            }
            else if (noOfRows == 5)
            {
                selectedRowValue = rowArray.get(4)
            }
            else if (noOfRows == 6)
            {
                selectedRowValue = rowArray.get(5)
            }
            else if (noOfRows == 7)
            {
                selectedRowValue = rowArray.get(6)
            }
            else if (noOfRows == 8)
            {
                selectedRowValue = rowArray.get(7)
            }
            else if (noOfRows == 9)
            {
                selectedRowValue = rowArray.get(8)
            }
            else if (noOfRows == 10)
            {
                selectedRowValue = rowArray.get(9)
            }
            else if (noOfRows == 11)
            {
                selectedRowValue = rowArray.get(10)
            }
            else if (noOfRows == 12)
            {
                selectedRowValue = rowArray.get(11)
            }
            else if (noOfRows == 13)
            {
                selectedRowValue = rowArray.get(12)
            }
            else if (noOfRows == 14)
            {
                selectedRowValue = rowArray.get(13)
            }

            var corrctLetters = 5 - noOfLetter
            var letterValue = 0.02 * (corrctLetters)
            var loMarValue = selectedRowValue?.plus(letterValue.toDouble())

            val roundOff = Math.round(loMarValue!!.times(100.0)) / 100.0
            logmarScore = roundOff.toString()

            return logmarScore
        }
    }
}

