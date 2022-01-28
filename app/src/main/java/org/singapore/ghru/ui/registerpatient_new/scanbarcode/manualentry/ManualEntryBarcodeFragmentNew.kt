package org.singapore.ghru.ui.registerpatient_new.scanbarcode.manualentry

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.RegisterPatientBarcodeManualentryFragmentBinding
import org.singapore.ghru.databinding.RegisterPatientBarcodeManualentryFragmentNewBinding
import org.singapore.ghru.databinding.RegisterPatientBarcodeManualentryFragmentSgBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantMeta
import javax.inject.Inject

class ManualEntryBarcodeFragmentNew : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<RegisterPatientBarcodeManualentryFragmentNewBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
//    @Inject
//    lateinit var scanbarcodeViewModel: ScanBarcodeViewModel

    private val disposables = CompositeDisposable()

    private var participantMeta: ParticipantMeta? = null

    private var concentPhoto: String? = null

    @Inject
    lateinit var viewModel: ManualEntryScanBarcodeViewModelNew

    var participant_id: String? = null
    var race: String? = null
    var nationality: String? = null
    var birthYear: String? = null
    var age: String? = null
    var dob: String? = null
    var gender: String? = null
    var otherRace: String? = null
    var lastMealTime: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
            concentPhoto = arguments?.getString("concentPhotoPath")!!
        } catch (e: KotlinNullPointerException) {

        }

        try {

            if(arguments?.getString("screeningId") != null) {
                participant_id = arguments?.getString("screeningId")!!
                gender = arguments?.getString("gender")!!
                dob = arguments?.getString("dob")!! + "-01-01"
                birthYear = arguments?.getString("dob")!!
                age = arguments?.getString("age")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

        try {

            if(arguments?.getString("screeningId") != null) {
                race = arguments?.getString("race")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

        try {

            if(arguments?.getString("screeningId") != null) {
                nationality = arguments?.getString("nationality")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

        try {

            if(arguments?.getString("screeningId") != null) {
                otherRace = arguments?.getString("otherRace")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

        try {

            if(arguments?.getString("screeningId") != null) {
                lastMealTime = arguments?.getString("lastMealTime")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<RegisterPatientBarcodeManualentryFragmentNewBinding>(
            inflater,
            R.layout.register_patient_barcode_manualentry_fragment_new,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonContinue.singleClick {

            handleContinue()
            view?.hideKeyboard()
        }
        binding.buttonBack.singleClick {

            navController().popBackStack()
        }
        binding.editTextCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.textLayoutCode.error = ""
            }
        })

        viewModel.screeningIdCheck?.observe(this, Observer { householdId ->
            ////L.d(householdId.toString())
            if (householdId?.status == Status.SUCCESS) {
                val codeCheckDialogFragment = CodeCheckDialogFragment()
                codeCheckDialogFragment.show(fragmentManager!!)
            } else if (householdId?.status == Status.ERROR) {
                Log.d("data", "pmeta: " + participantMeta + ", cpath: " +concentPhoto + ", sid: " + participant_id + ", gen: " + gender + ", dob " + dob
                + ", age: " + age + ", race: " + race + ", nation: " + nationality)
                findNavController().navigate(
                    R.id.action_scanBarcodeManualFragmentNew_to_basicDetailsFragmentNew,
                    bundleOf(
                        "participantMeta" to "NA",
                        "concentPhotoPath" to "NA",
                        "screeningId" to binding.editTextCode.text.toString(),
                        "gender" to "NA",
                        "dob" to "NA",
                        "age" to "NA",
                        "race" to "NA",
                        "nationality" to "NA",
                        "isFromScan" to false,
                        "otherRace" to "NA",
                        "lastMealTime" to "NA"
                    )
                )
            }

        })
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.editTextCode.requestFocus()

    }

    fun handleContinue() {
        val checkSum = validateChecksum(binding.editTextCode.text.toString(), Constants.TYPE_PARTICIPANT)

        if (!checkSum.error) {
            activity?.runOnUiThread {
                participantMeta?.body?.screeningId = binding.editTextCode.text.toString()
                viewModel.setScreeningId(binding.editTextCode.text.toString())
            }
        } else {

            binding.textLayoutCode.error = getString(R.string.invalid_code)//checkSum.message
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
//                findNavController().navigate(
//                    R.id.action_global_reviewFragment, bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto)
//                )
                return navController().popBackStack()
            }
        }
        return true
    }

    fun navController() = findNavController()


}
