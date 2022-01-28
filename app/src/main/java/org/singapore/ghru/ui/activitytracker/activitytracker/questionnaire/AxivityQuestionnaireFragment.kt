package org.singapore.ghru.ui.activitytracker.activitytracker.questionnaire

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
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
import org.singapore.ghru.databinding.BPManualOneFragmentBinding
import org.singapore.ghru.databinding.BPQuestionnaireFragmentBinding
import org.singapore.ghru.databinding.SpiroQuestionnaireFragmentBinding
import org.singapore.ghru.ui.ecg.trace.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class AxivityQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<SpiroQuestionnaireFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var viewModel: AxivityQuestionnaireViewModel

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
        val dataBinding = DataBindingUtil.inflate<SpiroQuestionnaireFragmentBinding>(
            inflater,
            R.layout.spiro_questionnaire_fragment,
            container,
            false
        )
        binding = dataBinding

        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(AxivityQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.participant = participantRequest

        binding.setLifecycleOwner(this)

        binding.nextButton.singleClick {
            if (validateNextButton()) {
                val bundle = Bundle()
                bundle.putParcelable("participant", participantRequest)
                navController().navigate(R.id.action_SpiroQuestionnaireFragment_to_mainFragment, bundle)
            }
        }

        binding.radioGroupRetina.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noRetina) {
                binding.radioGroupRetinaValue = false
                viewModel.setHaveRetina(false)

            } else {
                binding.radioGroupRetinaValue = false
                viewModel.setHaveRetina(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupEyeSurgery.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noEyeSurgery) {
                binding.radioGroupEyeSurgeryValue = false
                viewModel.setHaveEyeSurgery(false)

            } else {
                binding.radioGroupEyeSurgeryValue = false
                viewModel.setHaveEyeSurgery(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupOtherSurgery.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noOtherSurgery) {
                binding.radioGroupOtherSurgeryValue = false
                viewModel.setHaveOtherSurgery(false)

            } else {
                binding.radioGroupOtherSurgeryValue = false
                viewModel.setHaveOtherSurgery(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupMyocardial.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noMyocardial) {
                binding.radioGroupMyocardialValue = false
                viewModel.setHaveMycardial(false)

            } else {
                binding.radioGroupMyocardialValue = false
                viewModel.setHaveMycardial(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupStroke.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noStroke) {
                binding.radioGroupStrokeValue = false
                viewModel.setHaveStroke(false)

            } else {
                binding.radioGroupStrokeValue = false
                viewModel.setHaveStroke(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupChest.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noChest) {
                binding.radioGroupChestValue = false
                viewModel.setHaveChestInfection(false)

            } else {
                binding.radioGroupChestValue = false
                viewModel.setHaveChestInfection(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupTuberculosis.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noTuberculosis) {
                binding.radioGroupTuberculosisValue = false
                viewModel.setHaveTuberculosis(false)

            } else {
                binding.radioGroupTuberculosisValue = false
                viewModel.setHaveTuberculosis(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupPneumothorax.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noPneumothorax) {
                binding.radioGroupPneumothoraxValue = false
                viewModel.setHavePneumothorax(false)

            } else {
                binding.radioGroupPneumothoraxValue = false
                viewModel.setHavePneumothorax(true)

            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveRetina.value == null) {
            binding.radioGroupRetinaValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveEyeSurgery.value == null) {
            binding.radioGroupEyeSurgeryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveOtherSurgery.value == null) {
            binding.radioGroupOtherSurgeryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveMyocardialInfarction.value == null) {
            binding.radioGroupMyocardialValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveSufferedStroke.value == null) {
            binding.radioGroupStrokeValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveChestInfection.value == null) {
            binding.radioGroupChestValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveTuberculosis.value == null) {
            binding.radioGroupTuberculosisValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.havePneumothorax.value == null) {
            binding.radioGroupPneumothoraxValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveRetina.value ==  true || viewModel.haveEyeSurgery.value ==  true ||
            viewModel.haveOtherSurgery.value ==  true || viewModel.haveSufferedStroke.value ==  true ||
            viewModel.haveChestInfection.value ==  true || viewModel.haveMyocardialInfarction.value ==  true ||
            viewModel.haveTuberculosis.value ==  true || viewModel.havePneumothorax.value ==  true) {
            val skipDialogFragment = ReasonDialogFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participantRequest)
            skipDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
