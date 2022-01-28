package org.singapore.ghru.ui.samplecollection.bagscanned

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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.birbit.android.jobqueue.JobManager
import com.crashlytics.android.Crashlytics
import com.google.android.material.textfield.TextInputLayout
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.BagScannedFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.samplecollection.bagscanned.completed.CompletedDialogFragment
import org.singapore.ghru.ui.samplecollection.bagscanned.partialreadings.PartialReadingsDialogFragment
import org.singapore.ghru.ui.samplecollection.bagscanned.reason.ReasonDialogFragment
import org.singapore.ghru.ui.samplecollection.questions.SampleQuestionsViewModel
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

class BagScannedFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<BagScannedFragmentBinding>()

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private var participant: ParticipantRequest? = null
    private var sampleId: String? = null

    @Inject
    lateinit var viewModel: BagScannedViewModel
    @Inject
    lateinit var jobManager: JobManager

    var allSampleCollected: Boolean = false
    private var isPartial : Boolean? = null

    private lateinit var sampleQuestionsViewModel: SampleQuestionsViewModel

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
        val dataBinding = DataBindingUtil.inflate<BagScannedFragmentBinding>(
            inflater,
            R.layout.bag_scanned_fragment,
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
        binding.linearLayoutEcContainer.collapse()
        binding.buttonCancel.singleClick {

            val reasonDialogFragment = ReasonDialogFragment()
            reasonDialogFragment.arguments = bundleOf("participant" to participant)
            reasonDialogFragment.show(fragmentManager!!)
        }

        Log.d("BAG_SCAN_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        binding.buttonSubmit.singleClick {

            if (validateNextButton())
            {
                Log.d("BAG_SCAN_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val eTime: String = convertTimeTo24Hours()
                val eDate: String = getDate()
                val eDateTime:String = eDate + " " + eTime

                participant?.meta!!.endTime = eDateTime

                Log.d("BAG_SCAN_FRAG", "AFTER_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                val mSampleCreateRequest = SampleCreateRequest(
                    meta =  participant?.meta,
                    comment  = binding.comment.text.toString())

                mSampleCreateRequest.barcode_readings = getBarcodeReadings()
                mSampleCreateRequest.contraindications = getContraindications()

                if (!isPartial!!)
                {
                    mSampleCreateRequest.isPartialSubmission = false
                    viewModel.setSample(participant, mSampleCreateRequest)
                }
                else
                {
                    mSampleCreateRequest.isPartialSubmission = true
                    val partialDialogFragment = PartialReadingsDialogFragment()
                    partialDialogFragment.arguments = bundleOf(
                        "participant" to participant,
                        "sampleCreateRequest" to mSampleCreateRequest
                    )
                    partialDialogFragment.show(fragmentManager!!)
                }
            }
            else
            {
                Toast.makeText(activity!!, "Please enter at least one reading", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.sample?.observe(this, Observer { sampleResource ->

            if (sampleResource?.status == Status.SUCCESS) {
                //println(user)
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (sampleResource?.status == Status.ERROR) {
                //Crashlytics.setString("sampleId", sampleId.toString())
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("sample collection " + sampleResource.message.toString()))
                binding.buttonSubmit.visibility = View.GONE
                binding.textViewError.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.textViewError.text = sampleResource.message?.message
                //Crashlytics.logException(Exception(sampleResource.message?.message))
            }
        })


        binding.imageButtonEC.singleClick {
            if (binding.expand!!) {

                //collapse(binding.linearLayoutEcContainer)
                binding.linearLayoutEcContainer.collapse()
                binding.expand = false

            } else {
                //itexpand()
                binding.linearLayoutEcContainer.expand()
                binding.expand = true
            }
        }
        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            binding.checkLayout.background = resources.getDrawable(R.drawable.ic_base_check, null)
            allSampleCollected = isChecked
        }

        binding.participant = participant

        onTextChanges(binding.textInputEditText1, binding.textInputEditText2, binding.textInputLayout1)
        onTextChanges(binding.textInputEditText2, binding.textInputEditText3, binding.textInputLayout2)
        onTextChanges(binding.textInputEditText3, binding.textInputEditText4, binding.textInputLayout3)
        onTextChanges(binding.textInputEditText4, binding.textInputEditText5, binding.textInputLayout4)
        onTextChanges(binding.textInputEditText5, binding.textInputEditText6, binding.textInputLayout5)
        onTextChanges(binding.textInputEditText6, binding.textInputEditText7, binding.textInputLayout6)
        onTextChanges(binding.textInputEditText7, binding.textInputEditText8, binding.textInputLayout7)
        //onTextChanges(binding.textInputEditText8, binding.textInputEditText9, binding.textInputLayout8)
        onTextChanges(binding.textInputEditText9, binding.textInputEditText10, binding.textInputLayout9)
        onTextChanges(binding.textInputEditText10, binding.textInputEditText11, binding.textInputLayout10)
        onTextChanges(binding.textInputEditText11, binding.textInputEditText12, binding.textInputLayout11)
        onTextChanges(binding.textInputEditText12, binding.textInputEditText13, binding.textInputLayout12)
        onTextChanges(binding.textInputEditText13, binding.textInputEditText14, binding.textInputLayout13)
        onTextChanges(binding.textInputEditText14, binding.textInputEditText15, binding.textInputLayout14)
        onTextChanges(binding.textInputEditText15, binding.textInputEditText16, binding.textInputLayout15)
        //onTextChanges(binding.textInputEditText16, binding.textInputEditText17, binding.textInputLayout16)
        onTextChanges(binding.textInputEditText17, binding.comment, binding.textInputLayout17)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sampleQuestionsViewModel = activity?.run {
            ViewModelProviders.of(this).get(SampleQuestionsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
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
            if ((editText1.text.toString().replace(" ", "").length == 12) || (editText1.text.toString().replace(" ", "").length == 11))
            {
                textInputLayout1.error = null
                //editText2.requestFocus()
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
        if ((binding.textInputEditText1.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText1.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText2.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText2.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText3.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText3.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText4.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText4.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText5.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText5.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText6.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText6.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText7.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText7.text!!.toString().replace(" ", "").length == 11))
//            && binding.textInputEditText8.text!!.toString().replace(" ", "").length == 12
            && (binding.textInputEditText9.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText9.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText10.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText10.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText11.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText11.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText12.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText12.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText13.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText13.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText14.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText14.text!!.toString().replace(" ", "").length == 11))
            && (binding.textInputEditText15.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText15.text!!.toString().replace(" ", "").length == 11))
//            && binding.textInputEditText16.text!!.toString().replace(" ", "").length == 12
            && (binding.textInputEditText17.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText17.text!!.toString().replace(" ", "").length == 11)))
        {
            isPartial = false
            return true

        }
        else if ((binding.textInputEditText1.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText1.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText2.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText2.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText3.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText3.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText4.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText4.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText5.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText5.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText6.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText6.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText7.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText7.text!!.toString().replace(" ", "").length == 11))
//            && binding.textInputEditText8.text!!.toString().replace(" ", "").length == 12
            || (binding.textInputEditText9.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText9.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText10.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText10.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText11.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText11.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText12.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText12.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText13.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText13.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText14.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText14.text!!.toString().replace(" ", "").length == 11))
            || (binding.textInputEditText15.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText15.text!!.toString().replace(" ", "").length == 11))
//            && binding.textInputEditText16.text!!.toString().replace(" ", "").length == 12
            || (binding.textInputEditText17.text!!.toString().replace(" ", "").length == 12 || (binding.textInputEditText17.text!!.toString().replace(" ", "").length == 11)))
        {
            isPartial = true
            return true
        }

        isPartial = null
        return false
    }

    private fun getBarcodeReadings(): MutableList<Map<String, String>> {
        val readings: MutableList<Map<String, String>> = mutableListOf()

        val reading1 = if ((binding.textInputEditText1.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText1.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText1.text.toString() else "NA"
        val reading2 = if ((binding.textInputEditText2.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText2.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText2.text.toString() else "NA"
        val reading3 = if ((binding.textInputEditText3.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText3.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText3.text.toString() else "NA"
        val reading4 = if ((binding.textInputEditText4.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText4.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText4.text.toString() else "NA"
        val reading5 = if ((binding.textInputEditText5.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText5.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText5.text.toString() else "NA"
        val reading6 = if ((binding.textInputEditText6.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText6.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText6.text.toString() else "NA"
        val reading7 = if ((binding.textInputEditText7.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText7.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText7.text.toString() else "NA"
        //val reading8 = if (binding.textInputEditText8.text!!.toString().replace(" ", "").length == 12) binding.textInputEditText8.text.toString() else "NA"
        val reading9 = if ((binding.textInputEditText9.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText9.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText9.text.toString() else "NA"
        val reading10 = if ((binding.textInputEditText10.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText10.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText10.text.toString() else "NA"
        val reading11 = if ((binding.textInputEditText11.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText11.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText11.text.toString() else "NA"
        val reading12 = if ((binding.textInputEditText12.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText12.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText12.text.toString() else "NA"
        val reading13 = if ((binding.textInputEditText13.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText13.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText13.text.toString() else "NA"
        val reading14 = if ((binding.textInputEditText14.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText14.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText14.text.toString() else "NA"
        val reading15 = if ((binding.textInputEditText15.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText15.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText15.text.toString() else "NA"
        //val reading16 = if (binding.textInputEditText16.text!!.toString().replace(" ", "").length == 12) binding.textInputEditText16.text.toString() else "NA"
        val reading17 = if ((binding.textInputEditText17.text!!.toString().replace(" ", "").length == 12) || (binding.textInputEditText17.text!!.toString().replace(" ", "").length == 11)) binding.textInputEditText17.text.toString() else "NA"

        val reading1Map = mutableMapOf<String, String>()
        reading1Map["id"] = "SCBR01"
        reading1Map["reading"] = getString(R.string.sample_reading1)
        reading1Map["value"] = reading1

        readings.add(reading1Map)

        val reading2Map = mutableMapOf<String, String>()
        reading2Map["id"] = "SCBR02"
        reading2Map["reading"] = getString(R.string.sample_reading2)
        reading2Map["value"] = reading2

        readings.add(reading2Map)

        val reading3Map = mutableMapOf<String, String>()
        reading3Map["id"] = "SCBR03"
        reading3Map["reading"] = getString(R.string.sample_reading3)
        reading3Map["value"] = reading3

        readings.add(reading3Map)

        val reading4Map = mutableMapOf<String, String>()
        reading4Map["id"] = "SCBR04"
        reading4Map["reading"] = getString(R.string.sample_reading4)
        reading4Map["value"] = reading4

        readings.add(reading4Map)

        val reading5Map = mutableMapOf<String, String>()
        reading5Map["id"] = "SCBR05"
        reading5Map["reading"] = getString(R.string.sample_reading5)
        reading5Map["value"] = reading5

        readings.add(reading5Map)

        val reading6Map = mutableMapOf<String, String>()
        reading6Map["id"] = "SCBR06"
        reading6Map["reading"] = getString(R.string.sample_reading6)
        reading6Map["value"] = reading6

        readings.add(reading6Map)

        val reading7Map = mutableMapOf<String, String>()
        reading7Map["id"] = "SCBR07"
        reading7Map["reading"] = getString(R.string.sample_reading7)
        reading7Map["value"] = reading7

        readings.add(reading7Map)

//        val reading8Map = mutableMapOf<String, String>()
//        reading8Map["id"] = "SCBR08"
//        reading8Map["reading"] = getString(R.string.sample_reading8)
//        reading8Map["value"] = reading8
//
//        readings.add(reading8Map)

        val reading9Map = mutableMapOf<String, String>()
        reading9Map["id"] = "SCBR08"
        reading9Map["reading"] = getString(R.string.sample_reading9)
        reading9Map["value"] = reading9

        readings.add(reading9Map)

        val reading10Map = mutableMapOf<String, String>()
        reading10Map["id"] = "SCBR09"
        reading10Map["reading"] = getString(R.string.sample_reading10)
        reading10Map["value"] = reading10

        readings.add(reading10Map)

        val reading11Map = mutableMapOf<String, String>()
        reading11Map["id"] = "SCBR10"
        reading11Map["reading"] = getString(R.string.sample_reading11)
        reading11Map["value"] = reading11

        readings.add(reading11Map)

        val reading12Map = mutableMapOf<String, String>()
        reading12Map["id"] = "SCBR11"
        reading12Map["reading"] = getString(R.string.sample_reading12)
        reading12Map["value"] = reading12

        readings.add(reading12Map)

        val reading13Map = mutableMapOf<String, String>()
        reading13Map["id"] = "SCBR12"
        reading13Map["reading"] = getString(R.string.sample_reading13)
        reading13Map["value"] = reading13

        readings.add(reading13Map)

        val reading14Map = mutableMapOf<String, String>()
        reading14Map["id"] = "SCBR13"
        reading14Map["reading"] = getString(R.string.sample_reading14)
        reading14Map["value"] = reading14

        readings.add(reading14Map)

        val reading15Map = mutableMapOf<String, String>()
        reading15Map["id"] = "SCBR14"
        reading15Map["reading"] = getString(R.string.sample_reading15)
        reading15Map["value"] = reading15

        readings.add(reading15Map)

//        val reading16Map = mutableMapOf<String, String>()
//        reading16Map["id"] = "SCBR16"
//        reading16Map["reading"] = getString(R.string.sample_reading16)
//        reading16Map["value"] = reading16
//
//        readings.add(reading16Map)

        val reading17Map = mutableMapOf<String, String>()
        reading17Map["id"] = "SCBR15"
        reading17Map["reading"] = getString(R.string.sample_reading17)
        reading17Map["value"] = reading17

        readings.add(reading17Map)

        return readings
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        var contraindications = mutableListOf<Map<String, String>>()

        val hadEye = sampleQuestionsViewModel.hadDermatitisEyes.value
        val haveNeck = sampleQuestionsViewModel.hadDermatitisNeck.value
        val haveElbow = sampleQuestionsViewModel.hadDermatitisElbow.value
        val haveFront = sampleQuestionsViewModel.hadDermatitisFrontKnees.value
        val haveBehind = sampleQuestionsViewModel.hadDermatitisBehindKnees.value

        var eyeMap = mutableMapOf<String, String>()
        eyeMap["id"] = getString(R.string.sample_question_id_1)
        eyeMap["question"] = getString(R.string.sample_question_1)
        eyeMap["answer"] = hadEye!!

        contraindications.add(eyeMap)

        var neckMap = mutableMapOf<String, String>()
        neckMap["id"] = getString(R.string.sample_question_id_2)
        neckMap["question"] = getString(R.string.sample_question_2)
        neckMap["answer"] = haveNeck!!

        contraindications.add(neckMap)

        var elbowMap = mutableMapOf<String, String>()
        elbowMap["id"] = getString(R.string.sample_question_id_3)
        elbowMap["question"] = getString(R.string.sample_question_3)
        elbowMap["answer"] = haveElbow!!

        contraindications.add(elbowMap)

        var frontMap = mutableMapOf<String, String>()
        frontMap["id"] = getString(R.string.sample_question_id_4)
        frontMap["question"] = getString(R.string.sample_question_4)
        frontMap["answer"] = haveFront!!

        contraindications.add(frontMap)

        var behindMap = mutableMapOf<String, String>()
        behindMap["id"] = getString(R.string.sample_question_id_5)
        behindMap["question"] = getString(R.string.sample_question_5)
        behindMap["answer"] = haveBehind!!

        contraindications.add(behindMap)

        return contraindications
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    private fun convertTimeTo24Hours(): String
    {
        val now: Calendar = Calendar.getInstance()
        val inputFormat: DateFormat = SimpleDateFormat("MMM DD, yyyy HH:mm:ss")
        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        val date: Date
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
        val date: Date
        val output: String
        try{
            date= inputFormat.parse(binding.root.getLocalTimeString())
            output = outputformat.format(date)

            return output
        }catch(p: ParseException){
            return ""
        }
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
