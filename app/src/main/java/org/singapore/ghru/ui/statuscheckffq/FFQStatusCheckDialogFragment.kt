package org.singapore.ghru.ui.statuscheckffq

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.FfqStatusCheckDialogFragmentBinding
import org.singapore.ghru.databinding.StationCheckDialogFragmentBinding
import org.singapore.ghru.databinding.StatusCheckDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.ParticipantCre
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FFQStatusCheckDialogFragment : DialogFragment(), Injectable {

    val TAG = FFQStatusCheckDialogFragment::class.java.getSimpleName()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<FfqStatusCheckDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: FFQStatusCheckDialogViewModel

    private var participant: ParticipantRequest? = null

    private var participantData: ParticipantCre? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FfqStatusCheckDialogFragmentBinding>(
            inflater,
            R.layout.ffq_status_check_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        // viewModel.codecheckMsg.postValue(codecheckMsg)
        binding.viewModel = viewModel
        binding.saveAndExitButton.singleClick {
            //  ScanRefreshRxBus.getInstance().post(true)
            activity?.finish()
            dismiss()
        }

        binding.buttonAcceptAndContinue.singleClick {
            //StationCheckRxBus.getInstance().post("continue")
            val bundle = bundleOf("ParticipantRequest" to participant)
            findNavController().navigate(R.id.action_global_CognitionContraFragment, bundle)
            dismiss()
        }

        if (participant != null)
        {
            binding.screenId.setText(participant!!.screeningId)
            //viewModel.setScreeningId(participant!!.screeningId)
            viewModel.setLanguageId(participant!!.screeningId, "NA")
        }

//        viewModel.getParticipantCredintials.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantData = participantResource.data!!.data!!
//
//                if (participantData != null)
//                {
//                    binding.ffqLoginId.setText(participantData!!.username)
//                    binding.ffqPassword.setText(participantData!!.password)
//                    viewModel.setHaveCredintials(true)
//                }
//            } else if (participantResource?.status == Status.ERROR) {
////                val errorDialogFragment = ErrorDialogFragment()
////                errorDialogFragment.setErrorMessage(participantResource.message!!.data!!.message!!)
////                errorDialogFragment.show(fragmentManager!!)
//                //Crashlytics.logException(Exception(participantResource.toString()))
//            }
//            binding.executePendingBindings()
//        })

        viewModel.getLanguage!!.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                participantData = participantResource.data!!.data!!

                if (participantData != null)
                {
                    binding.ffqLoginId.setText(participantData!!.username)
                    binding.ffqPassword.setText(participantData!!.password)
                    viewModel.setHaveCredintials(true)
                }
            } else if (participantResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message!!.data!!.message!!)
                errorDialogFragment.show(fragmentManager!!)
                //Crashlytics.logException(Exception(participantResource.toString()))
            }
            binding.executePendingBindings()
        })

        binding.usernameCopyButton.singleClick {

            copyId()
        }

        binding.passwordCopyButton.singleClick {

            copyPassword()
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

    private fun copyId(){

        if (!binding.ffqLoginId.text.toString().equals(""))
        {
            var clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("label", binding.ffqLoginId.text.toString())
            clipboard.primaryClip = clip
            Toast.makeText(activity!!, "Login id copied to clip board", Toast.LENGTH_LONG).show()
        }
    }

    private fun copyPassword(){

        if (!binding.ffqPassword.text.toString().equals(""))
        {
            var clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("label", binding.ffqPassword.text.toString())
            clipboard.primaryClip = clip

            Toast.makeText(activity!!, "Password copied to clip board", Toast.LENGTH_LONG).show()
        }
    }

}
