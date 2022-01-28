package org.singapore.ghru.ui.ecg.trace.complete

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
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
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.EcgCompleteDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.ecg.questions.ECGQuestionnaireViewModel
import org.singapore.ghru.ui.ecg.trace.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CompleteDialogFragment : DialogFragment(), Injectable {

    val TAG = CompleteDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<EcgCompleteDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var confirmationdialogViewModel: CompleteDialogViewModel

    @Inject
    lateinit var jobManager: JobManager
    private var participant: ParticipantRequest? = null
    private var comment: String? = null
    private var device_id: String? = null

    private lateinit var questionnaireViewModel: ECGQuestionnaireViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            comment = arguments?.getString("comment")
            device_id = arguments?.getString("deviceId")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<EcgCompleteDialogFragmentBinding>(
            inflater,
            R.layout.ecg_complete_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
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

//        confirmationdialogViewModel.eCGSaveRemote?.observe(this, Observer { participant ->
//
//            if (participant?.status == Status.SUCCESS) {
//                dismiss()
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                completedDialogFragment.show(fragmentManager!!)
//            } else if (participant?.status == Status.ERROR) {
//
//                Crashlytics.setString("comment", comment.toString())
//                Crashlytics.setString("participant", participant.toString())
//                Crashlytics.logException(Exception("eCGSaveRemote " + participant.message.toString()))
//                binding.progressBar.visibility = View.GONE
//                binding.textViewError.setText(participant.message?.message)
//                binding.textViewError.visibility = View.VISIBLE
//                binding.executePendingBindings()
//            }
//        })
        binding.buttonAcceptAndContinue.singleClick {
            // if(binding,)
            val status = if (binding.radioGroup.checkedRadioButtonId == R.id.normal) {
                getString(R.string.ecg_check_normal)
            } else {
                getString(R.string.ecg_check_abnormal)
            }

            val endTime: String = convertTimeTo24Hours()
            val endDate: String = getDate()
            val endDateTime:String = endDate + " " + endTime

            participant?.meta?.endTime = endDateTime
//            if (isNetworkAvailable()) {
            //confirmationdialogViewModel.setECGRemote(participant!!, status, comment, device_id!!,isNetworkAvailable(), getContraindications())
//            } else {
//                val mECGStatus = ECGStatus(status, comment, device_id, meta= participant?.meta)
//                jobManager.addJobInBackground(SyncECGJob(participant, mECGStatus))
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                completedDialogFragment.show(fragmentManager!!)
//            }
        }

        binding.buttonCancel.singleClick {
            dismiss()
        }
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

        return dialog
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
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

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val haveArteriovenous = questionnaireViewModel.haveArteriovenous.value
        val hadSurgery = questionnaireViewModel.hadSurgery.value
        val lymphRemoved = questionnaireViewModel.lymphRemoved.value
        val haveTrauma = questionnaireViewModel.haveTrauma.value
        val haveNeckInjury = questionnaireViewModel.haveNeckInjury.value
        val amputated = questionnaireViewModel.amputated.value

        var arteriovenousMap = mutableMapOf<String, String>()
        arteriovenousMap["question"] = getString(R.string.ecg_arteriovenous_question)
        arteriovenousMap["answer"] = if (haveArteriovenous!!) "yes" else "no"

        contraindications.add(arteriovenousMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["question"] = getString(R.string.ecg_breast_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var lymphRemovedMap = mutableMapOf<String, String>()
        lymphRemovedMap["question"] = getString(R.string.ecg_lymph_question)
        lymphRemovedMap["answer"] = if (lymphRemoved!!) "yes" else "no"

        contraindications.add(lymphRemovedMap)

        var traumaMap = mutableMapOf<String, String>()
        traumaMap["question"] = getString(R.string.ecg_trauma_question)
        traumaMap["answer"] = haveTrauma!!

        contraindications.add(traumaMap)

        var neckInjuryMap = mutableMapOf<String, String>()
        neckInjuryMap["question"] = getString(R.string.ecg_neck_injury_question)
        neckInjuryMap["answer"] = if (haveNeckInjury!!) "yes" else "no"

        contraindications.add(neckInjuryMap)

        var amputatedMap = mutableMapOf<String, String>()
        amputatedMap["question"] = getString(R.string.ecg_amputated_question)
        amputatedMap["answer"] = if (amputated!!) "yes" else "no"

        contraindications.add(amputatedMap)

        return contraindications
    }

}
