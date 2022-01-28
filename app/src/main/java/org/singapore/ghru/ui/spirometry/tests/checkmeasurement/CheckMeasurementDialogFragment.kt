package org.singapore.ghru.ui.spirometry.tests.checkmeasurement

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
import org.singapore.ghru.databinding.CheckMeasurementDialogFragmentSpiroBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.spirometry.tests.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.SpirometryRequest
import org.singapore.ghru.vo.Status
import javax.inject.Inject


class CheckMeasurementDialogFragment : DialogFragment(), Injectable {

    val TAG = CheckMeasurementDialogFragment::class.java.getSimpleName()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<CheckMeasurementDialogFragmentSpiroBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: CheckMeasurementDialogViewModel
    //private var participantRequest: ParticipantRequest? = null
    private var spiroRequest: SpirometryRequest? = null
    private var prComment: String? = null
//    private var recCount: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            //participantRequest = arguments?.getParcelable<ParticipantRequest>("participant")!!
            spiroRequest = arguments?.getParcelable<SpirometryRequest>("spiroRequest")!!
            //recCount = arguments?.getInt("recordCount")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CheckMeasurementDialogFragmentSpiroBinding>(
            inflater,
            R.layout.check_measurement_dialog_fragment_spiro,
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
            //activity?.finish()
            dismiss()
        }

        if (!spiroRequest!!.comment.equals(""))
        {
            prComment = spiroRequest!!.comment
            binding.comment.setText(prComment)
        }

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            // println("i $i" + radioGroup.checkedRadioButtonId)
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButtonUnwell -> spiroRequest!!.partially_reason = getString(R.string.bp_unwell)
                R.id.radioButtonRushingOff -> spiroRequest!!.partially_reason = getString(R.string.bp_rushing_off)
                R.id.radioButtonRefused -> spiroRequest!!.partially_reason = getString(R.string.ecg_participant_refused)
                R.id.radioButtonTechnicalIssue -> spiroRequest!!.partially_reason = getString(R.string.intake_cancel_technical_issue)
                else -> {
                    binding.textViewError.text = getString(R.string.app_error_please_select_one)
                    binding.textViewError.visibility = View.GONE
                }
            }
            binding.executePendingBindings()
        }


        //binding.messageTextView.text = "You have " + recCount + " out of 3 measurements. Please comment the reason for partial measurement to continue."

        binding.buttonAcceptAndContinue.singleClick {

            binding.root.hideKeyboard()
            if (binding.radioGroup.checkedRadioButtonId == -1)
            {
                binding.textViewError.text = getString(R.string.app_error_please_select_one)
                binding.textViewError.visibility = View.VISIBLE
            }
            else
            {
                spiroRequest!!.comment = binding.comment.text.toString()
                viewModel.setSpirometryRequest(spiroRequest!!)
            }

//            if (binding.comment.text.toString().equals("") || binding.comment.text == null)
//            {
//                Toast.makeText(activity!!, "Please add a comment", Toast.LENGTH_LONG).show()
//            }
//            else
//            {
//                bpRequest!!.body.comment = binding.comment.text.toString()
//                viewModel.setBloodPressureMetaRequestRemote(bpRequest!!, participantRequest!!)
            //}
        }

        viewModel.spirometryRequest?.observe(this, Observer { commonResponce ->


            if (commonResponce?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(commonResponce?.status == Status.ERROR){
//                Crashlytics.setString("bodyMeasurementMeta", bodyMeasurementMeta.toString())
//                Crashlytics.setString("recordList", recordList.toString())
//                Crashlytics.logException(Exception("sample collection " + commonResponce.message.toString()))
//                Timber.d(commonResponce.message?.message)
            }
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

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

}
