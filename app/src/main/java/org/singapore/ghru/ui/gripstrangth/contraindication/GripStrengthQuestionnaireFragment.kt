package org.singapore.ghru.ui.gripstrangth.contraindication

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
import org.singapore.ghru.databinding.GripStrengthContraindicationFragmentBinding
import org.singapore.ghru.databinding.HeightWeightContraindicationFragmentBinding
import org.singapore.ghru.databinding.HipWaistContraindicationFragmentBinding
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class GripStrengthQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<GripStrengthContraindicationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: GripStrengthQuestionnaireViewModel

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
        val dataBinding = DataBindingUtil.inflate<GripStrengthContraindicationFragmentBinding>(
            inflater,
            R.layout.grip_strength_contraindication_fragment,
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
            ViewModelProviders.of(this).get(GripStrengthQuestionnaireViewModel::class.java)
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
                navController().navigate(R.id.action_contraFragment_to_gripStrengthHomeFragment, bundle)
            }
        }

        binding.radioGroupInjury.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noInjury) {
                binding.radioGroupInjuryValue = false
                viewModel.setHaveInjury("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftInjury) {
                binding.radioGroupInjuryValue = false
                viewModel.setHaveInjury("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightInjury) {
                binding.radioGroupInjuryValue = false
                viewModel.setHaveInjury("right")

            } else {
                binding.radioGroupInjuryValue = false
                viewModel.setHaveInjury("both")

            }
            binding.executePendingBindings()
        }

        binding.radioGroupSevere.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noSevere) {
                binding.radioGroupSevereValue = false
                viewModel.setHaveSevere("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftSereve) {
                binding.radioGroupSevereValue = false
                viewModel.setHaveSevere("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightSevere) {
                binding.radioGroupSevereValue = false
                viewModel.setHaveSevere("right")

            } else {
                binding.radioGroupSevereValue = false
                viewModel.setHaveSevere("both")

            }
            binding.executePendingBindings()
        }

//        binding.radioGroupDominant.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.bothDominant) {
//                binding.radioGroupDominantValue = false
//                viewModel.setDominant("both")
//            } else if (radioGroup.checkedRadioButtonId == R.id.leftDominant) {
//                binding.radioGroupDominantValue = false
//                viewModel.setDominant("left")
//            } else if (radioGroup.checkedRadioButtonId == R.id.rightDominant) {
//                binding.radioGroupDominantValue = false
//                viewModel.setDominant("right")
//            }
//            binding.executePendingBindings()
//        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveInjury.value == null) {
            binding.radioGroupInjuryValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveSevere.value == null) {
            binding.radioGroupSevereValue = true
            binding.executePendingBindings()
            return false
        }

//        if(viewModel.haveInjury.value ==  "both" || viewModel.haveSevere.value == "both") {
        if (!validateContra()){
            val skipDialogFragment = GripStrengthSkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participantRequest,
                "contraindications" to getContraindications(),
                "type" to TYPE_GRIP_STRENGTH)
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

        val haveInjury = viewModel.haveInjury.value
        val haveSevere = viewModel.haveSevere.value

        val injuryMap = mutableMapOf<String, String>()
        injuryMap["id"] = "HGCI1"
        injuryMap["question"] = getString(R.string.grip_injury_question)
        injuryMap["answer"] = haveInjury!!

        contraindications.add(injuryMap)

        val severeMap = mutableMapOf<String, String>()
        severeMap["id"] = "HGCI2"
        severeMap["question"] = getString(R.string.grip_severe_question)
        severeMap["answer"] = haveSevere!!

        contraindications.add(severeMap)

        return contraindications
    }

    private fun validateContra(): Boolean
    {
        val injury = viewModel.haveInjury.value
        val severe = viewModel.haveSevere.value

        if (injury == "both" || severe == "both")
        {
            return false
        }
        else if (injury == "left" && severe == "right")
        {
            return false
        }
        else if (injury == "right" && severe == "left")
        {
            return false
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

}
