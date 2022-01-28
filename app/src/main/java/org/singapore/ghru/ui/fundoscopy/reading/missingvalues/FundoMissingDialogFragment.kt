package org.singapore.ghru.ui.fundoscopy.reading.missingvalues

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.FundoMissingDialogFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject
import androidx.lifecycle.Observer
import org.singapore.ghru.util.singleClick

class FundoMissingDialogFragment : DialogFragment(), Injectable {

    val TAG = FundoMissingDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<FundoMissingDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var missingDialogViewModel: MissingDialogViewModel

//    var isCancel: Boolean = false
//    var participant: ParticipantRequest? = null
//    var comment: String? = null
//    var isNetwork: Boolean? = null
//    var contraindications : List<Map<String, String>>? = null
//    var questions : List<Map<String, String>>? = null
//    var selectedDeviceID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
//            isCancel = arguments?.getBoolean("is_cancel")!!
//            participant = arguments?.getParcelable<ParticipantRequest>("participant")
//            comment = arguments?.getString("comment")!!
//            selectedDeviceID = arguments?.getString("selectedDeviceID")!!
//            isNetwork = arguments?.getBoolean("isNetwork")!!
//            if (arguments?.getSerializable("contraindications") != null) {
//                contraindications = arguments?.getSerializable("contraindications") as List<Map<String, String>>
//            }
//            if (arguments?.getSerializable("questions") != null) {
//                contraindications = arguments?.getSerializable("questions") as List<Map<String, String>>
//            }
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FundoMissingDialogFragmentBinding>(
            inflater,
            R.layout.fundo_missing_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //binding.isCancel = isCancel!!

//        if(isCancel){
//            binding.textView.text = getString(R.string.station_canceled)
//        }else{
//            binding.textView.text = getString(R.string.station_completed)
//        }
        binding.continueButton.singleClick {
//            missingDialogViewModel.setParticipantComplete(
//                participant!!,
//                comment,
//                selectedDeviceID!!,
//                isNetworkAvailable(),
//                contraindications!!,
//                questions!!
//            )
            dismiss()
        }

//        missingDialogViewModel.fundoscopyComplete?.observe(this, Observer { participant ->
//
//            if (participant?.status == Status.SUCCESS) {
//                dismiss()
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                completedDialogFragment.show(fragmentManager!!)
//            } else if (participant?.status == Status.ERROR) {
//                Crashlytics.setString("comment", comment)
//                Crashlytics.setString("participant", participant.toString())
//                Crashlytics.logException(Exception("fundoscopyComplete " + participant.message.toString()))
//                binding.executePendingBindings()
//            }
//        })

        binding.cancelButton.singleClick {
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().navigateUp()
            }
        }
        return super.onOptionsItemSelected(item)
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
