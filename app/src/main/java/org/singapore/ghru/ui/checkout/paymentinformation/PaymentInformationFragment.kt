package org.singapore.ghru.ui.checkout.paymentinformation

import android.graphics.Color
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.payment_information_fragment.*

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.CognitionContraindicationFragmentBinding
import org.singapore.ghru.databinding.FoodFrqContraindicationFragmentBinding
import org.singapore.ghru.databinding.PaymentInformationFragmentBinding
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.request.ParticipantRequest
import java.util.ArrayList
import javax.inject.Inject

class PaymentInformationFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<PaymentInformationFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: PaymentInformationViewModel

    private var participantRequest: ParticipantRequest? = null
    //var meta: Meta? = null
//    val studyList: ArrayList<Boolean> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            //meta = arguments?.getParcelable<Meta>("Meta")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<PaymentInformationFragmentBinding>(
            inflater,
            R.layout.payment_information_fragment,
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
            ViewModelProviders.of(this).get(PaymentInformationViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //binding.participant = participantRequest

        binding.setLifecycleOwner(this)

        binding.reviewButton.singleClick {
            if (validateNextButton()) {
                if (!validateDollarText())
                {
                    binding.dollarTextLayout.error = getString(R.string.error_invalid_input)
                    Toast.makeText(activity, "Invalid input for dollars", Toast.LENGTH_LONG).show()
                }
                else
                {
                    val bundle = bundleOf("ParticipantRequest" to participantRequest)
                    if (viewModel.haveHellios.value !=null)
                    {
                        bundle.putBoolean("Hellios", viewModel.haveHellios.value!!)
                    }
                    else
                    {
                        bundle.putBoolean("Hellios", false)
                    }

                    if (viewModel.haveAbc.value !=null)
                    {
                        bundle.putBoolean("Abc", viewModel.haveAbc.value!!)
                    }
                    else
                    {
                        bundle.putBoolean("Abc", false)
                    }

                    if (viewModel.haveXyz.value !=null)
                    {
                        bundle.putBoolean("Xyz", viewModel.haveXyz.value!!)
                    }
                    else
                    {
                        bundle.putBoolean("Xyz", false)
                    }

                    if (viewModel.have123.value !=null)
                    {
                        bundle.putBoolean("123", viewModel.have123.value!!)
                    }
                    else
                    {
                        bundle.putBoolean("123", false)
                    }

                    bundle.putString("Amount", binding.toBePaidTextView.text.toString())
                    bundle.putString("Dollar", binding.sgDollarEditText.text.toString())
                    bundle.putString("Comment", binding.comment.text.toString())
                    navController().navigate(R.id.action_checkoutPaymentInformationFragment_to_checkoutPaymentReviewFragment, bundle)
                }
            }
            else
            {
                Toast.makeText(activity, "Please take at least one study method", Toast.LENGTH_LONG).show()
            }
        }

        binding.backButton.singleClick {

            navController().popBackStack()
        }

        binding.heliosCheck.setOnCheckedChangeListener { _, isChecked ->

            viewModel.setHaveHellios(isChecked)
            binding.toBePaidTextView.text = getAmount()
            //studyList.add(viewModel.haveHellios.value!!)
        }

        binding.abcCheck.setOnCheckedChangeListener { _, isChecked ->

            viewModel.setHaveAbc(isChecked)
            binding.toBePaidTextView.text = getAmount()
            //studyList.add(viewModel.haveAbc.value!!)
        }

        binding.xyzCheck.setOnCheckedChangeListener { _, isChecked ->

            viewModel.setHaveXyz(isChecked)
            binding.toBePaidTextView.text = getAmount()
            //studyList.add(viewModel.haveXyz.value!!)
        }

        binding.oneTwoThreeCheck.setOnCheckedChangeListener { _, isChecked ->

            viewModel.setHave123(isChecked)
            binding.toBePaidTextView.text = getAmount()
            //studyList.add(viewModel.have123.value!!)
        }

        onTextChanges(binding.sgDollarEditText)
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.sgDollarEditText) {

                    validateDollarText()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun validateNextButton(): Boolean {
        if((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            return false
        }

        if (binding.toBePaidTextView.text.toString().equals(""))
        {

            return false
        }

        return true
    }

    private fun validateDollarText():Boolean
    {
        if (binding.sgDollarEditText.text.toString().equals(""))
        {
            binding.dollarTextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

        return true
    }

    private fun getAmount():String{

        var amt: String = ""

        if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$0"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            viewModel.have123.value == true)
        {
            amt = "$50"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            viewModel.haveXyz.value == true &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$50"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            viewModel.haveXyz.value == true &&
            viewModel.have123.value == true)
        {
            amt = "$100"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            viewModel.haveAbc.value == true &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$50"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            viewModel.haveAbc.value == true &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            viewModel.have123.value == true)
        {
            amt = "$100"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            viewModel.haveAbc.value == true &&
            viewModel.haveXyz.value == true &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$100"
        }
        else if ((viewModel.haveHellios.value == false || viewModel.haveHellios.value == null) &&
            viewModel.haveAbc.value == true &&
            viewModel.haveXyz.value == true &&
            viewModel.have123.value == true)
        {
            amt = "$150"
        }
        else if (viewModel.haveHellios.value == true &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$50"
        }
        else if (viewModel.haveHellios.value == true &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            viewModel.have123.value == true)
        {
            amt = "$100"
        }
        else if (viewModel.haveHellios.value == true &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            viewModel.haveXyz.value == true &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$100"
        }
        else if (viewModel.haveHellios.value == true &&
            (viewModel.haveAbc.value == false || viewModel.haveAbc.value == null) &&
            viewModel.haveXyz.value == true &&
            viewModel.have123.value == true)
        {
            amt = "$150"
        }
        else if (viewModel.haveHellios.value == true &&
            viewModel.haveAbc.value == true &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$100"
        }
        else if (viewModel.haveHellios.value == true &&
            viewModel.haveAbc.value == true &&
            (viewModel.haveXyz.value == false || viewModel.haveXyz.value == null) &&
            viewModel.have123.value == true)
        {
            amt = "$150"
        }
        else if (viewModel.haveHellios.value == true &&
            viewModel.haveAbc.value == true &&
            viewModel.haveXyz.value == true &&
            (viewModel.have123.value == false || viewModel.have123.value == null))
        {
            amt = "$150"
        }
        else if (viewModel.haveHellios.value == true &&
            viewModel.haveAbc.value == true &&
            viewModel.haveXyz.value == true &&
            viewModel.have123.value == true)
        {
            amt = "$200"
        }

        return amt

    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                binding.root.hideKeyboard()
//                navController().popBackStack()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
    }

}
