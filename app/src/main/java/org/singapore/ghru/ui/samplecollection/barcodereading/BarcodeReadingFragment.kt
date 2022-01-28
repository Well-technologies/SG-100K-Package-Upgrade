package org.singapore.ghru.ui.samplecollection.barcodereading

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.google.android.material.textfield.TextInputLayout
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.BagScannedFragmentBinding
import org.singapore.ghru.databinding.BarcodeReadingFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Comment
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.User
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.SampleCreateRequest
import org.singapore.ghru.vo.request.SampleRequest
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BarcodeReadingFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<BarcodeReadingFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var participant: ParticipantRequest? = null
    private var sampleId: String? = null

    @Inject
    lateinit var viewModel: BarcodeReadingViewModel
    @Inject
    lateinit var jobManager: JobManager
    private var isPartial : Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            //sampleId = arguments?.getString("sample_id")!!

        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<BarcodeReadingFragmentBinding>(
            inflater,
            R.layout.barcode_reading_fragment,
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


    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.expand = true
        binding.participant = participant

        Log.d("READING_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        binding.buttonNext.singleClick {

            if (validateNextButton()) {

                Log.d("READING_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val bundle = bundleOf("participant" to participant)
//                findNavController().navigate(R.id.action_readingFragment_to_bagScannedFragment, bundle)
            }
            else
            {
                Toast.makeText(activity!!, "Please enter all fields", Toast.LENGTH_LONG).show()
            }
        }

        onTextChanges(binding.textInputEditText1, binding.textInputEditText2, binding.textInputLayout1)
        onTextChanges(binding.textInputEditText2, binding.textInputEditText3, binding.textInputLayout2)
        onTextChanges(binding.textInputEditText3, binding.textInputEditText4, binding.textInputLayout3)
        onTextChanges(binding.textInputEditText4, binding.textInputEditText5, binding.textInputLayout4)
        onTextChanges(binding.textInputEditText5, binding.textInputEditText6, binding.textInputLayout5)
        onTextChanges(binding.textInputEditText6, binding.textInputEditText7, binding.textInputLayout6)
        onTextChanges(binding.textInputEditText7, binding.textInputEditText8, binding.textInputLayout7)
        onTextChanges(binding.textInputEditText8, binding.textInputEditText9, binding.textInputLayout8)
        onTextChanges(binding.textInputEditText9, binding.textInputEditText10, binding.textInputLayout9)
        onTextChanges(binding.textInputEditText10, binding.textInputEditText11, binding.textInputLayout10)
        onTextChanges(binding.textInputEditText11, binding.textInputEditText12, binding.textInputLayout11)
        onTextChanges(binding.textInputEditText12, binding.textInputEditText13, binding.textInputLayout12)
        onTextChanges(binding.textInputEditText13, binding.textInputEditText14, binding.textInputLayout13)
        onTextChanges(binding.textInputEditText14, binding.textInputEditText15, binding.textInputLayout14)
        onTextChanges(binding.textInputEditText15, binding.textInputEditText16, binding.textInputLayout15)
        onTextChanges(binding.textInputEditText16, binding.textInputEditText17, binding.textInputLayout16)
    }

    private fun onTextChanges(editText1: EditText, editText2: EditText, textInputLayout1: TextInputLayout) {
        editText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                validateReading(editText1, editText2, textInputLayout1)
            }
        })
    }

    private fun validateReading(editText1: EditText, editText2: EditText, textInputLayout1: TextInputLayout){

        try {
            if (editText1.text.toString().length == 12)
            {
                textInputLayout1.error = null
                editText2.requestFocus()
            }
            else {
                textInputLayout1.error = getString(R.string.error_invalid_input)
            }

        } catch (e: Exception) {
            textInputLayout1.error = getString(R.string.error_invalid_input)
        }

    }

    private fun validateNextButton() : Boolean
    {
        if (binding.textInputEditText1.text!!.length == 12
            && binding.textInputEditText2.text!!.length == 12
            && binding.textInputEditText3.text!!.length == 12
            && binding.textInputEditText4.text!!.length == 12
            && binding.textInputEditText5.text!!.length == 12
            && binding.textInputEditText6.text!!.length == 12
            && binding.textInputEditText7.text!!.length == 12
            && binding.textInputEditText8.text!!.length == 12
            && binding.textInputEditText9.text!!.length == 12
            && binding.textInputEditText10.text!!.length == 12
            && binding.textInputEditText11.text!!.length == 12
            && binding.textInputEditText12.text!!.length == 12
            && binding.textInputEditText13.text!!.length == 12
            && binding.textInputEditText14.text!!.length == 12
            && binding.textInputEditText15.text!!.length == 12
            && binding.textInputEditText16.text!!.length == 12
            && binding.textInputEditText17.text!!.length == 12)
        {
            isPartial = false
            return true

        }
        else if (binding.textInputEditText1.text!!.length == 12
            || binding.textInputEditText2.text!!.length == 12
            || binding.textInputEditText3.text!!.length == 12
            || binding.textInputEditText4.text!!.length == 12
            || binding.textInputEditText5.text!!.length == 12
            || binding.textInputEditText6.text!!.length == 12
            || binding.textInputEditText7.text!!.length == 12
            || binding.textInputEditText8.text!!.length == 12
            || binding.textInputEditText9.text!!.length == 12
            || binding.textInputEditText10.text!!.length == 12
            || binding.textInputEditText11.text!!.length == 12
            || binding.textInputEditText12.text!!.length == 12
            || binding.textInputEditText13.text!!.length == 12
            || binding.textInputEditText14.text!!.length == 12
            || binding.textInputEditText15.text!!.length == 12
            || binding.textInputEditText16.text!!.length == 12
            || binding.textInputEditText17.text!!.length == 12)
        {
            isPartial = true
            return true
        }

        isPartial = null
        return false
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
