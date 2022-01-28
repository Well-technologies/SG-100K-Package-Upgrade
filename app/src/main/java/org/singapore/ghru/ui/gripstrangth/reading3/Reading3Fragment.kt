package org.singapore.ghru.ui.gripstrangth.reading3


import android.graphics.Color
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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.BuildConfig
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.GripReading3FragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.*
import org.singapore.ghru.network.ConnectivityReceiver
import org.singapore.ghru.ui.gripstrangth.contraindication.GripStrengthQuestionnaireViewModel
import org.singapore.ghru.ui.gripstrangth.reading3.missingvalue.ReadingThreeMissingDialogFragment
import org.singapore.ghru.ui.hipwaist.reason.ReasonDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import javax.inject.Inject


class Reading3Fragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager


    var binding by autoCleared<GripReading3FragmentBinding>()


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val disposables = CompositeDisposable()

    private lateinit var validator: Validator

    @Inject
    lateinit var viewModel: Reading3ViewModel

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null

    private var participantRequest: ParticipantRequest? = null
    private var reading3: GripStrengthData? = null
    private var reading_1 : GripStrengthReading? = null
    private var reading_2 : GripStrengthReading? = null

    private lateinit var questionnaireViewModel: GripStrengthQuestionnaireViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            reading3 = arguments?.getParcelable<GripStrengthData>("Reading3")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<GripReading3FragmentBinding>(
            inflater,
            R.layout.grip_reading3_fragment,
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
        binding.measurement = reading3


        if (BuildConfig.DEBUG) {

        }

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

        binding.hipR1EditText.filters = arrayOf<InputFilter>(filter)
        binding.hipR2EditText.filters = arrayOf<InputFilter>(filter)
        binding.hipR3EditText.filters = arrayOf<InputFilter>(filter)

        deviceListName.clear()
        deviceListName.add(getString(R.string.unknown))
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, deviceListName)
        binding.deviceIdSpinner.setAdapter(adapter);

        viewModel.setStationName(Measurements.grip_strength)
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

            if (validateGripReading1(binding.hipR1EditText.text.toString()) && validateGripReading2(binding.hipR2EditText.text.toString()))
            {
                reading_1 = GripStrengthReading(value = binding.hipR1EditText.text.toString(), unit = "kg")
                reading_2 = GripStrengthReading(value = binding.hipR2EditText.text.toString(), unit = "kg")

                val record = GripStrengthData(
                    left_grip = reading_1,
                    right_grip = reading_2)

                Reading3RecordTestRxBus.getInstance().post(record)
                binding.root.hideKeyboard()
                navController().popBackStack()
            }
            else if (validateGripReading1(binding.hipR1EditText.text.toString()) && !validateGripReading2(binding.hipR2EditText.text.toString()))
            {
                reading_1 = GripStrengthReading(value = binding.hipR1EditText.text.toString(), unit = "kg")
                reading_2 = GripStrengthReading(value = "NA", unit = "kg")

                val record = GripStrengthData(
                    left_grip = reading_1,
                    right_grip = reading_2)

                if (!isRightHandOk() || !isLeftHandOk())
                {
                    Reading3RecordTestRxBus.getInstance().post(record)
                    binding.root.hideKeyboard()
                    navController().popBackStack()
                }
                else
                {
                    val missingDialogFragment = ReadingThreeMissingDialogFragment()
                    missingDialogFragment.arguments = bundleOf(
                        "readingThreeData" to record)
                    missingDialogFragment.show(fragmentManager!!)
                }

//                val missingDialogFragment = ReadingThreeMissingDialogFragment()
//                missingDialogFragment.arguments = bundleOf(
//                    "readingThreeData" to record)
//                missingDialogFragment.show(fragmentManager!!)
            }
            else if (!validateGripReading1(binding.hipR1EditText.text.toString()) && validateGripReading2(binding.hipR2EditText.text.toString()))
            {
                reading_1 = GripStrengthReading(value = "NA", unit = "kg")
                reading_2 = GripStrengthReading(value = binding.hipR2EditText.text.toString(), unit = "kg")

                val record = GripStrengthData(
                    left_grip = reading_1,
                    right_grip = reading_2)

                if (!isRightHandOk() || !isLeftHandOk())
                {
                    Reading3RecordTestRxBus.getInstance().post(record)
                    binding.root.hideKeyboard()
                    navController().popBackStack()
                }
                else
                {
                    val missingDialogFragment = ReadingThreeMissingDialogFragment()
                    missingDialogFragment.arguments = bundleOf(
                        "readingThreeData" to record)
                    missingDialogFragment.show(fragmentManager!!)
                }

//                val missingDialogFragment = ReadingThreeMissingDialogFragment()
//                missingDialogFragment.arguments = bundleOf(
//                    "readingThreeData" to record)
//                missingDialogFragment.show(fragmentManager!!)
            }

