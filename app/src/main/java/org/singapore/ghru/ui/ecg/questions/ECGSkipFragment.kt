package org.singapore.ghru.ui.ecg.questions

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.ViewModelProviders
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

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ECGSkipFragmentBinding
import org.singapore.ghru.ui.ecg.trace.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class ECGSkipFragment : DialogFragment() {

    val TAG = ECGSkipFragment::class.java.getSimpleName()

    var binding by autoCleared<ECGSkipFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ECGSkipViewModel

    private var participant: ParticipantRequest? = null
    private var contraindications: List<Map<String, String>>? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            contraindications = arguments?.getSerializable("contraindications") as List<Map<String, String>>
            type = arguments?.getString("type")

        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ECGSkipFragmentBinding>(
            inflater,
            R.layout.e_c_g_skip_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ECGSkipViewModel::class.java)

        binding.buttonSkipStation.singleClick {
            if (type == TYPE_BP) {
                val reasonDialogFragment = org.singapore.ghru.ui.bodymeasurements.bp.reason.ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participant,
                    "comment" to binding.comment.text.toString(),
                    "contraindications" to contraindications,
                    "skipped" to true)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else if (type == TYPE_ECG) {
                val reasonDialogFragment = ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participant,
                    "comment" to binding.comment.text.toString(),
                    "contraindications" to contraindications,
                    "skipped" to true)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else if (type == TYPE_FUNDO) {
                val reasonDialogFragment = org.singapore.ghru.ui.fundoscopy.reading.reason.ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participant,
                    "comment" to binding.comment.text.toString(),
                    "contraindications" to contraindications,
                    "skipped" to true)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else if (type == TYPE_ULTRASOUND) {
                val reasonDialogFragment = org.singapore.ghru.ui.ultrasound.reason.ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participant,
                    "comment" to binding.comment.text.toString(),
                    "contraindications" to contraindications,
                    "skipped" to true)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else if (type == TYPE_TREADMILL) {
                val reasonDialogFragment = org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("participant" to participant,
                    "comment" to binding.comment.text.toString(),
                    "contraindications" to contraindications,
                    "skipped" to true)
                reasonDialogFragment.show(fragmentManager!!)
            }
            else if (type == TYPE_DXA) {
                val reasonDialogFragment = org.singapore.ghru.ui.dxa.reason.ReasonDialogFragment()
                reasonDialogFragment.arguments = bundleOf("ParticipantRequest" to participant,
                    "comment" to binding.comment.text.toString(),
                    "contraindications" to contraindications,
                    "skipped" to true)
                reasonDialogFragment.show(fragmentManager!!)
            }
//            else if (type == TYPE_SAMPLE) {
//                val reasonDialogFragment = org.singapore.ghru.ui.samplecollection.bagscanned.reason.ReasonDialogFragment()
//                reasonDialogFragment.arguments = bundleOf("ParticipantRequest" to participant,
//                    "comment" to binding.comment.text.toString(),
//                    "contraindications" to contraindications,
//                    "skipped" to true)
//                reasonDialogFragment.show(fragmentManager!!)
//            }
        }

        binding.buttonCancel.singleClick {
            binding.root.hideKeyboard()
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
        dialog.setCancelable(false)
        return dialog
    }

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

}
