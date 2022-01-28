package org.singapore.ghru.ui.checkout.paymentinformation.paymentreview

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.CognitionContraindicationFragmentBinding
import org.singapore.ghru.databinding.FoodFrqContraindicationFragmentBinding
import org.singapore.ghru.databinding.PaymentInformationFragmentBinding
import org.singapore.ghru.databinding.PaymentReviewFragmentBinding
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.StudyList
import java.util.ArrayList
import javax.inject.Inject

class PaymentReviewFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<PaymentReviewFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: PaymentReviewViewModel

    private var participantRequest: ParticipantRequest? = null
    private var stList: StudyList? = null
    //var meta: Meta? = null

    private var isHelliosChecked : Boolean? = null
    private var isAbcChecked : Boolean? = null
    private var isXyzChecked : Boolean? = null
    private var is123Checked : Boolean? = null
    private var amount : String? = null
    private var dollar : String? = null
    private var comment : String? = null
    val studyList: ArrayList<Boolean> = arrayListOf()

    private var helliosValue : String? = "No"
    private var abcValue : String? = "No"
    private var xyzValue : String? = "No"
    private var oneTwoValue : String? = "No"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            //meta = arguments?.getParcelable<Meta>("Meta")!!
            isHelliosChecked = arguments?.getBoolean("Hellios")!!
            isAbcChecked = arguments?.getBoolean("Abc")!!
            isXyzChecked = arguments?.getBoolean("Xyz")!!
            is123Checked = arguments?.getBoolean("123")!!
            amount = arguments?.getString("Amount")!!
            dollar = arguments?.getString("Dollar")!!
            comment = arguments?.getString("Comment")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<PaymentReviewFragmentBinding>(
            inflater,
            R.layout.payment_review_fragment,
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
            ViewModelProviders.of(this).get(PaymentReviewViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //binding.participant = participantRequest

        binding.setLifecycleOwner(this)

        binding.giroButton.singleClick {
            if (validateNextButton()) {
                stList  = StudyList(
                    study_hellios = helliosValue!!
//                    study_abc = abcValue!!,
//                    study_xyz = xyzValue!!,
//                    study_123 = oneTwoValue!!
                )
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                bundle.putParcelable("Study_list", stList)
                bundle.putString("Amount", amount!!)
                if (comment != null)
                {
                    bundle.putString("Comment", comment)
                }
                else
                {
                    bundle.putString("Comment", "NA")
                }

                navController().navigate(R.id.action_checkoutPaymentReviewFragment_to_checkoutGiroConfirmationFragment, bundle)
            }
        }

        binding.voucherButton.singleClick {
            if (validateNextButton()) {
                val stList  = StudyList(
                    study_hellios = helliosValue!!
//                    study_abc = abcValue!!,
//                    study_xyz = xyzValue!!,
//                    study_123 = oneTwoValue!!
                )
                val bundle = bundleOf("ParticipantRequest" to participantRequest)
                bundle.putParcelable("Study_list", stList)
                bundle.putString("Amount", amount!!)
                if (comment != null)
                {
                    bundle.putString("Comment", comment)
                }
                else
                {
                    bundle.putString("Comment", "NA")
                }
                navController().navigate(R.id.action_checkoutPaymentReviewFragment_to_checkoutVoucherDetailsFragment, bundle)
            }
        }

        binding.backButton.singleClick {

            navController().popBackStack()
        }

        if (isHelliosChecked!!)
        {
            binding.heliosCheck.isChecked = true
            helliosValue = "Yes"
        }

        if (isAbcChecked!!)
        {
            binding.abcCheck.isChecked = true
            abcValue = "Yes"
        }

        if (isXyzChecked!!)
        {
            binding.xyzCheck.isChecked = true
            xyzValue = "Yes"
        }

        if (is123Checked!!)
        {
            binding.oneTwoThreeCheck.isChecked = true
            oneTwoValue = "Yes"
        }

        studyList.add(isHelliosChecked!!)
        studyList.add(isAbcChecked!!)
        studyList.add(isXyzChecked!!)
        studyList.add(is123Checked!!)

        if (amount != null)
        {
            binding.toBePaidTextView.setText(amount)
        }

        if (dollar != null)
        {
            binding.sgDollarTextView.setText(dollar)
        }
    }

    private fun validateNextButton():Boolean
    {
        if (studyList.size == 0)
        {
            return false
        }

        if (binding.toBePaidTextView.text.toString().equals(""))
        {
            return false
        }

        return true
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

}
