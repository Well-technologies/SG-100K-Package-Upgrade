package org.singapore.ghru.ui.treadmill.ecgcheck

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
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
import org.singapore.ghru.databinding.EcgCheckDialogFragmentBinding
import org.singapore.ghru.databinding.StationCheckDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class EcgCheckDialogFragment : DialogFragment(), Injectable {

    val TAG = EcgCheckDialogFragment::class.java.getSimpleName()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<EcgCheckDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: EcgCheckDialogViewModel
    private var participantRequest: ParticipantRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<EcgCheckDialogFragmentBinding>(
            inflater,
            R.layout.ecg_check_dialog_fragment,
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

            viewModel.setScreeningIdEcgTrace(participantRequest!!.screeningId)
            //dismiss()
        }

        viewModel.getTraceStatus.observe(this, Observer { participantResource ->

//            if (participantResource?.status == Status.SUCCESS)
//            {
//                if (participantResource.data?.data?.isCancelled == 1)
//                {
//                    ecgErrorDialog1()
//                }
//                else if(!participantResource.data?.data?.statusCode!!.equals("100"))
//                {
//                    ecgErrorDialog1()
//                }
//                else if (participantResource.data?.data!!.trace_status != null)
//                {
//                    if (participantResource.data.data.trace_status!!.equals("Normal"))
//                    {
//                        //viewModel.setScreeningId(binding.editTextCode.text.toString())
//                        val bundle = bundleOf("participant" to participantRequest)
//                        findNavController().navigate(R.id.action_global_contraFragment, bundle)
//                        dismiss()
//                    }
//                    else
//                    {
//                        ecgErrorDialog()
//                    }
//                }
//                else
//                {
//                    //viewModel.setScreeningId(binding.editTextCode.text.toString())
//                    Toast.makeText(activity, "Trace status null", Toast.LENGTH_LONG).show()
//                    val bundle = bundleOf("participant" to participantRequest)
//                    findNavController().navigate(R.id.action_global_contraFragment, bundle)
//                    dismiss()
//                }
//
//            }
//            else if (participantResource?.status == Status.ERROR)
//            {
//                if (participantResource.message!!.data!!.message.equals("ECG is not started before."))
//                {
//                    ecgErrorDialog1()
//                }
////                val errorDialogFragment = ErrorDialogFragment()
////                errorDialogFragment.setErrorMessage(participantResource.data?.data?.message!!)
////                errorDialogFragment.show(fragmentManager!!)
//            }
            if (participantResource?.status == Status.SUCCESS)
            {
                if (participantResource.data?.data?.isCancelled == 1)
                {
                    ecgErrorDialog1(getString(R.string.ecg_error_complete1))
                }
                else if (participantResource.data?.data!!.trace_status != null)
                {
                    if (participantResource.data.data.trace_status!!.equals("Normal"))
                    {
                        //viewModel.setScreeningId(binding.editTextCode.text.toString())
//                        val bundle = bundleOf("participant" to participantRequest)
//                        findNavController().navigate(R.id.action_global_contraFragment, bundle)
//                        dismiss()
                        StationCheckRxBus.getInstance().post("continue")
                        dialog.dismiss()
                        dismiss()
                    }
                    else if (participantResource.data.data.trace_status!!.equals("Abnormal"))
                    {
                        ecgErrorDialog("Abnormal ECG", getString(R.string.ecg_error_complete), fragmentManager!!)
                        dismiss()
                    }
                }
                else {
                    ecgErrorDialog("Trace status Error", getString(R.string.ecg_trace_error), fragmentManager!!)
                    dismiss()
                }
            }
            else if (participantResource?.status == Status.ERROR)
            {
                if (participantResource.message!!.data!!.message.equals("ECG is not started before."))
                {
                    ecgErrorDialog1(getString(R.string.ecg_error_not_start))
                }
            }
            binding.executePendingBindings()
        })
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

//    private fun ecgErrorDialog()
//    {
//        lateinit var dialog: AlertDialog
//        val builder = AlertDialog.Builder(activity!!)
//
//        builder.setTitle("ECG Error")
//        builder.setMessage(getString(R.string.ecg_error_complete))
//        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))
//
//        // On click listener for dialog buttons
//        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
//            when(which){
//                DialogInterface.BUTTON_POSITIVE ->
//                {
//                    //viewModel.setScreeningId(binding.editTextCode.text.toString())
//                    val bundle = bundleOf("participant" to participantRequest)
//                    findNavController().navigate(R.id.action_global_contraFragment, bundle)
//                    dialog.dismiss()
//                    dismiss()
//                }
//                DialogInterface.BUTTON_NEGATIVE ->
//                {
//                    dialog.dismiss()
//                    dismiss()
//                    activity!!.finish()
//                }
//            }
//        }
//
//        builder.setPositiveButton(getString(R.string.app_yes),dialogClickListener)
//        builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)
//
//        dialog = builder.create()
//
//        dialog.show()
//    }

    private fun ecgErrorDialog(title: String, message : String, fragmentManager: FragmentManager)
    {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(activity!!)

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->
                {
                    //viewModel.setScreeningId(binding.editTextCode.text.toString())
//                    val bundle = bundleOf("participant" to participantRequest)
//                    findNavController().navigate(R.id.action_global_contraFragment, bundle)
//                    dialog.dismiss()

//                    val bundle = bundleOf("participant" to participantRequest)
//                    findNavController().navigate(R.id.action_global_contraFragment, bundle)
                    StationCheckRxBus.getInstance().post("continue")
                    dialog.dismiss()
                    dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE ->
                {
                    val reasonDialogFragment = ReasonDialogFragment()
                    reasonDialogFragment.arguments = bundleOf(
                        "participant" to participantRequest,
                        "skipped" to false)
                    reasonDialogFragment.show(fragmentManager)
                    dialog.dismiss()
                    //activity!!.finish()
                }
            }
        }

        builder.setPositiveButton(getString(R.string.app_yes),dialogClickListener)
        builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)

        builder.setCancelable(false)

        dialog = builder.create()

        dialog.show()
    }

//    private fun ecgErrorDialog1()
//    {
//        lateinit var dialog: AlertDialog
//        val builder = AlertDialog.Builder(activity!!)
//
//        builder.setTitle("ECG not complete")
//        builder.setMessage(getString(R.string.ecg_error_complete1))
//        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))
//
//        // On click listener for dialog buttons
//        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
//            when(which){
//                DialogInterface.BUTTON_POSITIVE ->
//                {
//                    activity!!.finish()
//                    dialog.dismiss()
//                }
////                DialogInterface.BUTTON_NEGATIVE ->
////                {
////                    dialog.dismiss()
////                }
//            }
//        }
//
//        builder.setPositiveButton(getString(R.string.app_button_ok),dialogClickListener)
//        //builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)
//
//        dialog = builder.create()
//
//        dialog.show()
//    }

    private fun ecgErrorDialog1(message:String)
    {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(activity!!)

        builder.setTitle("ECG Error")
        builder.setMessage(message)
        builder.setIcon(AppCompatResources.getDrawable(activity!!, R.drawable.ic_circular_cross))

        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE ->
                {
                    activity!!.finish()
                    dialog.dismiss()
                }
//                DialogInterface.BUTTON_NEGATIVE ->
//                {
//                    dialog.dismiss()
//                }
            }
        }

        builder.setPositiveButton(getString(R.string.app_button_ok),dialogClickListener)
        //builder.setNegativeButton(getString(R.string.app_no),dialogClickListener)
        builder.setCancelable(false)

        dialog = builder.create()

        dialog.show()
    }

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

}
