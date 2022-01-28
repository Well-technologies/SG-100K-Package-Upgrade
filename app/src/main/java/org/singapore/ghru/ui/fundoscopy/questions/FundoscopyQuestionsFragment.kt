package org.singapore.ghru.ui.fundoscopy.questions

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
import org.singapore.ghru.databinding.DisplayBarcodeBinding
import org.singapore.ghru.databinding.FundoscopyQuestionsFragmentBinding
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_FUNDO
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class FundoscopyQuestionsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FundoscopyQuestionsFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var fundosQuestionnaireViewModel: FundoscopyQuestionsViewModel

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
        val dataBinding = DataBindingUtil.inflate<FundoscopyQuestionsFragmentBinding>(
            inflater,
            R.layout.fundoscopy_questions_fragment,
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
        fundosQuestionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(FundoscopyQuestionsViewModel::class.java)
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
                navController().navigate(R.id.action_questionnaireFragment_to_guideMainFragment, bundle)
            }
        }

        binding.radioGroupSurgery.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noSurgery) {
                binding.radioGroupSurgeryValue = false
                fundosQuestionnaireViewModel.setHadSurgery("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftSurgery) {
                binding.radioGroupSurgeryValue = false
                fundosQuestionnaireViewModel.setHadSurgery("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightSurgery) {
                binding.radioGroupSurgeryValue = false
                fundosQuestionnaireViewModel.setHadSurgery("right")

            } else {
                binding.radioGroupSurgeryValue = false
                fundosQuestionnaireViewModel.setHadSurgery("both")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupSymptoms.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noSymptoms) {
                binding.radioGroupSymptomsValue = false
                fundosQuestionnaireViewModel.setHaveEyaPain("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftSymptoms) {
                binding.radioGroupSymptomsValue = false
                fundosQuestionnaireViewModel.setHaveEyaPain("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightSymptoms) {
                binding.radioGroupSymptomsValue = false
                fundosQuestionnaireViewModel.setHaveEyaPain("right")

            } else {
                binding.radioGroupSymptomsValue = false
                fundosQuestionnaireViewModel.setHaveEyaPain("both")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupRedness.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noRedness) {
                binding.radioGroupRednessValue = false
                fundosQuestionnaireViewModel.setHaveEyeRedness("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftRedness) {
                binding.radioGroupRednessValue = false
                fundosQuestionnaireViewModel.setHaveEyeRedness("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightRedness) {
                binding.radioGroupRednessValue = false
                fundosQuestionnaireViewModel.setHaveEyeRedness("right")

            } else {
                binding.radioGroupRednessValue = false
                fundosQuestionnaireViewModel.setHaveEyeRedness("both")
            }
            binding.executePendingBindings()
        }

//        binding.radioGroupBlurring.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.noBlurring) {
//                binding.radioGroupBlurringValue = false
//                fundosQuestionnaireViewModel.setHaveBlurring("no")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.leftBlurring) {
//                binding.radioGroupBlurringValue = false
//                fundosQuestionnaireViewModel.setHaveBlurring("left")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.rightBlurring) {
//                binding.radioGroupBlurringValue = false
//                fundosQuestionnaireViewModel.setHaveBlurring("right")
//
//            } else {
//                binding.radioGroupBlurringValue = false
//                fundosQuestionnaireViewModel.setHaveBlurring("both")
//            }
//            binding.executePendingBindings()
//        }
//
//        binding.radioGroupDouble.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.noDouble) {
//                binding.radioGroupDoubleValue = false
//                fundosQuestionnaireViewModel.setHaveDouble("no")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.leftDouble) {
//                binding.radioGroupDoubleValue = false
//                fundosQuestionnaireViewModel.setHaveDouble("left")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.rightDouble) {
//                binding.radioGroupDoubleValue = false
//                fundosQuestionnaireViewModel.setHaveDouble("right")
//
//            } else {
//                binding.radioGroupDoubleValue = false
//                fundosQuestionnaireViewModel.setHaveDouble("both")
//            }
//            binding.executePendingBindings()
//        }
    }

    private fun validateNextButton(): Boolean {
        if(fundosQuestionnaireViewModel.hadSurgery.value == null)
        {
            binding.radioGroupSurgeryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(fundosQuestionnaireViewModel.haveEyePain.value == null)
        {
            binding.radioGroupSymptomsValue = true
            binding.executePendingBindings()
            return false
        }
        else if(fundosQuestionnaireViewModel.hadEyeRedness.value == null)
        {
            binding.radioGroupRednessValue = true
            binding.executePendingBindings()
            return false
        }
//        else if(fundosQuestionnaireViewModel.haveBlurring.value == null)
//        {
//            binding.radioGroupBlurringValue = true
//            binding.executePendingBindings()
//            return false
//        }
//        else if(fundosQuestionnaireViewModel.haveDouble.value == null)
//        {
//            binding.radioGroupDoubleValue = true
//            binding.executePendingBindings()
//            return false
//        }

        if(fundosQuestionnaireViewModel.hadSurgery.value ==  "both"
            || fundosQuestionnaireViewModel.haveEyePain.value ==  "both"
            || fundosQuestionnaireViewModel.hadEyeRedness.value ==  "both") {
            val skipDialogFragment = ECGSkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participant,
                "contraindications" to getContraindications(),
                "type" to TYPE_FUNDO)
            skipDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadSurgery = fundosQuestionnaireViewModel.hadSurgery.value
        val haveSymptoms = fundosQuestionnaireViewModel.haveEyePain.value
        val haveEyeRedness = fundosQuestionnaireViewModel.hadEyeRedness.value
//        val haveBlurring = fundosQuestionnaireViewModel.haveBlurring.value
//        val haveDouble = fundosQuestionnaireViewModel.haveDouble.value

        var symptomsMap = mutableMapOf<String, String>()
        symptomsMap["id"] = "FCI1"
        symptomsMap["question"] = getString(R.string.funduscopy_symptoms_question)
        symptomsMap["answer"] = haveSymptoms!!

        contraindications.add(symptomsMap)

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = "FCI2"
        surgeryMap["question"] = getString(R.string.funduscopy_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var rednessMap = mutableMapOf<String, String>()
        rednessMap["id"] = "FCI3"
        rednessMap["question"] = getString(R.string.funduscopy_redness_question)
        rednessMap["answer"] = haveEyeRedness!!

        contraindications.add(rednessMap)

//        var blurringMap = mutableMapOf<String, String>()
//        surgeryMap["id"] = "FCI4"
//        surgeryMap["question"] = getString(R.string.funduscopy_blurring_question)
//        surgeryMap["answer"] = haveBlurring!!
//
//        contraindications.add(blurringMap)
//
//        var doubleMap = mutableMapOf<String, String>()
//        symptomsMap["id"] = "FCI5"
//        symptomsMap["question"] = getString(R.string.funduscopy_double_question)
//        symptomsMap["answer"] = haveDouble!!
//
//        contraindications.add(doubleMap)

        return contraindications
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
