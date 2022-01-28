package org.singapore.ghru.ui.treadmill.contraindications

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
import org.singapore.ghru.databinding.TreadmillContraFragmentBinding
import org.singapore.ghru.databinding.UltrasoundContraFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_TREADMILL
import org.singapore.ghru.ui.ecg.questions.TYPE_ULTRASOUND
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class TreadmillContraFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TreadmillContraFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var viewModel: TreadmillContraViewModel

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
        val dataBinding = DataBindingUtil.inflate<TreadmillContraFragmentBinding>(
            inflater,
            R.layout.treadmill_contra_fragment,
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
            ViewModelProviders.of(this).get(TreadmillContraViewModel::class.java)
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
                navController().navigate(R.id.action_contraFragment_to_beforeTestFragment, bundle)
            }
        }

        binding.radioGroupBloodPressure.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noBloodPressure) {
                binding.radioGroupBloodPressureValue = false
                viewModel.setBloodPressure(false)

            } else {
                binding.radioGroupBloodPressureValue = false
                viewModel.setBloodPressure(true)

            }
            binding.executePendingBindings()
        }

        binding.radioGroupMedical.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noMedical) {
                binding.radioGroupMedicalValue = false
                viewModel.setMedicalConditions(false)

            } else {
                binding.radioGroupMedicalValue = false
                viewModel.setMedicalConditions(true)

            }
            binding.executePendingBindings()
        }

        binding.radioGroupChestPain.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noChestPain) {
                binding.radioGroupChestPainValue = false
                viewModel.setChestPain(false)

            } else {
                binding.radioGroupChestPainValue = false
                viewModel.setChestPain(true)

            }
            binding.executePendingBindings()
        }

        binding.radioGroupDiabetes.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noDiabetes) {
                binding.radioGroupDiabetesValue = false
                viewModel.setDiabetes(false)

            } else {
                binding.radioGroupDiabetesValue = false
                viewModel.setDiabetes(true)

            }
            binding.executePendingBindings()
        }

        binding.radioGroupOxygen.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noOxygen) {
                binding.radioGroupOxygenValue = false
                viewModel.setOxygen(false)

            } else {
                binding.radioGroupOxygenValue = false
                viewModel.setOxygen(true)

            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.bloodPressure.value == null) {
            binding.radioGroupBloodPressureValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.medicalConditions.value == null) {
            binding.radioGroupMedicalValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.chestPain.value == null) {
            binding.radioGroupChestPainValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.diabetes.value == null) {
            binding.radioGroupDiabetesValue = true
            binding.executePendingBindings()
            return false
        }

//        if(viewModel.oxygen.value == null) {
//            binding.radioGroupOxygenValue = true
//            binding.executePendingBindings()
//            return false
//        }

        if(viewModel.bloodPressure.value == true || viewModel.medicalConditions.value == true ||
            viewModel.chestPain.value == true || viewModel.diabetes.value == true ) {
            val skipDialogFragment = ECGSkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participant,
                "contraindications" to getContraindications(),
                "type" to TYPE_TREADMILL
            )
            skipDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val bloodPressure = viewModel.bloodPressure.value
        val medical = viewModel.medicalConditions.value
        val chestPain = viewModel.chestPain.value
        val diabetes = viewModel.diabetes.value
//        val oxygen = viewModel.oxygen.value

        var bloodPressureMap = mutableMapOf<String, String>()
        bloodPressureMap["id"] = "TMCI1"
        bloodPressureMap["question"] = getString(R.string.treadmill_blood_pressure_question)
        bloodPressureMap["answer"] = if (bloodPressure!!) "yes" else "no"

        contraindications.add(bloodPressureMap)

        var medicalMap = mutableMapOf<String, String>()
        medicalMap["id"] = "TMCI2"
        medicalMap["question"] = getString(R.string.treadmill_medical_condition_question)
        medicalMap["answer"] = if (medical!!) "yes" else "no"

        contraindications.add(medicalMap)

        var chestMap = mutableMapOf<String, String>()
        chestMap["id"] = "TMCI3"
        chestMap["question"] = getString(R.string.treadmill_chest_pain_question)
        chestMap["answer"] = if (chestPain!!) "yes" else "no"

        contraindications.add(chestMap)

        var diabetesMap = mutableMapOf<String, String>()
        diabetesMap["id"] = "TMCI4"
        diabetesMap["question"] = "Have uncontrolled diabetes (blood glucose <4 mmol/L or >11 mmol/L) prior to the treadmill test?"
        diabetesMap["answer"] = if (diabetes!!) "yes" else "no"

        contraindications.add(diabetesMap)

//        var oxygenMap = mutableMapOf<String, String>()
//        oxygenMap["id"] = "TMCI5"
//        oxygenMap["question"] = "Does participant have SBP < 100 mmHg / DBP < 40 mmHg / HR < 50bpm (LES)"
//        oxygenMap["answer"] = if (oxygen!!) "yes" else "no"
//
//        contraindications.add(oxygenMap)

        return contraindications
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
