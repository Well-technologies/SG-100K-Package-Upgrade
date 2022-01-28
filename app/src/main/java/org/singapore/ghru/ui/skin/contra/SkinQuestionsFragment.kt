package org.singapore.ghru.ui.skin.contra

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
import org.singapore.ghru.databinding.SkinContraFragmentBinding
import org.singapore.ghru.ui.skin.reason.ReasonDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class SkinQuestionsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<SkinContraFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: SkinQuestionsViewModel

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
        val dataBinding = DataBindingUtil.inflate<SkinContraFragmentBinding>(
            inflater,
            R.layout.skin_contra_fragment,
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
            ViewModelProviders.of(this).get(SkinQuestionsViewModel::class.java)
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
                navController().navigate(R.id.action_contra_readingFragment, bundle)
            }
        }

        binding.radioGroupSkinLesson.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noSkinLesson) {
                binding.radioGroupSkinLessonValue = false
                viewModel.setHaveSkinLesson("no")

            } else if (radioGroup.checkedRadioButtonId == R.id.leftSkinLesson) {
                binding.radioGroupSkinLessonValue = false
                viewModel.setHaveSkinLesson("left")

            } else if (radioGroup.checkedRadioButtonId == R.id.rightSkinLesson) {
                binding.radioGroupSkinLessonValue = false
                viewModel.setHaveSkinLesson("right")

            } else {
                binding.radioGroupSkinLessonValue = false
                viewModel.setHaveSkinLesson("both")
            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.hadSkinLesson.value == null)
        {
            binding.radioGroupSkinLessonValue = true
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

        val hadSkinLesson = viewModel.hadSkinLesson.value

        var skinMap = mutableMapOf<String, String>()
        skinMap["id"] = getString(R.string.skin_contra_id)
        skinMap["question"] = getString(R.string.skin_contra_question)
        skinMap["answer"] = hadSkinLesson!!

        contraindications.add(skinMap)

        return contraindications
    }

    private fun validateContra(): Boolean
    {
        val hadSkinLesson = viewModel.hadSkinLesson.value

        if (hadSkinLesson != "both")
        {
            return true
        }

        return false
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
