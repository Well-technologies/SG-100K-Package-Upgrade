package org.singapore.ghru.ui.registerpatient_new.review

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ReviewPatientFragmentNewBinding
import org.singapore.ghru.databinding.ReviewPatientFragmentSgBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Date
import org.singapore.ghru.vo.UserConfig
import org.singapore.ghru.vo.request.ParticipantMeta
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class ReviewFragmentNew : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<ReviewPatientFragmentNewBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var reviewViewModelNew: ReviewViewModelNew

    var participantMeta: ParticipantMeta? = null

    var yob: String? = null
    var updatedDOB: String? = null
    var updatedAge: String? = null

    val sdf = SimpleDateFormat(Constants.dataFormatOLD, Locale.US)

    var cal = Calendar.getInstance()

    private var concentPhoto: String? = null
    var isFromScan:Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantMeta = arguments?.getParcelable<ParticipantMeta>("participantMeta")!!
            yob = arguments?.getString("yob")!!
            concentPhoto = arguments?.getString("concentPhotoPath")!!

        } catch (e: KotlinNullPointerException) {
            Log.d("EXCEPTION", "IS: " + e.toString())
        }

        try {

            if(arguments?.getBoolean("isFromScan") != null) {
                isFromScan = arguments?.getBoolean("isFromScan")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ReviewPatientFragmentNewBinding>(
            inflater,
            R.layout.review_patient_fragment_new,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.member = participantMeta
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)
        binding.root.hideKeyboard()
        binding.viewModel = reviewViewModelNew

        // binding.userPhoto.setImageBitmap(b)
        binding.viewModel?.gender?.postValue(participantMeta?.body?.gender)

        binding.editTextCode.setMaskedText(participantMeta?.body?.screeningId?.removePrefix("P")?.replace("-",""))
        binding.editTextCode.isEnabled = false

        if (participantMeta?.body?.age?.dob != null) {
            val date = SimpleDateFormat(Constants.dataFormatOLD, Locale.US).parse(participantMeta?.body?.age?.dob!!)
            reviewViewModelNew.birthDateVal.postValue(Date(date?.year!!, date.month, date?.date))
            binding.viewModel?.birthDate?.postValue(participantMeta?.body?.age?.dob!!)
        }

        if (participantMeta?.body?.age != null) {
            binding.viewModel?.age?.postValue(participantMeta?.body?.age?.ageInYears)
        }

        if (yob != null)
        {
            reviewViewModelNew.birthYearVal.postValue(yob)
            binding.birthDate.isEnabled = true
        }

        reviewViewModelNew.gender.observe(this, androidx.lifecycle.Observer { gender ->
            participantMeta?.body?.gender = gender.toString().toUpperCase()
            // Log.d("Gender >>",gender.toString().toUpperCase())
        })


        binding.nextButton.singleClick {
            binding.root.hideKeyboard()

            if (updatedDOB != null)
            {
                participantMeta!!.body.age.dob = updatedDOB.toString()
            }

            if (updatedAge != null)
            {
                participantMeta!!.body.age.ageInYears = updatedAge.toString()
            }
            Timber.d(participantMeta.toString())
            navController().navigate(
                R.id.action_reviewFragmentNew_to_confirmationFragmentNew,
                bundleOf("participantMeta" to participantMeta)
            )
        }

//        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
//            reviewViewModelNew.birthYear = year
//            cal.set(Calendar.YEAR, year)
//            cal.set(Calendar.MONTH, monthOfYear)
//            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//            val birthDate: Date =
//                Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
//            reviewViewModelNew.birthDate.postValue(sdf.format(cal.time))
//            reviewViewModelNew.birthDateVal.postValue(birthDate)
//            val years = Calendar.getInstance().get(Calendar.YEAR) - year
//            reviewViewModelNew.age.value = years.toString()
//
//            binding.executePendingBindings()
//        }

        binding.linearLayoutDob.singleClick {

            val dialog = datePickerDialog()
            dialog.show()

            // Hide Day Selector
            val day = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))
            if (day != null) {
                day.visibility = View.GONE
            }

            // Hide month Selector
            val month = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/month", null, null))
            if (month != null) {
                month.visibility = View.GONE
            }

            dialog.datePicker.setCalendarViewShown(false)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -18)
            dialog.datePicker.maxDate = calendar.timeInMillis

//            var datepicker = DatePickerDialog(
//                activity!!, R.style.datepicker, dateSetListener,
//                1998,
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)
//            )
//            val calendar = Calendar.getInstance()
//            calendar.add(Calendar.YEAR, -80)
//            datepicker.datePicker.minDate = calendar.timeInMillis
//            datepicker.show()
        }

        binding.birthDate.singleClick {

            val dialog = datePickerDialog()
            dialog.show()

            // Hide Day Selector
            val day = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))
            if (day != null) {
                day.visibility = View.GONE
            }

            // Hide month Selector
            val month = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/month", null, null))
            if (month != null) {
                month.visibility = View.GONE
            }

            dialog.datePicker.setCalendarViewShown(false)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -18)
            dialog.datePicker.maxDate = calendar.timeInMillis

//            var datepicker = DatePickerDialog(
//                activity!!, R.style.datepicker, dateSetListener,
//                1998,
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)
//            )
//            val calendar = Calendar.getInstance()
//            calendar.add(Calendar.YEAR, -80)
//            datepicker.datePicker.minDate = calendar.timeInMillis
//            datepicker.show()
        }

        binding.previousButton.singleClick {
            navController().popBackStack()
        }

        onTextChanges(binding.birthDate)

        Log.d("REVIEW_DETAILS", "ON_CREATE_IS_FROM_SCAN:" + isFromScan)

        if (isFromScan!!)
        {
            Log.d("REVIEW_DETAILS", "IN_SIDE_IS_FROM_SCAN:" + isFromScan)
            binding.editTextCode.isEnabled = false
            binding.buttonMale.isEnabled = false
            binding.buttonFemale.isEnabled = false
            binding.buttonGenderOther.isEnabled = false
            binding.inputLayoutBirthDate.isEnabled = false
            binding.birthDate.isEnabled = false
            binding.linearLayoutDob.isEnabled = false
        }

    }

    private fun validateDOB() : Boolean
    {
        if(binding.birthDate.text.toString().toInt() > 17)
        {
            binding.inputLayoutBirthDate.error = null
            return true
        }
        else
        {
            binding.inputLayoutBirthDate.error = getString(R.string.error_age)
            return false
        }
    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.birthDate) {
                    validateDOB()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    fun datePickerDialog(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = 1
        val day = 1

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            binding.executePendingBindings()
            // Display Selected date in textbox
            //date.text = "$dayOfMonth $monthOfYear, $year"
            reviewViewModelNew.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, 0)
            cal.set(Calendar.DAY_OF_MONTH, 1)

            val birthDate: Date = Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
            reviewViewModelNew.birthDate.postValue(sdf.format(cal.time))
            reviewViewModelNew.birthDateVal.postValue(birthDate)
            reviewViewModelNew.birthYearVal.postValue(year.toString())
            updatedDOB = view.toSimpleDateString(cal.time)

            val years = UserConfig.getAge(
                cal.get(Calendar.YEAR),
                1,
                1
            )  //Calendar.getInstance().get(Calendar.YEAR) - year

            reviewViewModelNew.age.value = years
            updatedAge = years


            binding.executePendingBindings()
        }, year, month, day)
        // Show Date Picker

        return datePickerDialog


    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
