package org.singapore.ghru.ui.registerpatient_new.scanbarcode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ScanBarcodePatientGenFragmentBinding
import org.singapore.ghru.databinding.ScanBarcodePatientGenFragmentNewBinding
import org.singapore.ghru.databinding.ScanBarcodePatientGenFragmentSgBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantMeta
import timber.log.Timber
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class ScanBarcodeFragmentNew : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ScanBarcodePatientGenFragmentNewBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: ScanBarcodeViewModelNew

    private var participantMeta: ParticipantMeta? = null

    private var concentPhoto: String? = null

    private lateinit var codeScanner: CodeScanner

    private var participant_id: String? = null
    private var gender: String? = null
    private var birth_year: String? = null
    private var age: String? = null
    private var race: String? = null
    private var nationality: String? = null
    private var otherRace: String? = null
    private var lastMealTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ScanBarcodePatientGenFragmentNewBinding>(
            inflater,
            R.layout.scan_barcode_patient_gen_fragment_new,
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.root.hideKeyboard()

        codeScanner = CodeScanner(context!!, binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE) // list of type BarcodeFormat, ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.startPreview()

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                //Toast.makeText(activity!!, getString(R.string.scan_result) + ": ${it.text}", Toast.LENGTH_LONG).show()

                Log.d("Scanned data", "Data: " + it.text)
                val data = it.text
                val arrayData:List<String> = data.split(",")
                //val dataArr = it.text.split(",").toTypedArray()

                Toast.makeText(activity!!, getString(R.string.scan_result) + arrayData[0], Toast.LENGTH_LONG).show()
                Log.d("Array data", "array: " + arrayData[1])

                var success = true
                if(arrayData.size < 4 ) {
                    Toast.makeText(activity!!, "Missing mandatory fields. Try again." , Toast.LENGTH_LONG).show()
                    success = false
                }
                else {
                    participant_id = arrayData[0]
                    gender = arrayData[1]
                    birth_year = arrayData[2]
                    age = arrayData[3]

                    if (arrayData.size == 5)
                    {
                        race = arrayData[4]
                    }
                    else if (arrayData.size == 6)
                    {
                        race = arrayData[4]
                        otherRace = arrayData[5]
                    }
                    else if (arrayData.size == 7)
                    {
                        race = arrayData[4]
                        otherRace = arrayData[5]
                        nationality = arrayData[6]
                    }
                    else if (arrayData.size == 8)
                    {
                        race = arrayData[4]
                        otherRace = arrayData[5]
                        nationality = arrayData[6]
                        lastMealTime = arrayData[7]
                    }

                    val checkSum = validateChecksum(participant_id!!, Constants.TYPE_PARTICIPANT)
                    if (!checkSum.error) {
                        viewModel.setScreeningId(participant_id)
                    }
                    else {
                        success = false
                    }

//                    val from = LocalDate.parse("0101"+birth_year, DateTimeFormatter.ofPattern("ddMMyyyy"))
//                    // get today's date
//                    val today = LocalDate.now()
//                    // calculate the period between those two
//                    val period = Period.between(from, today)
//
//                    if (period.years < 18)
//                    {
//                        Toast.makeText(activity!!, "Invalid birth year. Try again." , Toast.LENGTH_LONG).show()
//                    }
//                    else if (age!!.toInt() < 18)
//                    {
//                        Toast.makeText(activity!!, "Invalid age. Try again." , Toast.LENGTH_LONG).show()
//                    }
//                    else
//                    {
//                        if (arrayData.size == 5)
//                        {
//                            race = arrayData[4]
//                        }
//                        else if (arrayData.size == 6)
//                        {
//                            race = arrayData[4]
//                            nationality = arrayData[5]
//                        }
//
//                        val checkSum = validateChecksum(participant_id!!, Constants.TYPE_PARTICIPANT)
//                        if (!checkSum.error) {
//                            viewModel.setScreeningId(participant_id)
//                        }
//                        else {
//                            success = false
//                        }
//                    }

                }

                if (!success) {
                    codeScanner.startPreview()
                    val errorDialogFragment = ErrorDialogFragment()
                    errorDialogFragment.setErrorMessage(getString(R.string.invalid_code))
                    errorDialogFragment.show(fragmentManager!!)
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            activity?.runOnUiThread {
                Toast.makeText(
                    activity!!, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        viewModel.screeningIdCheck?.observe(this, Observer { householdId ->
            // //L.d(householdId.toString())
            if (householdId?.status == Status.SUCCESS) {
                codeScanner.startPreview()
                val codeCheckDialogFragment = CodeCheckDialogFragment()
                codeCheckDialogFragment.show(fragmentManager!!)
            } else if (householdId?.status == Status.ERROR) {
                if (race == null || race == " ")
                {
                    race = "NA"
                }

                if (nationality == null || nationality == " ")
                {
                    nationality = "NA"
                }

                if (otherRace == null || otherRace == " ")
                {
                    otherRace = "NA"
                }

                if (lastMealTime == null || lastMealTime == " ")
                {
                    lastMealTime = "NA"
                }

                navController().navigate(
                    R.id.action_scanBarcodeFragmentNew_to_basicDetailsFragmentNew,
                    bundleOf(
                        "participantMeta" to participantMeta,
                        "concentPhotoPath" to concentPhoto,
                        "screeningId" to participant_id,
                        "gender" to gender,
                        "dob" to birth_year,
                        "age" to age,
                        "race" to race,
                        "nationality" to nationality,
                        "isFromScan" to true,
                        "otherRace" to otherRace,
                        "lastMealTime" to lastMealTime
                    )
                )
            }

        })


//        if (BuildConfig.DEBUG) {
//            val barcode = "PAA-1042-9"
//            participantMeta?.body?.screeningId = barcode
//            viewModel.setScreeningId(barcode)
//        }

        //binding.buttonManualEntry.visibility = View.GONE

        binding.buttonManualEntry.singleClick {

            if (race == null)
            {
                race = "NA"
            }

            if (nationality == null)
            {
                nationality = "NA"
            }

            binding.root.hideKeyboard()
            Timber.d(participantMeta.toString())
            Log.d("data", "pmeta: " + participantMeta + ", cpath: " +concentPhoto + ", sid: " + participant_id + ", gen: " + gender + ", dob " + birth_year
                    + ", age: " + age + ", race: " + race + ", nation: " + nationality)
            navController().navigate(
                R.id.action_scanBarcodeFragmentNew_to_scanBarcodeManualFragmentNew,
                bundleOf(
                    "participantMeta" to "NA",
                    "concentPhotoPath" to "NA",
                    "screeningId" to "NA",
                    "gender" to "NA",
                    "dob" to "NA",
                    "age" to "NA",
                    "race" to "NA",
                    "nationality" to "NA",
                    "otherRace" to "NA",
                    "lastMealTime" to "NA"
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }


    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
