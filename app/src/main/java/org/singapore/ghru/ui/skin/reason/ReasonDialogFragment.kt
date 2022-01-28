package org.singapore.ghru.ui.skin.reason

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
import androidx.navigation.fragment.findNavController
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.birbit.android.jobqueue.JobManager

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.BpReasonDialogFragmentBinding
import org.singapore.ghru.databinding.DxaReasonDialogFragmentBinding
import org.singapore.ghru.databinding.VicReasonDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.shoKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.CancelRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.Status
import javax.inject.Inject


class ReasonDialogFragment : DialogFragment(), Injectable {

    val TAG = ReasonDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<VicReasonDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ReasonDialogViewModel

    lateinit var cancelRequest: CancelRequest

    private var participant: ParticipantRequest? = null

    @Inject
    lateinit var jobManager: JobManager

    private var existingComment: String? = null
    private var contraindications: List<Map<String, String>>? = null
    private var fromSkipped: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            existingComment = arguments?.getString("comment")
            fromSkipped = arguments?.getBoolean("skipped")!!
            if (arguments?.getSerializable("contraindications") != null) {
                contraindications = arguments?.getSerializable("contraindications") as List<Map<String, String>>
            }

        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<VicReasonDialogFragmentBinding>(
            inflater,
            R.layout.vic_reason_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cancelRequest = CancelRequest(stationType = "skin")

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            // println("i $i" + radioGroup.checkedRadioButtonId)
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonUnwell -> cancelRequest.reason = getString(R.string.bp_unwell)
                R.id.radioButtonRushingOff -> cancelRequest.reason = getString(R.string.bp_rushing_off)
                R.id.radioButtonParticipantRefused -> cancelRequest.reason = getString(R.string.ecg_participant_refused)
                R.id.radioButtonTechnicalIssue -> cancelRequest.reason = getString(R.string.intake_cancel_technical_issue)
                R.id.radioButtonContraindication -> cancelRequest.reason = getString(R.string.contraindication_present)
                else -> {
                    // binding.textViewError.text = getString(R.string.app_error_please_select_one)
                    binding.textViewError.visibility = View.GONE
                }
            }
            binding.executePendingBindings()
        }

        if (fromSkipped && contraindications != null) {
            binding.radioGroup.check(R.id.radioButtonContraindication)
        }

        binding.buttonAcceptAndContinue.singleClick {
            binding.root.hideKeyboard()
            if (binding.radioGroup.checkedRadioButtonId == -1) {
                binding.textViewError.text = getString(R.string.app_error_please_select_one)
                binding.textViewError.visibility = View.VISIBLE

            } else  {
                binding.textViewError.text = "";
                binding.textViewError.visibility = View.GONE

                var comment = binding.comment.text.toString()
                if(existingComment != null) {
                    comment = existingComment + "\n" + binding.comment.text.toString()
                }
                cancelRequest.comment = comment

                if(binding.radioButtonUnWell.isChecked)
                    cancelRequest.reason = binding.radioButtonUnWell.text.toString()
                else if(binding.radioButtonRushingOff.isChecked)
                    cancelRequest.reason = binding.radioButtonRushingOff.text.toString()
                else if(binding.radioButtonParticipantRefused.isChecked)
                    cancelRequest.reason = binding.radioButtonParticipantRefused.text.toString()
                else if(binding.radioButtonTechnicalIssue.isChecked)
                    cancelRequest.reason = binding.radioButtonTechnicalIssue.text.toString()
                else if(binding.radioButtonContraindication.isChecked)
                    cancelRequest.reason = binding.radioButtonContraindication.text.toString()


                cancelRequest.syncPending = !isNetworkAvailable()
                cancelRequest.screeningId = participant?.screeningId!!

                if (contraindications != null) {
                    cancelRequest.contraindications = contraindications
                }

                viewModel.setLogin(participant, cancelRequest)
            }

        }

        viewModel.cancelId?.observe(this, Observer { householdResource ->
            if (householdResource?.status == Status.SUCCESS) {
                dismiss()
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to true)
                completedDialogFragment.show(fragmentManager!!)
            }
        })
        binding.buttonCancel.singleClick {
            binding.root.hideKeyboard()
            dismiss()
        }
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

}
