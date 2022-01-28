package org.singapore.ghru.ui.cognition.guide

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.crashlytics.android.Crashlytics
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.CognitionGuideFragmentBinding
import org.singapore.ghru.databinding.FfqGuideFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.ui.cognition.completed.StartedDialogFragment
import org.singapore.ghru.ui.cognition.contraindication.CognitionQuestionnaireViewModel
import org.singapore.ghru.ui.cognition.reason.ReasonDialogFragment
import org.singapore.ghru.ui.cognition.stageonereason.ReasonDialogFragmentNew
import org.singapore.ghru.ui.foodquestionnaire.contraindication.FoodFrequencyQuestionnaireViewModel
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.getLocalTimeString
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject

class CognitionGuideFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<CognitionGuideFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: CognitionGuideViewModel

    private var participantRequest: ParticipantRequest? = null

    private var participantData: ParticipantCre? = null

    private lateinit var questionnaireViewModel: CognitionQuestionnaireViewModel

    private var cogData: CognitionDataNew1? = null

    //var meta: Meta? = null
    //var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<CognitionGuideFragmentBinding>(
            inflater,
            R.layout.cognition_guide_fragment,
            container,
            false
        )
        binding = dataBinding

        //setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.participant = participantRequest
        binding.setLifecycleOwner(this)

//        viewModel.setUser("user")
//        viewModel.user?.observe(this, Observer {
//
//                userData ->
//            if (userData?.data != null) {
//                // setupNavigationDrawer(userData.data)
//                user = userData.data
//
//                val sTime: String = convertTimeTo24Hours()
//                val sDate: String = getDate()
//                val sDateTime:String = sDate + " " + sTime
//
//                meta = Meta(collectedBy = user?.id, startTime = sDateTime)
//                //meta?.registeredBy = user?.id
//            }
//        })

        var id  = participantRequest!!.screeningId
        val re = Regex("[^A-Za-z0-9 ]")
        id = re.replace(id, "")
        val num_id = id.filter { it.isDigit() }
        binding.textViewPleaseMsgID.setText(num_id)

        if (!num_id.equals(""))
        {
            createPasswordBarcode(num_id)
        }

        //viewModel.setScreeningId(participantRequest!!.screeningId)

        Log.d("GUIDE_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        binding.nextButton.singleClick {
//            if (validateNextButton()) {

                cogData = CognitionDataNew1(
                    status = "1"
                )
                cogData!!.contraindications = getContraindications()

            Log.d("GUIDE_FRAG", "BEFORE_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

            Log.d("GUIDE_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val cogRequest = CognitionRequestNew1(meta = participantRequest?.meta, body = cogData)
                cogRequest.screeningId = participantRequest?.screeningId!!
                if(isNetworkAvailable()){
                    cogRequest.syncPending =false
                }else{
                    cogRequest.syncPending =true

                }

                Log.d("FFQ_CONFIRAMTION", "DATA:" + cogRequest)

                viewModel.setPostCog(cogRequest, participantRequest!!.screeningId)

                binding.progressBar.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE

//            } else {
//
//                binding.executePendingBindings()
//
//            }
        }

        viewModel.cogPostComplete?.observe(this, Observer { ffqPocess ->

            if(ffqPocess?.status == Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.nextButton.visibility = View.VISIBLE
            }

            if (ffqPocess?.status == Status.SUCCESS) {
                val completedDialogFragment = StartedDialogFragment()
                completedDialogFragment.arguments = bundleOf("is_cancel" to false)
                completedDialogFragment.show(fragmentManager!!)
            } else if(ffqPocess?.status == Status.ERROR){
                Crashlytics.setString(
                    "FFQMeasurementMeta",
                    CognitionRequestNew1(meta = participantRequest?.meta, body = cogData).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + ffqPocess.message.toString()))
            }
        })

        binding.skipButton.singleClick {
            val reasonDialogFragment = ReasonDialogFragmentNew()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest, "contraindications" to getContraindications())
            reasonDialogFragment.show(fragmentManager!!)
        }
    }

//    private fun validateNextButton(): Boolean {
//        if(viewModel.haveCredentials.value == null) {
//            //binding.isCredentialsValue = true
//            binding.executePendingBindings()
//            return false
//        }
//
//        return true
//    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.hideKeyboard()
                navController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionnaireViewModel = activity?.run {
            ViewModelProviders.of(this).get(CognitionQuestionnaireViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    private fun getContraindications(): MutableList<Map<String, String>> {
        val contraindications = mutableListOf<Map<String, String>>()

        val haveCommunicate = questionnaireViewModel.haveAbleToClick.value
        val communicationMap = mutableMapOf<String, String>()
        communicationMap["id"] = "COCI1"
        communicationMap["question"] = getString(R.string.cognition_question)
        communicationMap["answer"] = if (haveCommunicate!!) "yes" else "no"

        contraindications.add(communicationMap)

        return contraindications
    }

    override fun onResume() {
        super.onResume()
        BusProvider.getInstance().register(this)
    }

    override fun onPause() {
        super.onPause()
        BusProvider.getInstance().unregister(this)
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

    private fun createPasswordBarcode(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.barcode_width)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.barcode_height)

        binding.barcodeImageView.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = ContextCompat.getColor(activity!!, R.color.black),
                backgroundColor = ContextCompat.getColor(activity!!, android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )
    }

    private fun createBarcodeBitmap(
        barcodeValue: String,
        @ColorInt barcodeColor: Int,
        @ColorInt backgroundColor: Int,
        widthPixels: Int,
        heightPixels: Int
    ): Bitmap {
        val bitMatrix = Code128Writer().encode(
            barcodeValue,
            BarcodeFormat.CODE_128,
            widthPixels,
            heightPixels
        )

        val pixels = IntArray(bitMatrix.width * bitMatrix.height)
        for (y in 0 until bitMatrix.height) {
            val offset = y * bitMatrix.width
            for (x in 0 until bitMatrix.width) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) barcodeColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(
            bitMatrix.width,
            bitMatrix.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.setPixels(
            pixels,
            0,
            bitMatrix.width,
            0,
            0,
            bitMatrix.width,
            bitMatrix.height
        )
        return bitmap
    }

}
