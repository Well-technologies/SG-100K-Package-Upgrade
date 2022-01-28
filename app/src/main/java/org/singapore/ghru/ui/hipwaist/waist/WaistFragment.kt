package org.singapore.ghru.ui.hipwaist.waist


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
import android.widget.Toast
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
import org.singapore.ghru.BuildConfig
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.HipFragmentBinding
import org.singapore.ghru.databinding.WaistFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.*
import org.singapore.ghru.network.ConnectivityReceiver
import org.singapore.ghru.ui.hipwaist.reason.ReasonDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.HipWaistData
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.*
import javax.inject.Inject


class WaistFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager


    var binding by autoCleared<WaistFragmentBinding>()


    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private val disposables = CompositeDisposable()

    private lateinit var validator: Validator

    @Inject
    lateinit var viewModel: WaistViewModel
//    private lateinit var bodyMeasurementReading1: BodyMeasurementValue
//    private lateinit var bodyMeasurementReading2: BodyMeasurementValue
//    private lateinit var bodyMeasurementReading3: BodyMeasurementValue

    private var deviceListName: MutableList<String> = arrayListOf()
    private var deviceListObject: List<StationDeviceData> = arrayListOf()
    private var selectedDeviceID: String? = null
//    private lateinit var bodyMeasurementData: BodyMeasurementDataNew

    private var participantRequest: ParticipantRequest? = null
    private var waistData: HipWaistData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        bodyMeasurementReading1 = BodyMeasurementValue.build()
//        bodyMeasurementReading2 = BodyMeasurementValue.build()
//        bodyMeasurementReading3 = BodyMeasurementValue.build()

        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
            waistData = arguments?.getParcelable<HipWaistData>("WaistData")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<WaistFragmentBinding>(
            inflater,
            R.layout.waist_fragment,
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
        binding.measurement = waistData

        if (waistData != null)
        {
            binding.hipR1EditText.setText(waistData?.value?.get(0).toString())
            binding.hipR2EditText.setText(waistData?.value?.get(1).toString())
            binding.hipR3EditText.setText(waistData?.value?.get(2).toString())
        }


        if (BuildConfig.DEBUG) {
//            bodyMeasurementReading1.unit = "cm"
////            bodyMeasurementValue.value = "80.2"
////            bodyMeasurementValue.comment = "Reason by Mujeeb create body Hip"
//
//            bodyMeasurementReading2.unit = "cm"
//            //bodyMeasurementValueHip.value = "79.5"
//
//            bodyMeasurementReading3.unit = "cm"
//            //bodyMeasurementValueHip.value = "79.5"

        }
//        binding.hipReading1 = bodyMeasurementReading1
//        binding.hipReading2 = bodyMeasurementReading2
//        binding.hipReading3 = bodyMeasurementReading3
//        bodyMeasurementData = BodyMeasurementDataNew()
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
        binding.deviceIdSpinner.setAdapter(adapter)

        viewModel.setStationName(Measurements.waist)
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
            //println(bodyMeasurementData.toString())
//            if(selectedDeviceID==null)
//            {
//                binding.textViewDeviceError.visibility = View.VISIBLE
//            }else
            if (validateWaistSize1(binding.hipR1EditText.text.toString()) && validateWaistSize2(binding.hipR2EditText.text.toString()) && validateWaistSize3(binding.hipR3EditText.text.toString()))
            {
                val waistList:ArrayList<Double> = arrayListOf()
                waistList.add(binding.hipR1EditText.text.toString().toDouble())
                waistList.add(binding.hipR2EditText.text.toString().toDouble())
                waistList.add(binding.hipR3EditText.text.toString().toDouble())

                if (validateDifference(waistList))
                {
                    val record = HipWaistData(
                        unit = "cm")

                    record.value = waistList

                    Log.d("WAIST_FRAGMENT","DATA: " + record)

                    WaistRecordTestRxBus.getInstance().post(record)
                    binding.root.hideKeyboard()
                    navController().popBackStack()
                }
                else
                {
                    // Dialog
                    Toast.makeText(activity!!, "Please double check values", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.buttonCancel.singleClick {
            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }

        onTextChanges(binding.hipR1EditText)
        onTextChanges(binding.hipR2EditText)
        onTextChanges(binding.hipR3EditText)
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.hipR1EditText) {
                    validateWaistSize1(binding.hipR1EditText.text.toString())
                }

                if(editText == binding.hipR2EditText) {
                    validateWaistSize2(binding.hipR2EditText.text.toString())
                }

                if(editText == binding.hipR3EditText) {
                    validateWaistSize3(binding.hipR3EditText.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    private fun validateWaistSize1(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_WAIST_MIN_VAL && fatVal <= Constants.BD_WAIST_MAX_VAL) {
                binding.hipR1TextLayout.error = null
                viewModel.isValidWaistSize = false
                return true


            } else {
                viewModel.isValidWaistSize = true
                binding.hipR1TextLayout.error  = getString(R.string.error_not_in_range)
                return false

            }

            //validateNextButton()

        } catch (e: Exception) {
            binding.hipR1TextLayout.error = getString(R.string.error_invalid_input)
            return false

        }

    }

    private fun validateWaistSize2(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_WAIST_MIN_VAL && fatVal <= Constants.BD_WAIST_MAX_VAL) {
                binding.hipR2TextLayout.error = null
                viewModel.isValidWaistSize = false
                return true


            } else {
                viewModel.isValidWaistSize = true
                binding.hipR2TextLayout.error  = getString(R.string.error_not_in_range)
                return false

            }

            //validateNextButton()

        } catch (e: Exception) {
            binding.hipR2TextLayout.error = getString(R.string.error_invalid_input)
            return false

        }

    }

    private fun validateWaistSize3(fat: String): Boolean {

        try {
            val fatVal: Double = fat.toDouble()
            if (fatVal >= Constants.BD_WAIST_MIN_VAL && fatVal <= Constants.BD_WAIST_MAX_VAL) {
                binding.hipR3TextLayout.error = null
                viewModel.isValidWaistSize = false
                return true


            } else {
                viewModel.isValidWaistSize = true
                binding.hipR3TextLayout.error  = getString(R.string.error_not_in_range)
                return false

            }

            //validateNextButton()

        } catch (e: Exception) {
            binding.hipR3TextLayout.error = getString(R.string.error_invalid_input)
            return false

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_skip -> {
                val reasonDialogFragment = ReasonDialogFragment()
                reasonDialogFragment.show(fragmentManager!!)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.bp_main, menu)
        checkConnection(menu!!)
    }

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

    private fun validateDifference(listData : ArrayList<Double>):Boolean
    {
        val intWaistArray = listData.map(Double::toDouble)
        val maxWaist = intWaistArray.max() ?: 0.0
        val minWaist = intWaistArray.min() ?: 0.0

        val waistDiff = maxWaist - minWaist

        Log.d("HIPFRAGMENT" , "DATA: " + waistDiff)

        if (waistDiff > 2.0)
        {
            return false
        }

        return true
    }


}
