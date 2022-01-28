package org.singapore.ghru.ui.visualacuity.visualaid

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
import org.singapore.ghru.databinding.HeightWeightContraindicationFragmentBinding
import org.singapore.ghru.databinding.HipWaistContraindicationFragmentBinding
import org.singapore.ghru.databinding.VisualAcuityAidFragmentBinding
import org.singapore.ghru.databinding.VisualAcuityContraFragmentBinding
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class VisualAcuityAidFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<VisualAcuityAidFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: VisualAcuityAidViewModel

    private var participantRequest: ParticipantRequest? = null

    private var visualAidValue: String? = null

//    private var isImageExported: Boolean? = false

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
        val dataBinding = DataBindingUtil.inflate<VisualAcuityAidFragmentBinding>(
            inflater,
            R.layout.visual_acuity_aid_fragment,
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
            ViewModelProviders.of(this).get(VisualAcuityAidViewModel::class.java)
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
                bundle.putString("visualAid", visualAidValue)
                println(visualAidValue)
                println(binding.radioGroupVisualAidValue)
                navController().navigate(R.id.action_contraFragment_to_VisualAcuityHomeFragment, bundle)
            }
        }

        binding.radioGroupVisualAid.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.corrective_glasses)
            {
                binding.radioGroupVisualAidValue = false
                visualAidValue = "Corrective Glasses"
                viewModel.setVisualAid(true)

            }
            else if (radioGroup.checkedRadioButtonId == R.id.contact_lenses)
            {
                binding.radioGroupVisualAidValue = false
                visualAidValue = "Contact Lenses"
                viewModel.setVisualAid(true)

            }
            else if (radioGroup.checkedRadioButtonId == R.id.nil)
            {
                binding.radioGroupVisualAidValue = false
                visualAidValue = "NIL"
                viewModel.setVisualAid(true)
            }
            else
            {
                binding.radioGroupVisualAidValue = true
                viewModel.setVisualAid(false)
            }

            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveVisualAid.value == null) {
            binding.radioGroupVisualAidValue = true
            binding.executePendingBindings()
            return false
        }
            return true
//        else if (viewModel.haveImageExported.value == null)
//        {
//            binding.radioGroupImageExportValue = true
//            binding.executePendingBindings()
//            return false
//        }

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

}
