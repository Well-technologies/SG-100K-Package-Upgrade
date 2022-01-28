package org.singapore.ghru.ui.treadmill.aftertest

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import com.google.gson.GsonBuilder

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.TreadmillAfterTestFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.treadmill.completed.CompletedDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.TreadmillBody
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TreadmillAfterTestFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<TreadmillAfterTestFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: TreadmillAfterTestViewModel

    private var participant: ParticipantRequest? = null
    private var treadmillBody: TreadmillBody? = null
    private var rangeList: ArrayList<String> = arrayListOf()
    private var selectedRange: String? = null
    private var selectedBarRange: String? = null
    private var barRangeList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participant = arguments?.getParcelable<ParticipantRequest>("participant")!!
            treadmillBody = arguments?.getParcelable<TreadmillBody>("treadmillBody")!!
        } catch (e: KotlinNullPointerException) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<TreadmillAfterTestFragmentBinding>(
            inflater,
            R.layout.treadmill_after_test_fragment,
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
        binding.setLifecycleOwner(this)
        binding.participant = participant
        binding.viewModel = viewModel

        binding.previousButton.singleClick {
            navController().popBackStack()
        }

        viewModel.treadmillComplete?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                val completedDialogFragment = CompletedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if (participant?.status == Status.ERROR) {
                Crashlytics.setString("participant", participant.toString())
                Crashlytics.logException(Exception("treadmillComplete " + participant.message.toString()))
                binding.executePendingBindings()
            }
        })

        Log.d("AFTER_TEST_FRAG", "ONLOAD_META: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

        binding.nextButton.singleClick {
            if(selectedRange==null)
            {
                binding.textViewRangeError.visibility = View.VISIBLE
            }
            else if(selectedBarRange == null)
            {
                binding.barTextViewRangeError.visibility = View.VISIBLE
            }
            else
            {
                if (validateNextButton()) {

                    Log.d("AFTER_TEST_FRAG", "BEFORE_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                    val endTime: String = convertTimeTo24Hours()
                    val endDate: String = getDate()
                    val endDateTime:String = endDate + " " + endTime

                    participant?.meta?.endTime =  endDateTime

                    Log.d("AFTER_TEST_FRAG", "AFTER_ASSIGN: " + participant?.meta + " END_TIME: " + participant?.meta?.endTime)

                    val gson = GsonBuilder().setPrettyPrinting().create()
                    treadmillBody?.after_test = getAfterTestQuestions()
                    viewModel.setParticipantComplete(
                        participant!!, isNetworkAvailable(),
                        gson.toJson(treadmillBody)
                    )
                }
            }

        }

//        viewModel.rating?.observe(
//            this,
//            Observer { rating ->
//                validateRating(rating)
//                validateNextButton()
//            })

        binding.radioGroupChest.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noChest) {
                binding.radioGroupChestValue = false
                viewModel.setChestPain(false)

            } else {
                binding.radioGroupChestValue = false
                viewModel.setChestPain(true)

            }
            binding.executePendingBindings()
        }
        binding.radioGroupBreath.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId == R.id.noBreath) {
                binding.radioGroupBreathlessValue = false
                viewModel.setBreathless(false)

            } else {
                binding.radioGroupBreathlessValue = false
                viewModel.setBreathless(true)

            }
            binding.executePendingBindings()
        }
