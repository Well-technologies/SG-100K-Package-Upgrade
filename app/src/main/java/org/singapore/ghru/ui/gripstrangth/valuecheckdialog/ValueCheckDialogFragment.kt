package org.singapore.ghru.ui.gripstrangth.valuecheckdialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ValueCheckDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ValueCheckDialogFragment : DialogFragment(), Injectable {

    val TAG = ValueCheckDialogFragment::class.java.getSimpleName()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<ValueCheckDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ValueCheckDialogViewModel
    private var gripStrengthRequest: GripStrengthRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            gripStrengthRequest = arguments?.getParcelable<GripStrengthRequest>("GripStrengthRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ValueCheckDialogFragmentBinding>(
            inflater,
            R.layout.value_check_dialog_fragment,
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

        binding.buttonAcceptAndContinue.singleClick {

            if (gripStrengthRequest != null)
            {
                viewModel.setGripMeasurementMeta(gripStrengthRequest!!)
                //dismiss()
            }
        }

        viewModel.gripMeasurementMetaOffline?.observe(this, Observer { sampleMangementPocess ->

            if (sampleMangementPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
                Log.d("ok","ok")
            } else if(sampleMangementPocess?.status == Status.ERROR){
//                Crashlytics.setString(
//                    "HeightWeightMeasurementMeta",
//                    GripStrengthRequest(meta = meta, body = gripStrength).toString()
//                )
//                Crashlytics.setString("participant", participantRequest.toString())
//                Crashlytics.logException(Exception("BodyMeasurementMeta " + sampleMangementPocess.message.toString()))
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
