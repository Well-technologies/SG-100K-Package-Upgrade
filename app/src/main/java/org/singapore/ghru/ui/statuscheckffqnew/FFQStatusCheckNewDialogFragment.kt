package org.singapore.ghru.ui.statuscheckffqnew

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.FfqStatusCheckNewDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FFQStatusCheckNewDialogFragment : DialogFragment(), Injectable {

    val TAG = FFQStatusCheckNewDialogFragment::class.java.getSimpleName()


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<FfqStatusCheckNewDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: FFQStatusCheckNewDialogViewModel

    private var participant: ParticipantRequest? = null
    private var station_status : Boolean? = null
    private var status_code : Int? = null
    private var type : String? = null
    private var is_Cancelled : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {

        }

        try {
            station_status = arguments?.getBoolean("StationStatus")!!
            status_code = arguments?.getInt("StatusCode")!!
            type = arguments?.getString("Type")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        try {
            is_Cancelled = arguments?.getBoolean("is_Cancelled")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FfqStatusCheckNewDialogFragmentBinding>(
            inflater,
            R.layout.ffq_status_check_new_dialog_fragment,
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

        if (type != null)
        {
            Log.d("FFQ_STATUS_CHECK" ,"DATA: TYPE" + type)
            if (type == "Cancelled" || type == "Completed")
            {
                binding.message.text = getString(R.string.app_repeat_station)
            }
            else if (type == "Stage1")
            {
                binding.message.text = getString(R.string.status_repeat_station)
            }
        }

        binding.buttonAcceptAndContinue.singleClick {
            //StationCheckRxBus.getInstance().post("continue")
            Log.d("FFQ_STATUS_CHECK" ,"DATA: STATION_STATUS" + station_status + ", STATUS_CODE " + status_code)
//            val bundle = bundleOf("ParticipantRequest" to participant,
//                "StationStatus" to station_status,
//                "StatusCode" to status_code,
//                "is_Cancelled" to is_Cancelled)
//            findNavController().navigate(R.id.action_global_ffq_contra_fragment, bundle)

            val bundle = bundleOf("ParticipantRequest" to participant)

            if (type == "Cancelled" || type == "Completed")
            {
                // navigate to language fragment action_global_LanguageFragment
                findNavController().navigate(R.id.action_global_LanguageFragment, bundle)

            }
            else if (type == "Stage1")
            {
                //navigate to guide fragment
                findNavController().navigate(R.id.action_global_GuideFragment, bundle)
            }
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

}
