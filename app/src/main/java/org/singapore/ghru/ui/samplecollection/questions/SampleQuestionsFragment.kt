package org.singapore.ghru.ui.samplecollection.questions

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
import org.singapore.ghru.databinding.SampleQuestionsFragmentBinding
import org.singapore.ghru.ui.samplecollection.questions.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class SampleQuestionsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<SampleQuestionsFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var sampleQuestionsViewModel: SampleQuestionsViewModel

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
        val dataBinding = DataBindingUtil.inflate<SampleQuestionsFragmentBinding>(
            inflater,
            R.layout.sample_questions_fragment,
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
        sampleQuestionsViewModel = activity?.run {
            ViewModelProviders.of(this).get(SampleQuestionsViewModel::class.java)
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
                navController().navigate(R.id.action_sampleQuestionFragmenet_to_bagScannedFragment, bundle)
            }
        }

        binding.radioGroupEyes.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noEyes) {
                binding.radioGroupEyesValue = false
                sampleQuestionsViewModel.setHadDermatitisEyes("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.yesEyes) {
                binding.radioGroupEyesValue = false
                sampleQuestionsViewModel.setHadDermatitisEyes("yes")

            } else {
                binding.radioGroupEyesValue = false
                sampleQuestionsViewModel.setHadDermatitisEyes("unsure")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupNeck.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noNeck) {
                binding.radioGroupNeckValue = false
                sampleQuestionsViewModel.setHadDermatitisNeck("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.yesNeck) {
                binding.radioGroupNeckValue = false
                sampleQuestionsViewModel.setHadDermatitisNeck("yes")

            } else {
                binding.radioGroupNeckValue = false
                sampleQuestionsViewModel.setHadDermatitisNeck("unsure")

            }
            binding.executePendingBindings()
        }

        binding.radioGroupElbow.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noElbow) {
                binding.radioGroupElbowValue = false
                sampleQuestionsViewModel.setHadDermatitisElbow("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.yesElbow) {
                binding.radioGroupElbowValue = false
                sampleQuestionsViewModel.setHadDermatitisElbow("yes")

            } else {
                binding.radioGroupElbowValue = false
                sampleQuestionsViewModel.setHadDermatitisElbow("unsure")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupFront.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noFront) {
                binding.radioGroupFrontValue = false
                sampleQuestionsViewModel.setHadDermatitisFrontKnees("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.yesFront) {
                binding.radioGroupFrontValue = false
                sampleQuestionsViewModel.setHadDermatitisFrontKnees("yes")

            } else {
                binding.radioGroupFrontValue = false
                sampleQuestionsViewModel.setHadDermatitisFrontKnees("unsure")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupBehind.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noBehind) {
                binding.radioGroupBehindValue = false
                sampleQuestionsViewModel.setHadDermatitisBehindKnees("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.yesBehind) {
                binding.radioGroupBehindValue = false
                sampleQuestionsViewModel.setHadDermatitisBehindKnees("yes")

            } else {
                binding.radioGroupBehindValue = false
                sampleQuestionsViewModel.setHadDermatitisBehindKnees("unsure")
            }
            binding.executePendingBindings()
        }

        binding.skipButton.singleClick {

            val skipDialogFragment = ReasonDialogFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participant)
            skipDialogFragment.show(fragmentManager!!)
        }
    }

    private fun validateNextButton(): Boolean {
        if(sampleQuestionsViewModel.hadDermatitisEyes.value == null)
        {
            binding.radioGroupEyesValue = true
            binding.executePendingBindings()
            return false
        }
        else if(sampleQuestionsViewModel.hadDermatitisNeck.value == null)
        {
            binding.radioGroupNeckValue = true
            binding.executePendingBindings()
            return false
        }
        else if(sampleQuestionsViewModel.hadDermatitisElbow.value == null)
        {
            binding.radioGroupElbowValue = true
            binding.executePendingBindings()
            return false
        }
        else if(sampleQuestionsViewModel.hadDermatitisFrontKnees.value == null)
        {
            binding.radioGroupFrontValue = true
            binding.executePendingBindings()
            return false
        }
        else if(sampleQuestionsViewModel.hadDermatitisBehindKnees.value == null)
        {
            binding.radioGroupBehindValue = true
            binding.executePendingBindings()
            return false
        }

//        if(sampleQuestionsViewModel.hadDermatitisEyes.value == null
//            || sampleQuestionsViewModel.hadDermatitisNeck.value ==  "yes"
//            || sampleQuestionsViewModel.hadDermatitisElbow.value ==  "yes"
//            || sampleQuestionsViewModel.hadDermatitisFrontKnees.value ==  "yes"
//            || sampleQuestionsViewModel.hadDermatitisBehindKnees.value ==  "yes") {
//            val skipDialogFragment = ECGSkipFragment()
//            skipDialogFragment.arguments = bundleOf("participant" to participant,
//                "contraindications" to getContraindications(),
//                "type" to TYPE_SAMPLE)
//            skipDialogFragment.show(fragmentManager!!)
//
//            return false
//        }

        return true
    }

//    private fun getContraindications(): MutableList<Map<String, String>> {
//        var contraindications = mutableListOf<Map<String, String>>()
//
//        val hadEye = sampleQuestionsViewModel.hadDermatitisEyes.value
//        val haveNeck = sampleQuestionsViewModel.hadDermatitisNeck.value
//        val haveElbow = sampleQuestionsViewModel.hadDermatitisElbow.value
//        val haveFront = sampleQuestionsViewModel.hadDermatitisFrontKnees.value
//        val haveBehind = sampleQuestionsViewModel.hadDermatitisBehindKnees.value
//
//        var eyeMap = mutableMapOf<String, String>()
//        eyeMap["id"] = getString(R.string.sample_question_id_1)
//        eyeMap["question"] = getString(R.string.sample_question_1)
//        eyeMap["answer"] = hadEye!!
//
//        contraindications.add(eyeMap)
//
//        var neckMap = mutableMapOf<String, String>()
//        neckMap["id"] = getString(R.string.sample_question_id_2)
//        neckMap["question"] = getString(R.string.sample_question_2)
//        neckMap["answer"] = haveNeck!!
//
//        contraindications.add(neckMap)
//
//        var elbowMap = mutableMapOf<String, String>()
//        elbowMap["id"] = getString(R.string.sample_question_id_3)
//        elbowMap["question"] = getString(R.string.sample_question_3)
//        elbowMap["answer"] = haveElbow!!
//
//        contraindications.add(elbowMap)
//
//        var frontMap = mutableMapOf<String, String>()
//        frontMap["id"] = getString(R.string.sample_question_id_4)
//        frontMap["question"] = getString(R.string.sample_question_4)
//        frontMap["answer"] = haveFront!!
//
//        contraindications.add(frontMap)
//
//        var behindMap = mutableMapOf<String, String>()
//        behindMap["id"] = getString(R.string.sample_question_id_5)
//        behindMap["question"] = getString(R.string.sample_question_5)
//        behindMap["answer"] = haveBehind!!
//
//        contraindications.add(behindMap)
//
//        return contraindications
//    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
