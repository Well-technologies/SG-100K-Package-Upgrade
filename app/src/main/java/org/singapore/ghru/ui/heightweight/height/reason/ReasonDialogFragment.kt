package org.singapore.ghru.ui.heightweight.height.reason

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.HeightReasonDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BodyMeasurementDataEventType
import org.singapore.ghru.event.BodyMeasurementDataResponse
import org.singapore.ghru.event.BodyMeasurementDataRxBus
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.contraindication.HeightWeightQuestionnaireViewModel
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.BodyMeasurementData
import org.singapore.ghru.vo.request.CancelRequest
import org.singapore.ghru.vo.request.CancelRequestWithMeta
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ReasonDialogFragment : DialogFragment(), Injectable {

    val TAG = ReasonDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<HeightReasonDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ReasonDialogViewModel


    lateinit var cancelRequest: CancelRequestWithMeta

    private var participant: ParticipantRequest? = null

    @Inject
    lateinit var jobManager: JobManager

    private lateinit var bodyMeasurementData: BodyMeasurementData
    private var existingComment: String? = null
    private var contraindications: List<Map<String, String>>? = null
    private var questions: List<Map<String, String>>? = null

    private lateinit var questionnaireViewModel: HeightWeightQuestionnaireViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            existingComment = arguments?.getString("comment")

            if (arguments?.getSerializable("contraindications") != null) {
                contraindications = arguments?.getSerializable("contraindications") as List<Map<String, String>>
            }

            if (arguments?.getSerializable("questions") != null) {
                questions = arguments?.getSerializable("questions") as List<Map<String, String>>
            }
        } catch (e: KotlinNullPointerException) {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<HeightReasonDialogFragmentBinding>(
            inflater,
            R.layout.height_reason_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cancelRequest = CancelRequestWithMeta()

        bodyMeasurementData = BodyMeasurementData()

        cancelRequest = CancelRequestWithMeta(stationType = "height-weight")


        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            // println("i $i" + radioGroup.checkedRadioButtonId)
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonParticipantFactor -> cancelRequest.reason =
                    getString(R.string.participant_factor)
                R.id.radioButtonTechnicalProblem -> cancelRequest.reason =
                    getString(R.string.technical_problem)
                R.id.radioButtonContra -> cancelRequest.reason =
                    getString(R.string.string_reason_contra)
                else -> {
                    // binding.textViewError.text = getString(R.string.app_error_please_select_one)
                    binding.textViewError.visibility = View.GONE
                }
            }
//            if (radioGroup.checkedRadioButtonId == R.id.radioButtonOther) {
//                binding.textInputEditTextOther.visibility = View.VISIBLE
//                binding.textInputEditTextOther.shoKeyboard()
//            } else {
//                binding.textInputEditTextOther.visibility = View.GONE
//            }
            binding.executePendingBindings()
        }

        if (contraindications != null) {
            binding.radioGroup.check(R.id.radioButtonContra)
        }

        binding.buttonAcceptAndContinue.singleClick {
            binding.root.hideKeyboard()
            if (binding.radioGroup.checkedRadioButtonId == -1) {
                binding.textViewError.text = getString(R.string.app_error_please_select_one)
                binding.textViewError.visibility = View.VISIBLE

            } else
//                if (binding.radioGroup.checkedRadioButtonId == R.id.radioButtonOther) {
//                cancelRequest.reason = binding.textInputEditTextOther.text.toString()
//            }
            if (binding.radioGroup.checkedRadioButtonId != -1) {

                var comment = binding.comment.text.toString()
                if(existingComment != null) {
                    comment = existingComment + "\n" + binding.comment.text.toString()
                }
                val eTime: String = convertTimeTo24Hours()
                val eDate: String = getDate()
                val eDateTime:String = eDate + " " + eTime
                cancelRequest.comment = comment

                cancelRequest.syncPending = !isNetworkAvailable()
                cancelRequest.screeningId = participant?.screeningId!!

//                if (contraindications != null) {
//                    cancelRequest.contraindications = contraindications
//                }
                if (questions != null)
                {
                    cancelRequest.questions = questions
                }
                cancelRequest.contraindications = getContraindications()
                participant!!.meta!!.endTime = eDateTime
                cancelRequest.meta = participant!!.meta

                Log.d("HEIGHT_REASON", "META_DATA:" + cancelRequest.meta)

                //viewModel.setLogin(participant, cancelRequest)

                viewModel.setCancelWithMeta(participant, cancelRequest)

//                cancelRequest.comment = binding.comment.text.toString()
//
//
//                bodyMeasurementData.skip = cancelRequest
//
//                cancelRequest.screeningId = participant?.screeningId!!
//                cancelRequest.syncPending = !isNetworkAvailable()
//                cancelRequest.stationType = "height_weight"
//
//                println(cancelRequest.toString())
//                viewModel.setLogin(participant, cancelRequest)
//
////                BodyMeasurementDataRxBus.getInstance()
////                    .post(BodyMeasurementDataResponse(BodyMeasurementDataEventType.HEIGHT, bodyMeasurementData))
//                dismiss()
            }


        }
//        viewModel.cancelId?.observe(this, Observer { householdResource ->
//            if (householdResource?.status == Status.SUCCESS) {
//                dismiss()
//            }
//        })

//        viewModel.cancelId?.observe(this, Observer { cancelObserver ->
//            if (cancelObserver?.status == Status.SUCCESS) {
//                dismiss()
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to true)
//                completedDialogFragment.show(fragmentManager!!)
//
//            } else if (cancelObserver?.status == Status.ERROR) {
//                binding.textViewError.text = cancelObserver.message?.message.toString()
//            }
//        })

        binding.buttonCancel.singleClick {
            binding.root.hideKeyboard()
            dismiss()
        }

        viewModel.cancelIdWithMeta?.observe(this, Observer { cancelObserver ->
            if (cancelObserver?.status == Status.SUCCESS) {
                dismiss()
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to true)
                completedDialogFragment.show(fragmentManager!!)

            } else if (cancelObserver?.status == Status.ERROR) {
                binding.textViewError.text = cancelObserver.message?.message.toString()
            }
        })
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // creating the fullscreen dialog
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        return dialog
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(HeightWeightQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications = mutableListOf<Map<String, String>>()

        val haveUnaided = questionnaireViewModel.haveUnaided.value

        val unaidedMap = mutableMapOf<String, String>()
        unaidedMap["question"] = "Can stand unaided for 1 minute?"
        unaidedMap["answer"] = if (haveUnaided!!) "yes" else "no"

        contraindications.add(unaidedMap)

        return contraindications
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

}
