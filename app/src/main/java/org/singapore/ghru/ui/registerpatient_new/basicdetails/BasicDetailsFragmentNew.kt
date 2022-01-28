package org.singapore.ghru.ui.registerpatient_new.basicdetails

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
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
import br.com.ilhasoft.support.validation.Validator
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.BasicDetailsFragmentNewBinding
import org.singapore.ghru.databinding.BasicDetailsFragmentSgBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.Date
import org.singapore.ghru.vo.request.*
import org.singapore.ghru.vo.request.Member
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class BasicDetailsFragmentNew : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<BasicDetailsFragmentNewBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var basicDetailsViewModelNew: BasicDetailsViewModelNew

    private var member: Member? = null

    private var householdId: String? = null

    val sdf = SimpleDateFormat(Constants.dataFormat, Locale.US)
    val sdfMeal = SimpleDateFormat(Constants.dateFormat_meal, Locale.US)
    val sdfDiff = SimpleDateFormat(Constants.dateFormat_diff, Locale.US)
    val sdfNow = SimpleDateFormat(Constants.dateFormat_now, Locale.US)


    var cal = Calendar.getInstance()

    var user: User? = null
    var userConfig: UserConfig? = null

    var meta: Meta? = null
    var hoursFasted: String? = null
    var memberRequest: MemberRequest = MemberRequest.build()

    lateinit var participantMeta: ParticipantMeta

    var household: HouseholdRequest? = null

    private var concentPhoto: String? = null

    private var selectedRelationShip: String? = null

    private lateinit var validator: Validator

    private var screeningIDValid: Boolean = false

    var yob: String? = null
    var race: String? = null
    var nationality: String? = null
    var birthYear: String? = null
    var age: String? = null
    var isFromScan:Boolean? = null
    var otherRace: String? = null
    var lastMealTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            //hoursFasted = "8"

            if(arguments?.getString("screeningId") != null) {
                memberRequest.screeningId = arguments?.getString("screeningId")!!
                screeningIDValid = true
                memberRequest.gender = arguments?.getString("gender")!!
                memberRequest.age.dob = arguments?.getString("dob")!! + "-01-01"
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

            if(arguments?.getBoolean("isFromScan") != null) {
                isFromScan = arguments?.getBoolean("isFromScan")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

        try {

            if(arguments?.getString("otherRace") != null) {
                otherRace = arguments?.getString("otherRace")!!
            }

        } catch (e: KotlinNullPointerException) {
            print(e)
        }

        try {

            if(arguments?.getString("lastMealTime") != null) {
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
        val dataBinding = DataBindingUtil.inflate<BasicDetailsFragmentNewBinding>(
            inflater,
            R.layout.basic_details_fragment_new,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        validator = Validator(binding)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.root.hideKeyboard()
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.setLifecycleOwner(this)

        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()

        basicDetailsViewModelNew.setUser("user")
        basicDetailsViewModelNew.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {
                user = userData.data

                val stTime: String = convertTimeTo24Hours()
                val stDate: String = getDate()
                val stDateTime:String = stDate + " " + stTime

                meta = Meta(collectedBy = user?.id, startTime = stDateTime)
                meta?.registeredBy = user?.id
            }

        })

        binding.memberRequest = memberRequest
        if (member != null) {
            binding.member = member
            memberRequest.firstName = member?.name!!
            memberRequest.lastName = member?.familyName!!
            memberRequest.nickName = if (member?.nickName == null) "" else member?.nickName!!
            memberRequest.gender = member?.gender!!
            memberRequest.hoursFasted = hoursFasted?.toInt()!!
            memberRequest.contactDetails.phoneNumberPreferred =
                    if (member?.contactNo == null) "" else member?.contactNo!!
            memberRequest.age.ageInYears = member?.age!!
            memberRequest.age.dob = member?.birthDate?.year.toString() + "-" +
                    member?.birthDate?.month.toString().format(2) + "-" + member?.birthDate?.day.toString().format(2)

            binding.viewModel = basicDetailsViewModelNew
            binding.viewModel?.gender?.postValue(member?.gender)
            if (member?.birthDate != null) {
                val c = Calendar.getInstance()
                c.set(member?.birthDate!!.year, member?.birthDate!!.month - 1, member?.birthDate!!.day)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                binding.viewModel?.birthDate?.postValue(format.format(c.time))
            }
        } else {
            binding.viewModel = basicDetailsViewModelNew

            if(memberRequest.screeningId.isNotBlank()) {
                binding.editTextCode.setMaskedText(memberRequest.screeningId.removePrefix("P").replace("-",""))
                //setDateFromQR()
            }
            else {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
                binding.editTextCode.requestFocus()

                binding.memberRequest?.gender = Gender.MALE.gender.toString()
                binding.viewModel?.gender?.postValue("male")
            }

            basicDetailsViewModelNew.screeningIdCheck?.observe(this, Observer { householdId ->
                if (householdId?.status == Status.SUCCESS) {
                    screeningIDValid = false
                    binding.editTextCode.setMaskedText("")
                    val codeCheckDialogFragment = CodeCheckDialogFragment()
                    codeCheckDialogFragment.show(fragmentManager!!)
                } else if (householdId?.status == Status.ERROR) {
                    screeningIDValid = true
                    memberRequest.screeningId = binding.editTextCode.text.toString()
                }
                validateNextButton()
            })

        }

        if (race != null)
        {
            binding.raceEditText.setText(race)
        }

        if (nationality != null)
        {
            binding.nationalityEditText.setText(nationality)
        }

        if (otherRace != null)
        {
            binding.otherEditText.setText(otherRace)
        }

        if (lastMealTime != null)
        {
            binding.lastMealEditText.setText(lastMealTime)

            if (lastMealTime != "NA")
            {
                binding.lastMealEditText.isEnabled = false
            }
        }

        Log.d("BASIC_DETAILS","DATA:" + birthYear + ", " + age)
        binding.ageTextView.text = age
        binding.textViewYears.text = getString(R.string.string_years)
        binding.birthDate.setText(birthYear)
        //binding.memberRequest?.age?.dob = sdf.format(birthYear + "01-01")
        binding.memberRequest?.age?.ageInYears = age!!
        yob = birthYear


        binding.executePendingBindings()

        binding.nextButton.singleClick {

            Log.d("BASIC_DETAILS" , "GENDER_outside: " + memberRequest.gender)

            if (memberRequest.gender != "na")
            {
                if (binding.lastMealEditText.text.toString() != "NA" || binding.lastMealEditText.text.toString() != " ")
                {
                    Log.d("BASIC_DETAILS" , "GENDER_inside: " + memberRequest.gender)
                    val gender = memberRequest.gender.toLowerCase()
                    val newStr = gender.toLowerCase()

                    val memberId: String? = if (member != null) {
                        if (isNetworkAvailable()) member?.memberId!! else member?.uuid!!
                    } else {
                        null
                    }

                    val householdIdX: String? = if (member != null) {
                        householdId
                    } else {
                        null
                    }

                    if (binding.lastMealEditText.text.toString() != "NA" && binding.lastMealEditText.text.toString() != " ")
                    {
                        hoursFasted = findFastingTime()
                        Log.d("BASIC_DETAILS" , "FASTING:" + hoursFasted)
                    }
                    else
                    {
                        hoursFasted = "8"
                    }

                    participantMeta = ParticipantMeta(
                        meta = meta!!,
                        body = ParticipantX(
                            consentObtained = true,
                            isEligible = true,
                            firstName = "TEST",//memberRequest.firstName,
                            lastName = "ONE",//memberRequest.screeningId.replace("-",""),//memberRequest.lastName,
                            preferredName = memberRequest.nickName,
                            gender = newStr,
                            hoursFasted = hoursFasted!!,
                            enumerationId = householdIdX,
                            memberId = memberId,
                            idType = "NID",
                            videoWatched = false,
                            alternateContactsDetails = ParticipantAlternateContactsDetails(
                                name = memberRequest.alternateContactsDetails.name,
                                relationship = selectedRelationShip.toString(),
                                address = memberRequest.alternateContactsDetails.address,
                                email = if (memberRequest.alternateContactsDetails.email.isEmpty()) {
                                    null
                                } else {
                                    memberRequest.alternateContactsDetails.email
                                },
                                phone_preferred = memberRequest.alternateContactsDetails.phonePreferred,
                                phone_alternate = if (memberRequest.alternateContactsDetails.phoneAlternate.isEmpty()) {
                                    null
                                } else memberRequest.alternateContactsDetails.phoneAlternate
                            ),
                            age = ParticipantAge(
                                dob = memberRequest.age.dob,
                                ageInYears = memberRequest.age.ageInYears,
                                dobComputed = true
                            ),
                            address = ParticipantAddress(
                                street = memberRequest.address.street,
                                country = memberRequest.address.country,
                                locality = memberRequest.address.locality,
                                postcode = memberRequest.address.postcode
                            ),
                            contactDetails = ParticipantContactDetails(
                                phoneNumberAlternate = if (memberRequest.contactDetails.phoneNumberAlternate.isEmpty()) null else memberRequest.contactDetails.phoneNumberAlternate,
                                phoneNumberPreferred = memberRequest.contactDetails.phoneNumberPreferred,
                                email = if (memberRequest.contactDetails.email.isEmpty()) {
                                    null
                                } else memberRequest.contactDetails.email
                            ),
                            comment = null
                        )
                    )
                    participantMeta.body.screeningId = memberRequest.screeningId
                    participantMeta.phoneCountryCode = userConfig?.mobileCode
                    participantMeta.countryCode = userConfig?.countryCode
                    if (binding.raceEditText.text.toString().isNotEmpty())
                    {
                        participantMeta.body.race = binding.raceEditText.text.toString()
                    }
                    if (binding.nationalityEditText.text.toString().isNotEmpty())
                    {
                        participantMeta.body.nationality = binding.nationalityEditText.text.toString()
                    }
                    if (binding.otherEditText.text.toString().isNotEmpty())
                    {
                        participantMeta.body.otherRace = binding.otherEditText.text.toString()
                    }
                    if (binding.lastMealEditText.text.toString().isNotEmpty())
                    {
                        participantMeta.body.lastMealTime = binding.lastMealEditText.text.toString()
                    }

                    //  Timber.d("par", participantMeta.toString())
                    findNavController().navigate(
                        R.id.action_BasicDetailFragmentNew_to_reviewFragmentNew,
                        bundleOf("participantMeta" to participantMeta, "concentPhotoPath" to concentPhoto, "yob" to yob, "isFromScan" to isFromScan)

                    )
                }
                else
                {
                    Toast.makeText(activity!!, "Please select last meal time", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(activity!!, "Please select a gender", Toast.LENGTH_LONG).show()
            }

        }

//        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            basicDetailsViewModelNew.birthYear = year
//            cal.set(Calendar.YEAR, year)
//            cal.set(Calendar.MONTH, monthOfYear+1)
//            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//
//            val birthDate: Date = Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
//            basicDetailsViewModelNew.birthDate.postValue(sdf.format(cal.time))
//            basicDetailsViewModelNew.birthDateVal.postValue(birthDate)
//            binding.member?.birthDate = birthDate
//            binding.memberRequest?.age?.dob = view.toSimpleDateString(cal.time)
//
//            val years = UserConfig.getAge(
//                cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH)
//            )  //Calendar.getInstance().get(Calendar.YEAR) - year
//
//            basicDetailsViewModelNew.age.value = years
//
//            binding.memberRequest?.age?.ageInYears = years
//
//            binding.textViewYears.text = getString(R.string.string_years)
//            binding.executePendingBindings()
//        }

        binding.birthDate.singleClick {
            val dialog = datePickerDialog()
//            dialog.datePicker.y.plus(5.0)
//            val params = dialog.window!!.attributes
//
//            params.gravity = Gravity.CENTER
//
//            dialog.window!!.setLayout(1, 5)
//            params.width = 600; // dialogWidth;
            //params.height = WindowManager.LayoutParams.MATCH_PARENT // dialogHeight;

            //dialog.window!!.attributes = params
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
            dialog.datePicker.setSpinnersShown(true)
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
//
//            datepicker.show()
        }

        basicDetailsViewModelNew.setUser("user")
        basicDetailsViewModelNew.user?.observe(this, Observer { userData ->
            if (userData?.data != null) {

                user = userData.data
                val countryCode = user?.team?.country
                userConfig = UserConfig.getUserConfig(countryCode)

//                binding.contactNumberPrimaryCodeEditText.setText(userConfig?.mobileCode + " - ")
                binding.contactPersonContactNumberPrimaryCodeEditText.setText(userConfig?.mobileCode + " - ")
//                binding.contactNumberPrimaryEditText.filters =
//                        arrayOf<InputFilter>(InputFilter.LengthFilter(userConfig?.mobileMaxLength!!))

                memberRequest.address.country = user?.team?.country!!

            }
        })

        onTextChanges(binding.editTextCode)
        onTextChanges(binding.birthDate)
        validateNextButton()

        Log.d("BASCI_DETAILS", "ON_CREATE_IS_FROM_SCAN:" + isFromScan)

        if (isFromScan!!)
        {
            Log.d("BASCI_DETAILS", "IN_SIDE_IS_FROM_SCAN:" + isFromScan)
            binding.editTextCode.isEnabled = false
            binding.buttonMale.isClickable = false
            binding.buttonFemale.isClickable = false
            binding.buttonGenderOther.isClickable = false
            binding.inputLayoutBirthDate.isEnabled = false
            binding.birthDate.isEnabled = false
            binding.raceTextLayout.isEnabled = false
            binding.raceEditText.isEnabled = false
            binding.nationalityTextLayout.isEnabled = false
            binding.nationalityEditText.isEnabled = false
        }

        binding.lastMealEditText.singleClick {

            val dialog = datePickerDialogMeal()
            dialog.show()

            dialog.datePicker.setCalendarViewShown(false)
            dialog.datePicker.setSpinnersShown(true)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, 0)
            val rt = System.currentTimeMillis()-(1000L*60*60*8)
            Log.d("SAPLE_COLLECTION", "CURRENT: " + calendar.timeInMillis + ", 8 HOURS BACK" + rt)
            dialog.datePicker.maxDate = rt
        }

    }

    private fun onTextChanges(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(editText == binding.birthDate) {
                    validateNextButton()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(editText == binding.editTextCode) {
                    screeningIDValid = false
                    binding.textLayoutCode.error = ""
                    validateScreeningId()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return navController().popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateNextButton() {

        if (screeningIDValid
            && !binding.memberRequest?.gender.isNullOrBlank()
            && !binding.ageTextView.text.toString().isNullOrBlank()
            && validateDOB()) {
            binding.nextButton.setTextColor(Color.parseColor("#0A1D53"))
            binding.nextButton.setDrawableRightColor("#0A1D53")
            binding.nextButton.isEnabled = true
        } else {
            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"))
            binding.nextButton.setDrawableRightColor("#AED6F1")
            binding.nextButton.isEnabled = false
        }

    }
    private fun validateScreeningId() {
        val id = binding.editTextCode.text.toString()
        val checkSum = validateChecksum(id, Constants.TYPE_PARTICIPANT)
        if (!checkSum.error) {
            basicDetailsViewModelNew.setScreeningId(id)
        } else {
            binding.textLayoutCode.error = getString(R.string.invalid_code)//checkSum.message
            binding.nextButton.setTextColor(Color.parseColor("#AED6F1"));
            binding.nextButton.setDrawableRightColor("#AED6F1")
            binding.nextButton.isEnabled = false
        }
    }
    private fun validateDOB() : Boolean
    {
//         if(binding.memberRequest?.age?.ageInYears != null && binding.memberRequest?.age?.ageInYears != "" && binding.memberRequest?.age?.ageInYears!!.toInt() > 17)
//         {
//             binding.inputLayoutBirthDate.error = null
//             return true
//         }
//        else
//         {
//             binding.inputLayoutBirthDate.error = getString(R.string.error_age)
//             return false
//         }
        if (!binding.ageTextView.text.equals("NA"))
        {
            if(binding.ageTextView.text != null && binding.ageTextView.text.toString() != "" && binding.ageTextView.text.toString().toFloat() > 17.9)
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
        else
        {
            return false
        }

    }
//    private fun validatePrimaryMobile(): Boolean {
//
//        if (!binding.contactNumberPrimaryEditText.text.isNullOrEmpty()) {
//            if (UserConfig.isValidPhoneNumber(binding.contactNumberPrimaryEditText.text.toString(), userConfig!!)) {
//                binding.contactNumberPrimaryTextLayout.error = null
//                return true
//            } else {
//                binding.contactNumberPrimaryTextLayout.error = getString(R.string.app_error_valid_phone)
//                return false
//            }
//
//        } else {
//            return false
//        }
//    }

//    private fun validateSecondaryMobile(): Boolean {
//
//        if (!binding.contactNumberSecondryEditText.text.isNullOrEmpty()) {
//            if (UserConfig.isValidPhoneNumber(binding.contactNumberSecondryEditText.text.toString(), userConfig!!)) {
//                binding.contactNumberSecondryTextLayout.error = null
//                // binding.contactNumberSecondryTextLayout.clearFocus()
//                return true
//            } else {
//
//                //   binding.contactNumberSecondryTextLayout.requestFocus();
//                binding.contactNumberSecondryTextLayout.error = getString(R.string.app_error_valid_phone)
//                return false
//            }
//
//        } else {
//            return true // bcz optional
//        }
//    }

    private fun validateContactSecondaryMobile(): Boolean {

        if (!binding.contactPersonContactNumberSecondryEditText.text.isNullOrEmpty()) {
            if (UserConfig.isValidPhoneNumber(
                    binding.contactPersonContactNumberSecondryEditText.text.toString(),
                    userConfig!!
                )
            ) {
                binding.contactPersonContactNumberSecondryTextLayout.error = null
                // binding.contactPersonContactNumberSecondryTextLayout.clearFocus()
                //binding.contactPersonEmailEditText.requestFocus()

                return true
            } else {

                //binding.contactPersonContactNumberSecondryTextLayout.requestFocus();
                binding.contactPersonContactNumberSecondryTextLayout.error = getString(R.string.app_error_valid_phone)
                return false
            }

        } else {
            return true // bcz optional
        }
    }

    private fun validateContactPrimaryMobile(): Boolean {

        if (!binding.contactPersonContactNumberPrimaryEditText.text.isNullOrEmpty()) {
            if (UserConfig.isValidPhoneNumber(
                    binding.contactPersonContactNumberPrimaryEditText.text.toString(),
                    userConfig!!
                )
            ) {
                binding.contactPersonContactNumberPrimaryTextLayout.error = null
                // binding.contactPersonContactNumberPrimaryTextLayout.clearFocus()
                // binding.contactPersonContactNumberSecondryEditText.requestFocus()
                return true
            } else {

                // binding.contactPersonContactNumberPrimaryTextLayout.requestFocus();
                binding.contactPersonContactNumberPrimaryTextLayout.error = getString(R.string.app_error_valid_phone)
                return false
            }

        } else {
            return false
        }
    }

    private fun validatePrimaryEmail(): Boolean {
        if (!binding.emailEditText.text.isNullOrEmpty()) {
            if (UserConfig.isValidEMail(memberRequest.contactDetails.email)) {
                binding.emailTextLayout.error = ""
                //binding.emailTextLayout.clearFocus()
                return true
            } else {
                binding.emailTextLayout.error = getString(R.string.app_error_valid_email)
                // binding.emailTextLayout.requestFocus()
                return false
            }
        } else {
            return true // bcz optional
        }
    }

    private fun validateContactPersonEmail(): Boolean {
        if (!binding.contactPersonEmailEditText.text.isNullOrEmpty()) {
            if (UserConfig.isValidEMail(memberRequest.alternateContactsDetails.email)) {
                binding.contactPersonEmailTextLayout.error = ""
                //binding.contactPersonEmailTextLayout.clearFocus()
                // binding.nextButton.requestFocus()
                return true
            } else {
                binding.contactPersonEmailTextLayout.error = getString(R.string.app_error_valid_email)
                // binding.contactPersonEmailTextLayout.requestFocus()
                return false
            }
        } else {
            return true // bcz optional
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    // to set the 24 hours time ------------------------------ 7.2.2020 --------- Nuwan ----------

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: java.util.Date
        val output: String
        try{
            date= inputFormat.parse(now.time.toLocaleString())
            output = outputformat.format(date)
            return output
        }catch(p: ParseException){
            return ""
        }
    }

    private fun getDate(): String
    {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val outputformat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date: java.util.Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }

    private fun setDateFromQR() {

        val dataArr = memberRequest.age.dob.split("-").toTypedArray()
        val year = dataArr[0].toInt()
//        val monthOfYear = dataArr[1].toInt()
//        val dayOfMonth = dataArr[2].toInt()
        val monthOfYear = 1
        val dayOfMonth = 1

        basicDetailsViewModelNew.birthYear = year
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val birthDate: Date = Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        basicDetailsViewModelNew.birthDate.postValue(sdf.format(cal.time))
        basicDetailsViewModelNew.birthDateVal.postValue(birthDate)
        binding.member?.birthDate = birthDate

        val years = UserConfig.getAge(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )  //Calendar.getInstance().get(Calendar.YEAR) - year

        basicDetailsViewModelNew.age.value = years

        binding.memberRequest?.age?.ageInYears = years

        binding.textViewYears.text = getString(R.string.string_years)
        binding.executePendingBindings()
        validateNextButton()
    }

    // -------------------------------------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    fun datePickerDialog(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = 1
        val day = 1

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(activity, R.style.DatePickerDialogTheme, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            //date.text = "$dayOfMonth $monthOfYear, $year"
            basicDetailsViewModelNew.birthYear = year
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, 1)
            cal.set(Calendar.DAY_OF_MONTH, 1)

            val birthDate: Date = Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
            basicDetailsViewModelNew.birthDate.postValue(sdf.format(cal.time))
            basicDetailsViewModelNew.birthDateVal.postValue(birthDate)
            basicDetailsViewModelNew.birthYearVal.postValue(year.toString())
            yob = year.toString()
            binding.member?.birthDate = birthDate
            binding.memberRequest?.age?.dob = view.toSimpleDateString(cal.time)

            val years = UserConfig.getAge(
                cal.get(Calendar.YEAR),
                1,
                1
            )  //Calendar.getInstance().get(Calendar.YEAR) - year

            basicDetailsViewModelNew.age.value = years

            binding.memberRequest?.age?.ageInYears = years
            binding.ageTextView.text = years
            binding.textViewYears.text = getString(R.string.string_years)
            binding.birthDate.setText(year.toString())
            binding.executePendingBindings()
        }, year, month, day)
        // Show Date Picker

        return datePickerDialog


    }

    @SuppressLint("SetTextI18n")
    fun datePickerDialogMeal(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(context!!, R.style.datepicker, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            //binding.lastMealDate.setText(sdf.format(cal.time))

            getMealTime(sdfMeal.format(cal.time))
            binding.executePendingBindings()
        }, year, month, day)
        // Show Date Picker

        return datePickerDialog


    }

    @SuppressLint("SetTextI18m")
    fun getMealTime(date: String?)
    {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val min = c.get(Calendar.MINUTE)

        // Time Picker Dialog
        val timePickerDialog = TimePickerDialog(context!!, R.style.datepicker, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)

            var ho:String? = null
            var min:String? = null

            if (hourOfDay >= 10)
            {
                ho =  hourOfDay.toString()
            }
            else
            {
                ho =  "0"+ hourOfDay.toString()
            }

            if (minute >= 10)
            {
                min =  minute.toString()
            }
            else
            {
                min =  "0"+ minute.toString()
            }

            binding.lastMealEditText.setText(date + " " + ho + ":" + min)

//            binding.lastMealTime.setText(hourOfDay.toString() + ":" + minute.toString())
//            selectedTime = hourOfDay.toString() + ":" + minute.toString()
            binding.executePendingBindings()
        }, hour, min, false)

        timePickerDialog.show()

    }

    private fun findFastingTime(): String
    {
        var fastingHours : String? = "8"

        try
        {
            val mealTime = sdfNow.parse(binding.lastMealEditText.text.toString())

            var dateNow :String? = ""


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                dateNow =  current.format(formatter)

            } else {
                val date = java.util.Date()
                val formatter = SimpleDateFormat("MMM dd yyyy HH:mma")
                dateNow = formatter.format(date)

            }

            val mealTime1 = sdfNow.parse(dateNow)

            Log.d("BASIC_DETAILS", "MEAL_TIME: " + mealTime + " NOW: " + mealTime1  )

            val diff: Long = mealTime1.time - mealTime.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60

            fastingHours = hours.toString()
        }
        catch (e:Exception)
        {
            Log.d("BASIC_DETAILS_FRAG", "ERROR:" + e.toString())
        }

        return fastingHours!!
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}





