package org.singapore.ghru.ui.hipwaist.contraindication

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
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class HipWaistQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<HipWaistContraindicationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: HipWaistQuestionnaireViewModel

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
        val dataBinding = DataBindingUtil.inflate<HipWaistContraindicationFragmentBinding>(
            inflater,
            R.layout.hip_waist_contraindication_fragment,
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
            ViewModelProviders.of(this).get(HipWaistQuestionnaireViewModel::class.java)
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
                navController().navigate(R.id.action_contraFragment_to_hipWaistHomeFragment, bundle)
            }
        }

        binding.radioGroupColostomy.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noColostomy) {
                binding.radioGroupColostomyValue = false
                viewModel.setHaveColostomy(false)

            } else {
                binding.radioGroupColostomyValue = false
                viewModel.setHaveColostomy(true)

            }
            binding.executePendingBindings()
        }

        binding.radioGroupUnaided.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noUnaided) {
                binding.radioGroupUnaidedValue = false
                viewModel.setHaveUnaided(false)

            } else {
                binding.radioGroupUnaidedValue = false
                viewModel.setHaveUnaided(true)

            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveColostomy.value == null) {
            binding.radioGroupColostomyValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveUnaided.value == null) {
            binding.radioGroupUnaidedValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveColostomy.value ==  true || viewModel.haveUnaided.value ==  false) {
            val skipDialogFragment = HipWaistSkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participantRequest,
                "contraindications" to getContraindications(),
                "type" to TYPE_HIP_WAIST)
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

        val haveColostomy = viewModel.haveColostomy.value
        val haveUnaided = viewModel.haveUnaided.value

        val unaidedMap = mutableMapOf<String, String>()
        unaidedMap["id"] = "WHCI1"
        unaidedMap["question"] = getString(R.string.hip_waist_unaided_question)
        unaidedMap["answer"] = if (haveUnaided!!) "yes" else "no"

        contraindications.add(unaidedMap)

        val colostomyMap = mutableMapOf<String, String>()
        colostomyMap["id"] = "WHCI2"
        colostomyMap["question"] = getString(R.string.hip_waist_colostomy_question)
        colostomyMap["answer"] = if (haveColostomy!!) "yes" else "no"

        contraindications.add(colostomyMap)

        return contraindications
    }

}
