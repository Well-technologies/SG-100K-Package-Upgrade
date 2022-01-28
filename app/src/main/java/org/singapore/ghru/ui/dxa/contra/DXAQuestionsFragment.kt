package org.singapore.ghru.ui.dxa.contra

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.DisplayBarcodeBinding
import org.singapore.ghru.databinding.DxaQuestionnaireFragmentBinding
import org.singapore.ghru.databinding.FundoscopyQuestionsFragmentBinding
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_DXA
import org.singapore.ghru.ui.ecg.questions.TYPE_FUNDO
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.BodyMeasurementMeta
import org.singapore.ghru.vo.request.BodyMeasurementMetaNew
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class DXAQuestionsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<DxaQuestionnaireFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: DXAQuestionsViewModel

    private var participant: ParticipantRequest? = null
    private var examinationList: MutableList<String> = arrayListOf()
    private var selectedExamination: String? = null
    var bodyMeasurementMeta: BodyMeasurementMetaNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            bodyMeasurementMeta = arguments?.getParcelable<BodyMeasurementMetaNew>("BodyMeasurementData")
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<DxaQuestionnaireFragmentBinding>(
            inflater,
            R.layout.dxa_questionnaire_fragment,
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
            ViewModelProviders.of(this).get(DXAQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participant
        //binding.viewModel = viewModel

//        binding.previousButton.singleClick {
//            navController().popBackStack()
//        }

        examinationList.clear()
        examinationList.add(getString(R.string.unknown_2))
        examinationList.add(getString(R.string.dxa_xray_drop_down_item1))
        examinationList.add(getString(R.string.dxa_xray_drop_down_item2))
        examinationList.add(getString(R.string.dxa_xray_drop_down_item3))
        examinationList.add(getString(R.string.dxa_xray_drop_down_item4))
        examinationList.add(getString(R.string.dxa_xray_drop_down_item5))

        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, examinationList)
        binding.examinationSpinner.setAdapter(adapter)

        binding.examinationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedExamination = null
                } else {
                    binding.textViewExamError.visibility = View.GONE
                    selectedExamination = binding.examinationSpinner.selectedItem.toString()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }

        binding.nextButton.singleClick {
            if (validateNextButton()) {
                val bundle = Bundle()
                bundle.putParcelable("ParticipantRequest", participant)
                bundle.putParcelable("BodyMeasurementData" , bodyMeasurementMeta)
                navController().navigate(R.id.action_dxaContraFragment_to_dxaHomeFragment, bundle)
            }
        }

        binding.radioGroupXray.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noXray) {
                binding.radioGroupXrayValue = false
                viewModel.setHadXray(false)
                binding.examinationLayout.visibility = View.GONE

            } else {
                binding.radioGroupXrayValue = false
                viewModel.setHadXray(true)
                binding.examinationLayout.visibility = View.VISIBLE

            }
            binding.executePendingBindings()
        }


    }

    private fun validateNextButton(): Boolean {
        if(viewModel.hadXray.value == null) {
            binding.radioGroupXrayValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.hadXray.value ==  true)
        {
            if (selectedExamination != null)
            {
                val skipDialogFragment = ECGSkipFragment()
                skipDialogFragment.arguments = bundleOf("participant" to participant,
                    "contraindications" to getContraindications(),
                    "type" to TYPE_DXA)
                skipDialogFragment.show(fragmentManager!!)
            }
            else
            {
                binding.textViewExamError.visibility = View.VISIBLE
            }

            return false
        }

        return true
    }

    private fun getContraindications(): MutableList<Map<String, String>>
    {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadXray = viewModel.hadXray.value

        if (!hadXray!!)
        {
            var xrayMap = mutableMapOf<String, String>()
            xrayMap["id"] = "DCI1"
            xrayMap["question"] = getString(R.string.dxa_xray_question)
            xrayMap["answer"] = if (hadXray!!) "yes" else "no"

            contraindications.add(xrayMap)

            return contraindications
        }
        else
        {
            var xrayMap = mutableMapOf<String, String>()
            xrayMap["id"] = "DCI1"
            xrayMap["question"] = getString(R.string.dxa_xray_question)
            xrayMap["answer"] = if (hadXray!!) "yes" else "no"
            xrayMap["examination"] = selectedExamination!!

            contraindications.add(xrayMap)

            return contraindications
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
