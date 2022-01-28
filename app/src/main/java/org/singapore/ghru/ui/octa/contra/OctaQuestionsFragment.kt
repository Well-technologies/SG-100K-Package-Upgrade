package org.singapore.ghru.ui.octa.contra

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
import org.singapore.ghru.databinding.OctaQuestionsFragmentBinding
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_FUNDO
import org.singapore.ghru.ui.octa.reading.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class OctaQuestionsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<OctaQuestionsFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var octaViewModel: OctaQuestionsViewModel

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
        val dataBinding = DataBindingUtil.inflate<OctaQuestionsFragmentBinding>(
            inflater,
            R.layout.octa_questions_fragment,
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
        octaViewModel = activity?.run {
            ViewModelProviders.of(this).get(OctaQuestionsViewModel::class.java)
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
                navController().navigate(R.id.action_questionnaireFragment_to_readingFragment, bundle)
            }
        }

        binding.radioGroupSurgery.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noSurgery) {
                binding.radioGroupSurgeryValue = false
                octaViewModel.setHadSurgery("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftSurgery) {
                binding.radioGroupSurgeryValue = false
                octaViewModel.setHadSurgery("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightSurgery) {
                binding.radioGroupSurgeryValue = false
                octaViewModel.setHadSurgery("right")

            } else {
                binding.radioGroupSurgeryValue = false
                octaViewModel.setHadSurgery("both")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupPain.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noPain) {
                binding.radioGroupPainValue = false
                octaViewModel.setHaveEyaPain("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftPain) {
                binding.radioGroupPainValue = false
                octaViewModel.setHaveEyaPain("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightPain) {
                binding.radioGroupPainValue = false
                octaViewModel.setHaveEyaPain("right")

            } else {
                binding.radioGroupPainValue = false
                octaViewModel.setHaveEyaPain("both")
            }
            binding.executePendingBindings()
        }

        binding.radioGroupRedness.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noRedness) {
                binding.radioGroupRednessValue = false
                octaViewModel.setHaveEyeRedness("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftRedness) {
                binding.radioGroupRednessValue = false
                octaViewModel.setHaveEyeRedness("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightRedness) {
                binding.radioGroupRednessValue = false
                octaViewModel.setHaveEyeRedness("right")

            } else {
                binding.radioGroupRednessValue = false
                octaViewModel.setHaveEyeRedness("both")
            }
            binding.executePendingBindings()
        }

//        binding.radioGroupBlurring.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.noBlurring) {
//                binding.radioGroupBlurringValue = false
//                octaViewModel.setHaveBlurring("no")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.leftBlurring) {
//                binding.radioGroupBlurringValue = false
//                octaViewModel.setHaveBlurring("left")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.rightBlurring) {
//                binding.radioGroupBlurringValue = false
//                octaViewModel.setHaveBlurring("right")
//
//            } else {
//                binding.radioGroupBlurringValue = false
//                octaViewModel.setHaveBlurring("both")
//            }
//            binding.executePendingBindings()
//        }
//
//        binding.radioGroupDouble.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.noDouble) {
//                binding.radioGroupDoubleValue = false
//                octaViewModel.setHaveDouble("no")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.leftDouble) {
//                binding.radioGroupDoubleValue = false
//                octaViewModel.setHaveDouble("left")
//
//            } else if (radioGroup.checkedRadioButtonId == R.id.rightDouble) {
//                binding.radioGroupDoubleValue = false
//                octaViewModel.setHaveDouble("right")
//
//            } else {
//                binding.radioGroupDoubleValue = false
//                octaViewModel.setHaveDouble("both")
//            }
//            binding.executePendingBindings()
//        }
    }

    private fun validateNextButton(): Boolean {
        if(octaViewModel.hadSurgery.value == null)
        {
            binding.radioGroupSurgeryValue = true
            binding.executePendingBindings()
            return false
        }
        else if(octaViewModel.haveEyePain.value == null)
        {
            binding.radioGroupPainValue = true
            binding.executePendingBindings()
            return false
        }
        else if(octaViewModel.hadEyeRedness.value == null)
        {
            binding.radioGroupRednessValue = true
            binding.executePendingBindings()
            return false
        }

        if(!validateContra()) {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf(
                "participant" to participant,
                "contraindications" to getContraindications(),
                "skipped" to true,
                "comment" to "")
            reasonDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadSurgery = octaViewModel.hadSurgery.value
        val havePain = octaViewModel.haveEyePain.value
        val haveEyeRedness = octaViewModel.hadEyeRedness.value

        var surgeryMap = mutableMapOf<String, String>()
        surgeryMap["id"] = getString(R.string.octa_surgery_question_id_1)
        surgeryMap["question"] = getString(R.string.octa_surgery_question)
        surgeryMap["answer"] = hadSurgery!!

        contraindications.add(surgeryMap)

        var painMap = mutableMapOf<String, String>()
        painMap["id"] = getString(R.string.octa_surgery_question_id_2)
        painMap["question"] = getString(R.string.funduscopy_symptoms_question)
        painMap["answer"] = havePain!!

        contraindications.add(painMap)

        var rednessMap = mutableMapOf<String, String>()
        rednessMap["id"] = getString(R.string.octa_surgery_question_id_3)
        rednessMap["question"] = getString(R.string.funduscopy_redness_question)
        rednessMap["answer"] = haveEyeRedness!!

        contraindications.add(rednessMap)

        return contraindications
    }

    private fun validateContra(): Boolean
    {
        val hadSurgery = octaViewModel.hadSurgery.value
        val havePain = octaViewModel.haveEyePain.value
        val haveEyeRedness = octaViewModel.hadEyeRedness.value

        if (hadSurgery == "no" && havePain == "no" && haveEyeRedness == "no")
        {
            return true
        }
        else if (hadSurgery == "both" || havePain == "both" || haveEyeRedness == "both")
        {
            return false
        }
        else if (hadSurgery == "left" && (havePain == "right" || haveEyeRedness == "right"))
        {
            return false
        }
        else if (havePain == "left" && (hadSurgery == "right" || haveEyeRedness == "right"))
        {
            return false
        }
        else if (haveEyeRedness == "left" && (havePain == "right" || hadSurgery == "right" ))
        {
            return false
        }
        else if (hadSurgery == "right" && (havePain == "left" || haveEyeRedness == "left"))
        {
            return false
        }
        else if (havePain == "right" && (haveEyeRedness == "left" || hadSurgery == "left"))
        {
            return false
        }
        else if (haveEyeRedness == "right" && (havePain == "left" || hadSurgery == "left"))
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
