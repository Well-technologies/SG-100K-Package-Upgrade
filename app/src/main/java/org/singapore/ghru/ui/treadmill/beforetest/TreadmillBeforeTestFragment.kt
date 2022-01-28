package org.singapore.ghru.ui.treadmill.beforetest

import android.graphics.Color
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
import org.singapore.ghru.databinding.TreadmillBeforeTestFragmentBinding
import org.singapore.ghru.databinding.TreadmillContraFragmentBinding
import org.singapore.ghru.databinding.UltrasoundContraFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.ecg.questions.TYPE_ULTRASOUND
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.setDrawableRightColor
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class TreadmillBeforeTestFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TreadmillBeforeTestFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var viewModel: TreadmillBeforeTestViewModel

    private var participant: ParticipantRequest? = null
    private var isAbleToNext: Boolean = false

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
        val dataBinding = DataBindingUtil.inflate<TreadmillBeforeTestFragmentBinding>(
            inflater,
            R.layout.treadmill_before_test_fragment,
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
            ViewModelProviders.of(this).get(TreadmillBeforeTestViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        setNextButton()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.participant = participant
        binding.viewModel = viewModel

        setNextButton()

        binding.previousButton.singleClick {
            navController().popBackStack()
            isAbleToNext = false
        }

        binding.nextButton.singleClick {
            if (validateNextButton()) {
                val bundle = Bundle()
                bundle.putParcelable("participant", participant)
                navController().navigate(R.id.action_beforeTestFragment_to_mainFragment, bundle)
            }
        }

        binding.radioGroupArm.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.leftArm) {
                binding.radioGroupArmValue = false
                viewModel.setArmPlacement("left")
                isAbleToNext = true

            } else if (radioGroup.checkedRadioButtonId == R.id.rightArm) {
                binding.radioGroupArmValue = false
                viewModel.setArmPlacement("right")
                isAbleToNext = true

            }
//            else {
//                binding.radioGroupArmValue = false
//                viewModel.setArmPlacement("N/A")
//                isAbleToNext = true
//
//            }
            setNextButton()
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.armPlacement.value == null) {
            binding.radioGroupArmValue = true
            binding.executePendingBindings()
            return false
        }

        return true
    }

    private fun setNextButton() {
        if (isAbleToNext) {
            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.nextButton.setDrawableRightColor("#0A1D53")
            binding.nextButton.isEnabled = true
        } else {
            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"))
            binding.nextButton.setDrawableRightColor("#AED6F1")
            binding.nextButton.isEnabled = false
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
