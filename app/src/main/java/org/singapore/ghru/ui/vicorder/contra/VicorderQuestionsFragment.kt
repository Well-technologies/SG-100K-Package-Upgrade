package org.singapore.ghru.ui.vicorder.contra

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
import org.singapore.ghru.databinding.VicorderContraFragmentBinding
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_FUNDO
import org.singapore.ghru.ui.vicorder.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.request.BodyMeasurementMetaNew
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class VicorderQuestionsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<VicorderContraFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: VicorderQuestionsViewModel

    private var participant: ParticipantRequest? = null
    var bodyMeasurementMeta: BodyMeasurementMetaNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            bodyMeasurementMeta = arguments?.getParcelable<BodyMeasurementMetaNew>("BodyMeasurementData")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<VicorderContraFragmentBinding>(
            inflater,
            R.layout.vicorder_contra_fragment,
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
            ViewModelProviders.of(this).get(VicorderQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participant

        binding.previousButton.singleClick {
            navController().popBackStack()
        }

        binding.nextButton.singleClick {
            if (validateNextButton()) {
                val bundle = Bundle()
                bundle.putParcelable("participant", participant)
                bundle.putParcelable("BodyMeasurementData" , bodyMeasurementMeta)
                navController().navigate(R.id.action_contra_stationQuestionFragment, bundle)
            }
        }

        binding.radioGroupFistula.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noFistula) {
                binding.radioGroupFistulaValue = false
                viewModel.setHadFistula("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftFistula) {
                binding.radioGroupFistulaValue = false
                viewModel.setHadFistula("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightFistula) {
                binding.radioGroupFistulaValue = false
                viewModel.setHadFistula("right")

            } else {
                binding.radioGroupFistulaValue = false
                viewModel.setHadFistula("both")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupMastectomy.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noMastectomy) {
                binding.radioGroupMastectomyValue = false
                viewModel.setHaveMastectomy("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftMastectomy) {
                binding.radioGroupMastectomyValue = false
                viewModel.setHaveMastectomy("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightMastectomy) {
                binding.radioGroupMastectomyValue = false
                viewModel.setHaveMastectomy("right")

            } else {
                binding.radioGroupMastectomyValue = false
                viewModel.setHaveMastectomy("both")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupLymph.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noLymph) {
                binding.radioGroupLymphValue = false
                viewModel.setHaveLymph("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftLymph) {
                binding.radioGroupLymphValue = false
                viewModel.setHaveLymph("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightLymph) {
                binding.radioGroupLymphValue = false
                viewModel.setHaveLymph("right")

            } else {
                binding.radioGroupLymphValue = false
                viewModel.setHaveLymph("both")
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
        if(viewModel.hadFistula.value == null)
        {
            binding.radioGroupFistulaValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveMastectomy.value == null)
        {
            binding.radioGroupMastectomyValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.hadLymph.value == null)
        {
            binding.radioGroupLymphValue = true
            binding.executePendingBindings()
            return false
        }
        else if(viewModel.haveTrauma.value == null)
        {
            binding.radioGroupTraumaValue = true
            binding.executePendingBindings()
            return false
        }

        if(!validateContra()) {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "ParticipantRequest" to participant,
                "contraindications" to getContraindications(),
                "comment" to "",
                "skipped" to true)
            reasonDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadFistula = viewModel.hadFistula.value
        val haveMastectomy = viewModel.haveMastectomy.value
        val haveLymph = viewModel.hadLymph.value
        val haveTrauma = viewModel.haveTrauma.value

        var FistulaMap = mutableMapOf<String, String>()
        FistulaMap["id"] = "VICI1"
        FistulaMap["question"] = getString(R.string.vicorder_fistula)
        FistulaMap["answer"] = hadFistula!!

        contraindications.add(FistulaMap)

        var mastectomyMap = mutableMapOf<String, String>()
        mastectomyMap["id"] = "VICI2"
        mastectomyMap["question"] = getString(R.string.vicorder_mastectomy)
        mastectomyMap["answer"] = haveMastectomy!!

        contraindications.add(mastectomyMap)

        var lymphMap = mutableMapOf<String, String>()
        lymphMap["id"] = "VICI3"
        lymphMap["question"] = getString(R.string.vicorder_lymph)
        lymphMap["answer"] = haveLymph!!

        contraindications.add(lymphMap)

        var traumaMap = mutableMapOf<String, String>()
        traumaMap["id"] = "VICI4"
        traumaMap["question"] = getString(R.string.vicorder_trauma)
        traumaMap["answer"] = haveTrauma!!

        contraindications.add(traumaMap)

        return contraindications
    }

    private fun validateContra(): Boolean
    {
        val fistula = viewModel.hadFistula.value
        val mastectomy = viewModel.haveMastectomy.value
        val lymph = viewModel.hadLymph.value
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