//        binding.radioGroupBar.setOnCheckedChangeListener { radioGroup, i ->
//            if (radioGroup.checkedRadioButtonId == R.id.noBar) {
//                binding.radioGroupBarValue = false
//                viewModel.setFrontBar(false)
//
//            } else {
//                binding.radioGroupBarValue = false
//                viewModel.setFrontBar(true)
//
//            }
//            binding.executePendingBindings()
//        }

        rangeList.clear()
        rangeList.add(getString(R.string.unknown_1))
        rangeList.add("6")
        rangeList.add("7")
        rangeList.add("8")
        rangeList.add("9")
        rangeList.add("10")
        rangeList.add("11")
        rangeList.add("12")
        rangeList.add("13")
        rangeList.add("14")
        rangeList.add("15")
        rangeList.add("16")
        rangeList.add("17")
        rangeList.add("18")
        rangeList.add("19")
        rangeList.add("20")
        val adapter = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, rangeList)
        binding.rangeSpinner.setAdapter(adapter)

        binding.rangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedRange = null
                } else {
                    binding.textViewRangeError.visibility = View.GONE
                    selectedRange = binding.rangeSpinner.selectedItem.toString()
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        barRangeList.clear()
        barRangeList.add(getString(R.string.unknown_1))
        barRangeList.add("1 - not at all")
        barRangeList.add("2")
        barRangeList.add("3")
        barRangeList.add("4")
        barRangeList.add("5 - all the time")
        val adapter1 = ArrayAdapter(context!!, R.layout.basic_spinner_dropdown_item, barRangeList)
        binding.barRangeSpinner.setAdapter(adapter1)

        binding.barRangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, @NonNull selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedBarRange = null
                } else {
                    binding.barTextViewRangeError.visibility = View.GONE
                    selectedBarRange = binding.barRangeSpinner.selectedItem.toString()
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }
    }

    private fun validateRating(rating: String) {
        try {
            val ratingVal: Double = rating.toDouble()
            if (ratingVal >= 6 && ratingVal <= 20) {
                binding.ratingInputLayout.error = null
                viewModel.isValidRating = true

            } else {
                viewModel.isValidRating = false
                binding.ratingInputLayout.error = getString(R.string.error_not_in_range)
            }

        } catch (e: Exception) {
            viewModel.isValidRating = false
            binding.ratingInputLayout.error = getString(R.string.error_invalid_input)
        }
    }

    private fun validateNextButton(): Boolean {
//        if(viewModel.rating?.value.isNullOrBlank() || !viewModel.isValidRating) {
//            return false
//        }
        if(viewModel.chestPain.value == null) {
            binding.radioGroupChestValue = true
            binding.executePendingBindings()
            return false
        }
        if(viewModel.breathless.value == null) {
            binding.radioGroupBreathlessValue = true
            binding.executePendingBindings()
            return false
        }
//        if(viewModel.frontBar.value == null) {
//            binding.radioGroupBarValue = true
//            binding.executePendingBindings()
//            return false
//        }

        return true
    }

    private fun getAfterTestQuestions(): MutableList<Map<String, String>> {
        var afterTest = mutableListOf<Map<String, String>>()

        //val rating = viewModel.rating.value
        val rating = selectedRange
        val chestPain = viewModel.chestPain.value
        val breathless = viewModel.breathless.value
//        val frontBar = viewModel.frontBar.value
        val frontBar = selectedBarRange

        var ratingMap = mutableMapOf<String, String>()
        ratingMap["id"] = "TMSQ1"
        ratingMap["question"] = getString(R.string.treadmill_rating_question)
        ratingMap["answer"] = rating!!

        afterTest.add(ratingMap)

        var chestPainMap = mutableMapOf<String, String>()
        chestPainMap["id"] = "TMSQ2"
        chestPainMap["question"] = getString(R.string.treadmill_after_chest_pain_question)
        chestPainMap["answer"] = if (chestPain!!) "yes" else "no"

        afterTest.add(chestPainMap)

        var breathMap = mutableMapOf<String, String>()
        breathMap["id"] = "TMSQ3"
        breathMap["question"] = getString(R.string.treadmill_after_breathless_question)
        breathMap["answer"] = if (breathless!!) "yes" else "no"

        afterTest.add(breathMap)

        var barMap = mutableMapOf<String, String>()
        barMap["id"] = "TMSQ4"
        barMap["question"] = getString(R.string.treadmill_front_bar_question)
        barMap["answer"] = frontBar!!

        afterTest.add(barMap)

        return afterTest
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