//            if (isLeftHandOk() && isRightHandOk())
//            {
//                if (validateGripReading1(binding.hipR1EditText.text.toString()) && validateGripReading2(binding.hipR2EditText.text.toString()))
//                {
//                    reading_1 = GripStrengthReading(value = binding.hipR1EditText.text.toString(), unit = "kg")
//                    reading_2 = GripStrengthReading(value = binding.hipR2EditText.text.toString(), unit = "kg")
//
//                    val record = GripStrengthData(
//                        left_grip = reading_1,
//                        right_grip = reading_2)
//
//                    Reading3RecordTestRxBus.getInstance().post(record)
//                    binding.root.hideKeyboard()
//                    navController().popBackStack()
//                }
//            }
//            else if (isLeftHandOk() && !isRightHandOk())
//            {
//                if (validateGripReading1(binding.hipR1EditText.text.toString()))
//                {
//                    reading_1 = GripStrengthReading(value = binding.hipR1EditText.text.toString(), unit = "kg")
//                    reading_2 = GripStrengthReading(value = "NA", unit = "kg")
//
//                    val record = GripStrengthData(
//                        left_grip = reading_1,
//                        right_grip = reading_2)
//
//                    val missingDialogFragment = ReadingThreeMissingDialogFragment()
//                    missingDialogFragment.arguments = bundleOf(
//                        "readingThreeData" to record)
//                    missingDialogFragment.show(fragmentManager!!)
//                }
//            }
//            else if (!isLeftHandOk() && isRightHandOk())
//            {
//                if (validateGripReading2(binding.hipR2EditText.text.toString()))
//                {
//                    reading_1 = GripStrengthReading(value = "NA", unit = "kg")
//                    reading_2 = GripStrengthReading(value = binding.hipR2EditText.text.toString(), unit = "kg")
//
//                    val record = GripStrengthData(
//                        left_grip = reading_1,
//                        right_grip = reading_2)
//
//                    val missingDialogFragment = ReadingThreeMissingDialogFragment()
//                    missingDialogFragment.arguments = bundleOf(
//                        "readingThreeData" to record)
//                    missingDialogFragment.show(fragmentManager!!)
//                }
//            }

