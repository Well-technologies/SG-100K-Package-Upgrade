package org.singapore.ghru.ui.visualacuity.contraindication

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.HeightWeightContraindicationFragmentBinding
import org.singapore.ghru.databinding.HipWaistContraindicationFragmentBinding
import org.singapore.ghru.databinding.VisualAcuityContraFragmentBinding
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class VisualAcuityQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<VisualAcuityContraFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: VisualAcuityQuestionnaireViewModel

    private var participantRequest: ParticipantRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<VisualAcuityContraFragmentBinding>(
            inflater,
            R.layout.visual_acuity_contra_fragment,
            container,
            false
        )
        binding = dataBinding

        //setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(VisualAcuityQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.participant = participantRequest

        binding.setLifecycleOwner(this)

        binding.nextButton.singleClick {
            if (validateNextButton()) {
                val bundle = Bundle()
                bundle.putParcelable("ParticipantRequest", participantRequest)
                //navController().navigate(R.id.action_contraFragment_to_VisualAidFragment, bundle)
            }
        }

        binding.radioGroupEyeOccluded.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noEyeOccluded) {
                binding.radioGroupEyeOccludedValue = false
                viewModel.setEyeOccluded(false)

            } else {
                binding.radioGroupEyeOccludedValue = false
                viewModel.setEyeOccluded(true)

            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveEyeOccluded.value == null) {
            binding.radioGroupEyeOccludedValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveEyeOccluded.value ==  false) {
            val skipDialogFragment = VisualAcuitySkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participantRequest,
                "contraindications" to getContraindications(),
                "type" to TYPE_VISUAL_ACUITY)
            skipDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.hideKeyboard()
                navController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications = mutableListOf<Map<String, String>>()

        val haveEyeOccluded = viewModel.haveEyeOccluded.value

        val eyeMap = mutableMapOf<String, String>()
        eyeMap["id"] = "VACI1"
        eyeMap["question"] = getString(R.string.visual_acuity_question)
        eyeMap["answer"] = if (haveEyeOccluded!!) "yes" else "no"

        contraindications.add(eyeMap)

        return contraindications
    }

}
