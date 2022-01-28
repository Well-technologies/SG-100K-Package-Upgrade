package org.singapore.ghru.ui.ecg.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.InputFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class InputFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<InputFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var inputViewModel: InputViewModel


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
        val dataBinding = DataBindingUtil.inflate<InputFragmentBinding>(
            inflater,
            R.layout.input_fragment,
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.participant = participant
        binding.previousButton.singleClick {
            navController().popBackStack()
        }

        binding.nextButton.singleClick {
            if (validateNextButton()) {
                val bundle = bundleOf("participant" to participant)
                navController().navigate(R.id.action_inputFragment_to_traceFragment, bundle)
            }
        }

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->

            inputViewModel.setHasExplained(isChecked)
            validateNextButton()
        }
    }

    private fun validateNextButton(): Boolean {

        if (inputViewModel.hasGivenConsent.value != null && inputViewModel.hasGivenConsent.value!!) {
            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check, null)
            binding.textViewError.visibility = View.GONE
            return true
        } else {
            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check_error, null)
            binding.textViewError.visibility = View.VISIBLE
            return false

        }
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
