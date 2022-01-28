package org.singapore.ghru.ui.visualacuity.lefteye

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.LeftEyeFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.*
import org.singapore.ghru.ui.visualacuity.reason.ReasonDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import javax.inject.Inject

class LeftEyeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager

    var binding by autoCleared<LeftEyeFragmentBinding>()


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val disposables = CompositeDisposable()

    private lateinit var validator: Validator

    @Inject
    lateinit var viewModel: LeftEyeViewModel

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    private var participantRequest: ParticipantRequest? = null
    private var leftEye: VisualAcuityData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            leftEye = arguments?.getParcelable<VisualAcuityData>("LeftEye")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<LeftEyeFragmentBinding>(
            inflater,
            R.layout.left_eye_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        validator = Validator(binding)

        binding.root.hideKeyboard()
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        binding.measurement = leftEye

        val filter = object : InputFilter {
            val maxDigitsBeforeDecimalPoint = 10
            val maxDigitsAfterDecimalPoint = 1

            override fun filter(
                source: CharSequence, start: Int, end: Int,
                dest: Spanned, dstart: Int, dend: Int
            ): CharSequence? {
                val builder = StringBuilder(dest)
                builder.replace(
                    dstart, dend, source
                        .subSequence(start, end).toString()
                )
                return if (!builder.toString().matches(("(([1-9]{1})([0-9]{0," + (maxDigitsBeforeDecimalPoint - 1) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimalPoint + "})?").toRegex())) {
                    if (source.length == 0) dest.subSequence(dstart, dend) else ""
                } else null

            }
        }

        binding.leftR1EditText.filters = arrayOf<InputFilter>(filter)
        binding.leftR2EditText.filters = arrayOf<InputFilter>(filter)
        binding.leftR3EditText.filters = arrayOf<InputFilter>(filter)

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.VISUAL_ACUITY)
        viewModel.stationDeviceList?.observe(this, Observer {
            if (it.status.equals(Status.SUCCESS)) {
                deviceListObject = it.data!!

                deviceListObject.iterator().forEach {
                    deviceListName.add(it.device_name!!)
                }
                adapter.notifyDataSetChanged()
            }
        })
        binding.deviceIdSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>, @NonNull selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    selectedDeviceID = null
                } else {
                    binding.textViewDeviceError.visibility = View.GONE
                    selectedDeviceID = deviceListObject[position - 1].device_id
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        binding.buttonSubmit.singleClick {
//            if(selectedDeviceID==null)
//            {
//                binding.textViewDeviceError.visibility = View.VISIBLE
//            }
//            else
            if (validateHipReading1(binding.leftR1EditText.text.toString()) && validateHipReading2(binding.leftR2EditText.text.toString()) && validateHipReading3(binding.leftR3EditText.text.toString()))
            {
                val record = VisualAcuityData(
                    comment = binding.comment.text.toString(),
                    device_id = selectedDeviceID,
                    letter_score = binding.leftR1EditText.text.toString(),
                    acuity_score = binding.leftR2EditText.text.toString(),
                    logmar_score = binding.leftR3EditText.text.toString()
                    )

                Log.d("HIP_FRAGMENT","DATA: " + record)

                LeftEyeRecordTestRxBus.getInstance().post(record)
                binding.root.hideKeyboard()
                navController().popBackStack()
            }
        }

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

        onTextChanges(binding.leftR1EditText)
        onTextChanges(binding.leftR2EditText)
        onTextChanges(binding.leftR3EditText)
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.leftR1EditText) {
                    validateHipReading1(binding.leftR1EditText.text.toString())
                }

                if(editText == binding.leftR2EditText) {
                    validateHipReading2(binding.leftR2EditText.text.toString())
                }

                if(editText == binding.leftR3EditText) {
                    validateHipReading3(binding.leftR3EditText.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun validateHipReading1(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_GRIP_MIN_VAL) {
                binding.leftR1TextLayout.error = null
                viewModel.isValidLeftEye = false
                return true

            } else {
                viewModel.isValidLeftEye = true
                binding.leftR1TextLayout.error = getString(R.string.error_not_in_range)
                return false
            }

            // validateNextButton()

        } catch (e: Exception) {
            binding.leftR1TextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

    private fun validateHipReading2(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_GRIP_MIN_VAL) {
                binding.leftR2TextLayout.error = null
                viewModel.isValidLeftEye = false
                return true

            } else {
                viewModel.isValidLeftEye = true
                binding.leftR2TextLayout.error = getString(R.string.error_not_in_range)
                return false
            }

            // validateNextButton()

        } catch (e: Exception) {
            binding.leftR2TextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

    private fun validateHipReading3(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_GRIP_MIN_VAL) {
                binding.leftR3TextLayout.error = null
                viewModel.isValidLeftEye = false
                return true

            } else {
                viewModel.isValidLeftEye = true
                binding.leftR3TextLayout.error = getString(R.string.error_not_in_range)
                return false
            }

            // validateNextButton()

        } catch (e: Exception) {
            binding.leftR3TextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_skip -> {
//                val reasonDialogFragment = ReasonDialogFragment()
//                reasonDialogFragment.show(fragmentManager!!)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater?.inflate(R.menu.bp_main, menu)
//        checkConnection(menu!!)
//    }

    private fun checkConnection(menu: Menu) {
//        val isConnected = ConnectivityReceiver.isConnected(context)
//        if (isConnected) {
//            menu.findItem(R.id.menu_text).setTitleColor(Color.WHITE)
//            menu.findItem(R.id.menu_text).setTitle("Online (Local)")
//            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_local_lan)
//        } else {
//            menu.findItem(R.id.menu_text).setTitleColor(Color.RED)
//            menu.findItem(R.id.menu_text).setTitle("Offline")
//            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_wifi_disconnected)
//        }
//        activity!!.invalidateOptionsMenu();
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
