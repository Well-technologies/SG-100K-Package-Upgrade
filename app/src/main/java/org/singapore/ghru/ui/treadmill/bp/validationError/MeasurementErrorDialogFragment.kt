package org.singapore.ghru.ui.treadmill.bp.validationError

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import org.singapore.ghru.R
import org.singapore.ghru.RegisterPatientActivity
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.*
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class MeasurementErrorDialogFragment : DialogFragment(), Injectable {

    val TAG = MeasurementErrorDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<ErrorMeasurementDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var confirmationdialogViewModel: MeasurementErrorDialogViewModel
    var isCancel: Boolean = false

    private var participantRequest: ParticipantRequest? = null
    var message: String? = null
    private var contraindications: List<Map<String, String>>? = null
    private var fromSkipped: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
//            message = arguments?.getString("Message")!!
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
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
        val dataBinding = DataBindingUtil.inflate<ErrorMeasurementDialogFragmentBinding>(
            inflater,
            R.layout.error_measurement_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.isCancel = isCancel!!

        binding.homeButton.singleClick {
            //activity?.finish()
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participantRequest,
                "contraindications" to contraindications,
                "skipped" to false)
            reasonDialogFragment.show(fragmentManager!!)
            dismiss()
        }

        binding.backButton.singleClick {
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
