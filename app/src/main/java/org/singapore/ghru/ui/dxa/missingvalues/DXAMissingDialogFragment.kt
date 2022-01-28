package org.singapore.ghru.ui.dxa.missingvalues

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
import org.singapore.ghru.ui.dxa.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject
import androidx.lifecycle.Observer
import com.google.gson.GsonBuilder
import org.singapore.ghru.databinding.DxaMissingDialogFragmentBinding
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.DXABody
import org.singapore.ghru.vo.request.DXABodyData

class DXAMissingDialogFragment : DialogFragment(), Injectable {

    val TAG = DXAMissingDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<DxaMissingDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var missingDialogViewModel: DXAMissingDialogViewModel


//    var participant: ParticipantRequest? = null
//    var isNetwork: Boolean? = null
//    var dxaBody: DXABodyData? = null
//    var contraindications : List<Map<String, String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
//            participant = arguments?.getParcelable<ParticipantRequest>("participant")
//            isNetwork = arguments?.getBoolean("isNetwork")!!
//            dxaBody = arguments?.getParcelable<DXABodyData>("dxaBodyData")
//
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
        val dataBinding = DataBindingUtil.inflate<DxaMissingDialogFragmentBinding>(
            inflater,
            R.layout.dxa_missing_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.continueButton.singleClick {
//            val mDXABody = DXABody(dxaBody!!)
//            mDXABody.contraindications = contraindications
//            val gson = GsonBuilder().setPrettyPrinting().create()
//
//            missingDialogViewModel.setParticipantComplete(
//                participant!!, isNetwork!!,
//                gson.toJson(mDXABody)
//            )
            dismiss()
        }

//        missingDialogViewModel.dxaComplete?.observe(this, Observer { participant ->
//
//            if (participant?.status == Status.SUCCESS) {
//                dismiss()
//                val completedDialogFragment = CompletedDialogFragment()
//                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
//                completedDialogFragment.show(fragmentManager!!)
//            } else if (participant?.status == Status.ERROR) {
//                Crashlytics.setString("participant", participant.toString())
//                Crashlytics.logException(Exception("dxaComplete " + participant.message.toString()))
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
