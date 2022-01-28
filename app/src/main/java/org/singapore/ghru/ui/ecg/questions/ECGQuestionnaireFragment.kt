package org.singapore.ghru.ui.ecg.questions

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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.EcgQuestionnaireFragmentBinding
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class ECGQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<EcgQuestionnaireFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var viewModel: ECGQuestionnaireViewModel

    private var participant: ParticipantRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<EcgQuestionnaireFragmentBinding>(
            inflater,
            R.layout.ecg_questionnaire_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.root.hideKeyboard()
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(ECGQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.participant = participant

        binding.nextButton.singleClick {

            if (validateNextButton()) {
                navController().navigate(
                    R.id.action_questionnaireFragment_to_guideMainFragment,
                    bundleOf("participant" to participant)
                )
            }
        }

        binding.radioGroupArteriovenous.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noArteriovenous) {
                binding.radioGroupArteriovenousValue = false
                viewModel.setHaveArteriovenous(false)

            } else {
                binding.radioGroupArteriovenousValue = false
                viewModel.setHaveArteriovenous(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupSurgery.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noSurgery) {
                binding.radioGroupSurgeryValue = false
                viewModel.setHadSurgery("no")
            } else if (radioGroup.checkedRadioButtonId == R.id.leftSurgery) {
                binding.radioGroupSurgeryValue = false
                viewModel.setHadSurgery("left")
            } else if (radioGroup.checkedRadioButtonId == R.id.rightSurgery) {
                binding.radioGroupSurgeryValue = false
                viewModel.setHadSurgery("right")
            } else {
                binding.radioGroupSurgeryValue = false
                viewModel.setHadSurgery("both")
            }
            binding.executePendingBindings()
        }
        binding.radioGroupLymph.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noLymph) {
                binding.radioGroupLymphValue = false
                viewModel.setLymphRemoved(false)

            } else {
                binding.radioGroupLymphValue = false
                viewModel.setLymphRemoved(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupTrauma.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noTrauma) {
                binding.radioGroupTraumaValue = false
                viewModel.setHaveTrauma("no")
            } else if (radioGroup.checkedRadioButtonId == R.id.leftTrauma) {
                binding.radioGroupTraumaValue = false
                viewModel.setHaveTrauma("left")
            } else if (radioGroup.checkedRadioButtonId == R.id.rightTrauma) {
                binding.radioGroupTraumaValue = false
                viewModel.setHaveTrauma("right")
            } else {
                binding.radioGroupTraumaValue = false
                viewModel.setHaveTrauma("both")
            }
            binding.executePendingBindings()
        }
        binding.radioGroupNeck.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noNeck) {
                binding.radioGroupNeckInjuryValue = false
                viewModel.setHaveNeckInjury(false)

            } else {
                binding.radioGroupNeckInjuryValue = false
                viewModel.setHaveNeckInjury(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupAmputate.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noAmputate) {
                binding.radioGroupAmputatedValue = false
                viewModel.setAmputated(false)

            } else {
                binding.radioGroupAmputatedValue = false
                viewModel.setAmputated(true)

            }
            binding.executePendingBindings()
        }

    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveArteriovenous.value == null) {
            binding.radioGroupArteriovenousValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.hadSurgery.value == null) {
            binding.radioGroupSurgeryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.lymphRemoved.value == null) {
            binding.radioGroupLymphValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveTrauma.value == null) {
            binding.radioGroupTraumaValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveNeckInjury.value == null) {
            binding.radioGroupNeckInjuryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.amputated.value == null) {
            binding.radioGroupAmputatedValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveArteriovenous.value ==  true || viewModel.lymphRemoved.value ==  true ||
            viewModel.haveNeckInjury.value ==  true || viewModel.amputated.value ==  true ||
            viewModel.hadSurgery.value == "both" || viewModel.haveTrauma.value == "both") {
            val skipDialogFragment = ECGSkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participant,
                "contraindications" to getContraindications(),
                "type" to TYPE_ECG)
            skipDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val haveArteriovenous = viewModel.haveArteriovenous.value
        val hadSurgery = viewModel.hadSurgery.value
        val lymphRemoved = viewModel.lymphRemoved.value
        val haveTrauma = viewModel.haveTrauma.value
        val haveNeckInjury = viewModel.haveNeckInjury.value
        val amputated = viewModel.amputated.value

        var arteriovenousMap = mutableMapOf<String, String>()
        arteriovenousMap["question"] = getString(R.string.ecg_arteriovenous_question)
        arteriovenousMap["answer"] = if (haveArteriovenous!!) "yes" else "no"

        contraindications.add(arteriovenousMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["question"] = getString(R.string.ecg_breast_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var lymphRemovedMap = mutableMapOf<String, String>()
        lymphRemovedMap["question"] = getString(R.string.ecg_lymph_question)
        lymphRemovedMap["answer"] = if (lymphRemoved!!) "yes" else "no"

        contraindications.add(lymphRemovedMap)

        var traumaMap = mutableMapOf<String, String>()
        traumaMap["question"] = getString(R.string.ecg_trauma_question)
        traumaMap["answer"] = haveTrauma!!

        contraindications.add(traumaMap)

        var neckInjuryMap = mutableMapOf<String, String>()
        neckInjuryMap["question"] = getString(R.string.ecg_neck_injury_question)
        neckInjuryMap["answer"] = if (haveNeckInjury!!) "yes" else "no"

        contraindications.add(neckInjuryMap)

        var amputatedMap = mutableMapOf<String, String>()
        amputatedMap["question"] = getString(R.string.ecg_amputated_question)
        amputatedMap["answer"] = if (amputated!!) "yes" else "no"

        contraindications.add(amputatedMap)

        return contraindications
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
