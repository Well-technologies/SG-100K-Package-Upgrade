package org.singapore.ghru.ui.bodymeasurements.bp.questionnaire

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
import org.singapore.ghru.databinding.BPQuestionnaireFragmentBinding
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_BP
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class BPQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<BPQuestionnaireFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var viewModel: BPQuestionnaireViewModel

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
        val dataBinding = DataBindingUtil.inflate<BPQuestionnaireFragmentBinding>(
            inflater,
            R.layout.b_p_questionnaire_fragment,
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
            ViewModelProviders.of(this).get(BPQuestionnaireViewModel::class.java)
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
                navController().navigate(R.id.action_bPQuestionnaireFragment_to_pPManualOneFragment, bundle)
            }
        }

        binding.radioGroupArteriovenous.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noArteriovenous) {
                binding.radioGroupArteriovenousValue = false
                viewModel.setHaveArteriovenous("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftArteriovenous) {
                binding.radioGroupArteriovenousValue = false
                viewModel.setHaveArteriovenous("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightArteriovenous) {
                binding.radioGroupArteriovenousValue = false
                viewModel.setHaveArteriovenous("right")

            } else {
                binding.radioGroupArteriovenousValue = false
                viewModel.setHaveArteriovenous("both")

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
                viewModel.setLymphRemoved("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftLymph) {
                binding.radioGroupLymphValue = false
                viewModel.setLymphRemoved("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightLymph) {
                binding.radioGroupLymphValue = false
                viewModel.setLymphRemoved("right")

            } else {
                binding.radioGroupLymphValue = false
                viewModel.setLymphRemoved("both")

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

        if(!validateContra()) {
            val skipDialogFragment = ECGSkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participantRequest,
                "contraindications" to getContraindications(),
                "type" to TYPE_BP)
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

        var arteriovenousMap = mutableMapOf<String, String>()
        arteriovenousMap["id"] = "BPCI1"
        arteriovenousMap["question"] = getString(R.string.bp_arteriovenous_question)
        arteriovenousMap["answer"] = haveArteriovenous!!

        contraindications.add(arteriovenousMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = "BPCI2"
        surgeryMap["question"] = getString(R.string.bp_breast_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var lymphRemovedMap = mutableMapOf<String, String>()
        lymphRemovedMap["id"] = "BPCI3"
        lymphRemovedMap["question"] = getString(R.string.bp_lymph_question)
        lymphRemovedMap["answer"] = lymphRemoved!!

        contraindications.add(lymphRemovedMap)

        var traumaMap = mutableMapOf<String, String>()
        traumaMap["id"] = "BPCI4"
        traumaMap["question"] = getString(R.string.bp_trauma_question)
        traumaMap["answer"] = haveTrauma!!

        contraindications.add(traumaMap)

        return contraindications
    }

    private fun validateContra(): Boolean
    {
        val fistula = viewModel.haveArteriovenous.value
        val mastectomy = viewModel.hadSurgery.value
        val lymph = viewModel.lymphRemoved.value
        val trauma = viewModel.haveTrauma.value

        if (fistula == "no" && mastectomy == "no" && lymph == "no" && trauma == "no")
        {
            return true
        }
        else if (fistula == "both" || mastectomy == "both" || lymph == "both" || trauma == "both")
        {
            return false
        }
        else if (fistula == "left" && (mastectomy == "right" || lymph == "right" || trauma == "right"))
        {
            return false
        }
        else if (mastectomy == "left" && (fistula == "right" || lymph == "right" || trauma == "right"))
        {
            return false
        }
        else if (lymph == "left" && (mastectomy == "right" || fistula == "right" || trauma == "right"))
        {
            return false
        }
        else if (trauma == "left" && (mastectomy == "right" || lymph == "right" || fistula == "right"))
        {
            return false
        }
        else if (fistula == "right" && (mastectomy == "left" || lymph == "left" || trauma == "left"))
        {
            return false
        }
        else if (mastectomy == "right" && (fistula == "left" || lymph == "left" || trauma == "left"))
        {
            return false
        }
        else if (lymph == "right" && (mastectomy == "left" || fistula == "left" || trauma == "left"))
        {
            return false
        }
        else if (trauma == "right" && (mastectomy == "left" || lymph == "left" || fistula == "left"))
        {
            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
