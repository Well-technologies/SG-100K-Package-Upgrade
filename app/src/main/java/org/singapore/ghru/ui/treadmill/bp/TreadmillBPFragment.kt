package org.singapore.ghru.ui.treadmill.bp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import org.singapore.ghru.BuildConfig
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.TreadmillBpFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.TreadmillBPRecordRxBus
import org.singapore.ghru.ui.treadmill.TreadmillMainViewModel
import org.singapore.ghru.ui.treadmill.bp.validationError.MeasurementErrorDialogFragment
import org.singapore.ghru.ui.treadmill.contraindications.TreadmillContraViewModel
import org.singapore.ghru.util.Constants
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.TreadmillBP
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class TreadmillBPFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var participant: ParticipantRequest? = null

    var binding by autoCleared<TreadmillBpFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var isValidRecord: Boolean = false

    @Inject
    lateinit var viewModel: TreadmillBPViewModel
    private lateinit var questionnaireViewModel: TreadmillContraViewModel

    var bloodPressureMain: TreadmillBP? = null
    var bloodPressure: TreadmillBP? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            bloodPressureMain = arguments?.getParcelable<TreadmillBP>("bp")!!
            participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!

            bloodPressure = TreadmillBP(0)
            bloodPressure!!.stage.value = bloodPressureMain!!.stage.value
            bloodPressure!!.systolic.value = bloodPressureMain!!.systolic.value
            bloodPressure!!.diastolic.value = bloodPressureMain!!.diastolic.value
            bloodPressure!!.pulse.value = bloodPressureMain!!.pulse.value
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<TreadmillBpFragmentBinding>(
            inflater,
            R.layout.treadmill_bp_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(TreadmillContraViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)

        bloodPressure?.systolic?.observe(
            this,
            Observer { systolic ->
                validateSystolicBp(systolic!!)
                validateNextButton()
            })
        bloodPressure?.diastolic?.observe(
            this,
            Observer { diastolic ->
                validateDiatolicBp(diastolic!!)
                validateNextButton()
            })
        bloodPressure?.pulse?.observe(this, Observer { pluse ->
            validatePulse(pluse)
            validateNextButton()
        })

        if (bloodPressure!!.stage.value == "Resting (0%)")
        {
            binding.systolicInputLayout.visibility = View.VISIBLE
            binding.diastolicInputLayout.visibility = View.VISIBLE
            binding.systolicSeparator.visibility = View.VISIBLE
        }
        else if (bloodPressure!!.stage.value == "After Test")
        {
            binding.systolicInputLayout.visibility = View.VISIBLE
            binding.diastolicInputLayout.visibility = View.VISIBLE
            binding.systolicSeparator.visibility = View.VISIBLE
        }
        else
        {
            binding.systolicInputLayout.visibility = View.GONE
            binding.diastolicInputLayout.visibility = View.GONE
            binding.systolicSeparator.visibility = View.GONE
        }

//        if (BuildConfig.DEBUG) {
//            bloodPressure?.systolic?.value = "120"
//            bloodPressure?.diastolic?.value = "90"
//            bloodPressure?.pulse?.value = "80"
//        }

        binding.buttonClose.singleClick {
            navController().popBackStack()
            view?.hideKeyboard()
        }
        binding.buttonRecord.singleClick {
            validateNextButton()

            if (bloodPressure!!.stage.value == "Resting (0%)")
            {
                if (validateResting())
                {
                    if (isValidRecord)
                    {
                        bloodPressureMain!!.systolic.value = bloodPressure!!.systolic.value
                        bloodPressureMain!!.diastolic.value = bloodPressure!!.diastolic.value
                        bloodPressureMain!!.pulse.value = bloodPressure!!.pulse.value

                        TreadmillBPRecordRxBus.getInstance().post(bloodPressureMain!!)
                        binding.root.hideKeyboard()
                        navController().popBackStack()
                    }
                }
            }
            else
            {
                if (isValidRecord)
                {
                    bloodPressureMain!!.systolic.value = bloodPressure!!.systolic.value
                    bloodPressureMain!!.diastolic.value = bloodPressure!!.diastolic.value
                    bloodPressureMain!!.pulse.value = bloodPressure!!.pulse.value

                    TreadmillBPRecordRxBus.getInstance().post(bloodPressureMain!!)
                    binding.root.hideKeyboard()
                    navController().popBackStack()
                }
            }
        }

        binding.bloodPressure = bloodPressure
    }

    private fun validateSystolicBp(systolic: String) {
        try {
            val systolicVal: Double = systolic.toDouble()
            if (systolicVal >= Constants.BP_SYSTOLIC_MIN_VAL && systolicVal <= Constants.BP_SYSTOLIC_MAX_VAL) {
                binding.systolicInputLayout.error = null
                viewModel.isValidSystolicBp = false

            } else {
                viewModel.isValidSystolicBp = true
                binding.systolicInputLayout.error = getString(R.string.error_not_in_range)
            }

            validateNextButton()

        } catch (e: Exception) {
            viewModel.isValidSystolicBp = true
            binding.systolicInputLayout.error = getString(R.string.error_invalid_input)
        }
    }

    private fun validateDiatolicBp(diatolic: String) {
        try {
            val diatolicVal: Double = diatolic.toDouble()
            if (diatolicVal >= Constants.BP_DIATOLIC_MIN_VAL && diatolicVal <= Constants.BP_DIATOLIC_MAX_VAL) {
                binding.diastolicInputLayout.error = null
                viewModel.isValidDiastolicBp = false
            } else {
                viewModel.isValidDiastolicBp = true
                binding.diastolicInputLayout.error = getString(R.string.error_not_in_range)
            }
            validateNextButton()
        } catch (e: Exception) {
            binding.diastolicInputLayout.error = getString(R.string.error_invalid_input)
        }
    }


    private fun validatePulse(pulse: String) {
        try {
            val pulseVal: Double = pulse.toDouble()
            if (pulseVal >= Constants.BP_PULSE_MIN_VAL && pulseVal <= Constants.BP_PULSE_MAX_VAL) {
                binding.pulseInputLayout.error = null
                viewModel.isValidPuls = false
            } else {
                viewModel.isValidPuls = true
                binding.pulseInputLayout.error = getString(R.string.error_not_in_range)
            }
            validateNextButton()
        } catch (e: Exception) {
            binding.pulseInputLayout.error = getString(R.string.error_invalid_input)
        }
    }


    private fun validateNextButton() {
        if (bloodPressure!!.stage.value == "Resting (0%)")
        {
            isValidRecord = (!binding.bloodPressure?.systolic?.value.isNullOrBlank()
                    && !binding.bloodPressure?.diastolic?.value.isNullOrBlank()
                    && !binding.bloodPressure?.pulse?.value.isNullOrBlank()
                    && !viewModel.isValidDiastolicBp
                    && !viewModel.isValidSystolicBp
                    && !viewModel.isValidPuls)

        }
        else if (bloodPressure!!.stage.value == "After Test")
        {
            isValidRecord = (!binding.bloodPressure?.systolic?.value.isNullOrBlank()
                    && !binding.bloodPressure?.diastolic?.value.isNullOrBlank()
                    && !binding.bloodPressure?.pulse?.value.isNullOrBlank()
                    && !viewModel.isValidDiastolicBp
                    && !viewModel.isValidSystolicBp
                    && !viewModel.isValidPuls)
        }
        else
        {
            isValidRecord = (!binding.bloodPressure?.pulse?.value.isNullOrBlank()
                    ||!viewModel.isValidPuls)
        }

    }

    private fun validateResting(): Boolean
    {
        if (bloodPressure!!.stage.value == "Resting (0%)")
        {
            if ((!binding.systolicEditText.text.toString().equals("")) && (!binding.diastolicEditText.text.toString().equals("")))
            {
                if (binding.systolicEditText.text.toString().toInt()>= 160 || binding.diastolicEditText.text.toString().toInt() >= 100)
                {
                    val measurementError = MeasurementErrorDialogFragment()
                    measurementError.arguments = bundleOf(
                        "ParticipantRequest" to  participant,
                        "contraindications" to getContraindications(),
                        "skipped" to false
                    )
                    measurementError.show(fragmentManager!!)
                    return false
                }
                else
                {
                    return true
                }
            }
        }
        return false
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val bloodPressure = questionnaireViewModel.bloodPressure.value
        val medical = questionnaireViewModel.medicalConditions.value
        val chestPain = questionnaireViewModel.chestPain.value
        val diabetes = questionnaireViewModel.diabetes.value
//        val oxygen = questionnaireViewModel.oxygen.value

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
//
//        contraindications.add(oxygenMap)

        return contraindications
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
