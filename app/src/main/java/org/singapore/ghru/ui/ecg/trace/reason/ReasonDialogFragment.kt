package org.singapore.ghru.ui.ecg.trace.reason

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
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.EcgReasonDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.ecg.trace.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.shoKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.CancelRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ReasonDialogFragment : DialogFragment(), Injectable {

    val TAG = ReasonDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<EcgReasonDialogFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ReasonDialogViewModel


    lateinit var cancelRequest: CancelRequest

    private var participant: ParticipantRequest? = null

    private var existingComment: String? = null

    @Inject
    lateinit var jobManager: JobManager

    private var contraindications: List<Map<String, String>>? = null
    private var fromSkipped: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            existingComment = arguments?.getString("comment")
            fromSkipped = arguments?.getBoolean("skipped")!!

//            if (arguments?.getSerializable("contraindications") != null) {
//                contraindications = arguments?.getSerializable("contraindications") as List<Map<String, String>>
//            }

        } catch (e: KotlinNullPointerException) {

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<EcgReasonDialogFragmentBinding>(
            inflater,
            R.layout.ecg_reason_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cancelRequest = CancelRequest(stationType = "ecg")

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, _ ->
            // println("i $i" + radioGroup.checkedRadioButtonId)
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonUnableToObtaionReading -> cancelRequest.reason =
                    getString(R.string.ecg_unable_to_obtain_reading)
                R.id.radioButtonPaticipantRefused -> cancelRequest.reason =
                    getString(R.string.ecg_participant_refused)
                R.id.radioButtonMachineMalfucntion -> cancelRequest.reason =
                    getString(R.string.ecg_machine_malfunctioned)
                R.id.radioButtonContraindication -> cancelRequest.reason =
                    getString(R.string.contraindication_present)
                else -> {
                    // binding.textViewError.text = getString(R.string.app_error_please_select_one)
                    binding.textViewError.visibility = View.GONE
                }
            }
            if (radioGroup.checkedRadioButtonId == R.id.radioButtonOther) {
                binding.textInputEditTextOther.visibility = View.VISIBLE
                binding.textInputEditTextOther.shoKeyboard()
            } else {
                binding.textInputEditTextOther.visibility = View.GONE
            }
            binding.executePendingBindings()
        }

//        if (fromSkipped && contraindications != null) {
//            binding.radioGroup.check(R.id.radioButtonContraindication)
//        }

        binding.buttonAcceptAndContinue.singleClick {
            binding.root.hideKeyboard()
            if (binding.radioGroup.checkedRadioButtonId == -1) {
                binding.textViewError.text = getString(R.string.app_error_please_select_one)
                binding.textViewError.visibility = View.VISIBLE

            } else  {

                binding.textViewError.text = ""
                binding.textViewError.visibility = View.GONE
                if(binding.radioButtonMachineMalfucntion.isChecked)
                    cancelRequest.reason = binding.radioButtonMachineMalfucntion.text.toString()
                else if(binding.radioButtonPaticipantRefused.isChecked)
                    cancelRequest.reason = binding.radioButtonPaticipantRefused.text.toString()
                else if(binding.radioButtonContraindication.isChecked)
                    cancelRequest.reason = binding.radioButtonContraindication.text.toString()
                else
                    cancelRequest.reason = binding.radioButtonPaticipantRefused.text.toString()

                var comment = binding.comment.text.toString()
                if(existingComment != null) {
                    comment = existingComment + "\n" + binding.comment.text.toString()
                }

                cancelRequest.comment = comment
                cancelRequest.syncPending = !isNetworkAvailable()
                cancelRequest.screeningId = participant?.screeningId!!

//                if (contraindications != null) {
//                    cancelRequest.contraindications = contraindications
//                }

                viewModel.setLogin(participant, cancelRequest)
            }

            //println(cancelRequest.toString())
//                if (isNetworkAvailable()) {

//                } else {
//                    jobManager.addJobInBackground(SyncCancelrequestJob(participant!!, cancelRequest))
//                    val completedDialogFragment = CompletedDialogFragment()
//                    completedDialogFragment.arguments = bundleOf("is_cancel" to true)
//                    completedDialogFragment.show(fragmentManager!!)
//                }


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
