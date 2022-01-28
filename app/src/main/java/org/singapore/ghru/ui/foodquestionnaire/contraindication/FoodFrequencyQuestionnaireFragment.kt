package org.singapore.ghru.ui.foodquestionnaire.contraindication

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
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
import org.singapore.ghru.databinding.FoodFrqContraindicationFragmentBinding
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class FoodFrequencyQuestionnaireFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FoodFrqContraindicationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: FoodFrequencyQuestionnaireViewModel

    private var participantRequest: ParticipantRequest? = null
    private var station_status : Boolean? = null
    private var status_code : Int? = null
    private var is_Cancelled : Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        try {
            station_status = arguments?.getBoolean("StationStatus")!!
            status_code = arguments?.getInt("StatusCode")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        try {
            is_Cancelled = arguments?.getBoolean("is_Cancelled")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FoodFrqContraindicationFragmentBinding>(
            inflater,
            R.layout.food_frq_contraindication_fragment,
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
            ViewModelProviders.of(this).get(FoodFrequencyQuestionnaireViewModel::class.java)
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

                Log.d("FFQ_CONTRA" ,"DATA: STATION_STATUS" + station_status + ", STATUS_CODE " + status_code)

                if (station_status != null)
                {
                    if (station_status!!)
                    {
                        if (is_Cancelled!!)
                        {
                            //navController().navigate(R.id.action_contraFragment_to_ffqLanguageFragment, bundle)
                        }
                        else
                        {
                            if (status_code != null)
                            {
                                if (status_code == 10)
                                {
                                    navController().navigate(R.id.action_global_GuideFragment, bundle)
                                }
                                else if (status_code == 100)
                                {
                                    //navController().navigate(R.id.action_contraFragment_to_ffqLanguageFragment, bundle)
                                }
                            }
                        }
                    }
                    else
                    {
                        //navController().navigate(R.id.action_contraFragment_to_ffqLanguageFragment, bundle)
                    }
                }
            }
        }

        binding.radioGroupCommunicate.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noCommunicate) {
                binding.radioGroupCommunicateValue = false
                viewModel.setHaveCommunicate(false)

            } else {
                binding.radioGroupCommunicateValue = false
                viewModel.setHaveCommunicate(true)
            }
            binding.executePendingBindings()
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveCommunicate.value == null) {
            binding.radioGroupCommunicateValue = true
            binding.executePendingBindings()
            return false
        }

        if(viewModel.haveCommunicate.value ==  false) {
            val skipDialogFragment = FoodFrequencySkipFragment()
            skipDialogFragment.arguments = bundleOf("participant" to participantRequest,
                "contraindications" to getContraindications(),
                "type" to TYPE_FOOD_FREQUENCY)
            skipDialogFragment.show(fragmentManager!!)

            return false
        }

        return true
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

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications = mutableListOf<Map<String, String>>()

        val haveCommunicate = viewModel.haveCommunicate.value
        val communicationMap = mutableMapOf<String, String>()
        communicationMap["id"] = "FFCI1"
        communicationMap["question"] = getString(R.string.ffq_question)
        communicationMap["answer"] = if (haveCommunicate!!) "yes" else "no"

        contraindications.add(communicationMap)

        return contraindications
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

}