//            if (validateGripReading1(binding.hipR1EditText.text.toString()) && validateGripReading2(binding.hipR2EditText.text.toString()))
//            {
//                reading_1 = GripStrengthReading(value = binding.hipR1EditText.text.toString(), unit = "kg")
//                reading_2 = GripStrengthReading(value = binding.hipR2EditText.text.toString(), unit = "kg")
//
//                val record = GripStrengthData(
//                    left_grip = reading_1,
//                    right_grip = reading_2)
//
//                Reading3RecordTestRxBus.getInstance().post(record)
//                binding.root.hideKeyboard()
//                navController().popBackStack()
//            }
//            else if (validateGripReading1(binding.hipR1EditText.text.toString()) && !validateGripReading2(binding.hipR2EditText.text.toString()))
//            {
//                reading_1 = GripStrengthReading(value = binding.hipR1EditText.text.toString(), unit = "kg")
//                reading_2 = GripStrengthReading(value = "NA", unit = "kg")
//
//                val record = GripStrengthData(
//                    left_grip = reading_1,
//                    right_grip = reading_2)
//
//                val missingDialogFragment = ReadingThreeMissingDialogFragment()
//                missingDialogFragment.arguments = bundleOf(
//                    "readingThreeData" to record)
//                missingDialogFragment.show(fragmentManager!!)
//            }
//            else if (!validateGripReading1(binding.hipR1EditText.text.toString()) && validateGripReading2(binding.hipR2EditText.text.toString()))
//            {
//                reading_1 = GripStrengthReading(value = "NA", unit = "kg")
//                reading_2 = GripStrengthReading(value = binding.hipR2EditText.text.toString(), unit = "kg")
//
//                val record = GripStrengthData(
//                    left_grip = reading_1,
//                    right_grip = reading_2)
//
//                val missingDialogFragment = ReadingThreeMissingDialogFragment()
//                missingDialogFragment.arguments = bundleOf(
//                    "readingThreeData" to record)
//                missingDialogFragment.show(fragmentManager!!)
//            }

        }

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

        onTextChanges(binding.hipR1EditText)
        onTextChanges(binding.hipR2EditText)

        if (isLeftHandOk() && isRightHandOk())
        {
            binding.hipR1LinearLayout.visibility = View.VISIBLE
            binding.hipR2LinearLayout.visibility = View.VISIBLE
        }
        else if (isRightHandOk())
        {
            binding.hipR2LinearLayout.visibility = View.VISIBLE
            binding.hipR1LinearLayout.visibility = View.GONE
        }
        else if (isLeftHandOk())
        {
            binding.hipR1LinearLayout.visibility = View.VISIBLE
            binding.hipR2LinearLayout.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(GripStrengthQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.hipR1EditText) {
                    validateGripReading1(binding.hipR1EditText.text.toString())
                }

                if(editText == binding.hipR2EditText) {
                    validateGripReading2(binding.hipR2EditText.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun validateGripReading1(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_GRIP_MIN_VAL) {
                binding.hipR1TextLayout.error = null
                viewModel.isValidHipSize = false
                return true

            } else {
                viewModel.isValidHipSize = true
                //binding.hipR1TextLayout.error = getString(R.string.error_not_in_range)
                return false
            }

            // validateNextButton()

        } catch (e: Exception) {
            binding.hipR1TextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

    private fun validateGripReading2(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_GRIP_MIN_VAL) {
                binding.hipR2TextLayout.error = null
                viewModel.isValidHipSize = false
                return true

            } else {
                viewModel.isValidHipSize = true
                //binding.hipR2TextLayout.error = getString(R.string.error_not_in_range)
                return false
            }

            // validateNextButton()

        } catch (e: Exception) {
            binding.hipR2TextLayout.error = getString(R.string.error_invalid_input)
            return false
        }

    }

    private fun validateGripReading3(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_GRIP_MIN_VAL && fatVal <= Constants.BD_GRIP_MAX_VAL) {
                binding.hipR3TextLayout.error = null
                viewModel.isValidHipSize = false
                return true

            } else {
                viewModel.isValidHipSize = true
                binding.hipR3TextLayout.error = getString(R.string.error_not_in_range)
                return false
            }

            // validateNextButton()

        } catch (e: Exception) {
            binding.hipR3TextLayout.error = getString(R.string.error_invalid_input)
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
        val isConnected = ConnectivityReceiver.isConnected(context)
        if (isConnected) {
            menu.findItem(R.id.menu_text).setTitleColor(Color.WHITE)
            menu.findItem(R.id.menu_text).setTitle("Online (Local)")
            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_local_lan)
        } else {
            menu.findItem(R.id.menu_text).setTitleColor(Color.RED)
            menu.findItem(R.id.menu_text).setTitle("Offline")
            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_wifi_disconnected)
        }
        activity!!.invalidateOptionsMenu();
    }

    private fun isLeftHandOk():Boolean {

        val haveServe = questionnaireViewModel.haveSevere.value
        val haveInjury = questionnaireViewModel.haveInjury.value

        if (haveServe.equals("left"))
        {
            return false
        }
        if (haveInjury.equals("left"))
        {
            return false
        }

        return true
    }

    private fun isRightHandOk():Boolean {

        val haveServe = questionnaireViewModel.haveSevere.value
        val haveInjury = questionnaireViewModel.haveInjury.value

        if (haveServe.equals("right"))
        {
            return false
        }
        if (haveInjury.equals("right"))
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
