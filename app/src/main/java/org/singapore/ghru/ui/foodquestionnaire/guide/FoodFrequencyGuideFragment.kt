package org.singapore.ghru.ui.foodquestionnaire.guide

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
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
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getSystemService
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
import org.singapore.ghru.databinding.FfqGuideFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.BusProvider
import org.singapore.ghru.ui.cognition.completed.StartedDialogFragment
import org.singapore.ghru.ui.foodquestionnaire.contraindication.FoodFrequencyQuestionnaireViewModel
import org.singapore.ghru.ui.foodquestionnaire.stageonereason.ReasonDialogFragmentNew
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

class FoodFrequencyGuideFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<FfqGuideFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var viewModel: FoodFrequencyGuideViewModel

    private var participantRequest: ParticipantRequest? = null

    private var participantData: ParticipantCre? = null

    //private lateinit var questionnaireViewModel: FoodFrequencyQuestionnaireViewModel

    private var myUsername : ClipboardManager? = null
    private var mypassword : ClipboardManager? = null

    private var ffqData: FfqDataNew1? = null

    //var meta: Meta? = null
    //var user: User? = null

    private var language : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            participantRequest = arguments?.getParcelable<ParticipantRequest>("ParticipantRequest")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        try {
            language = arguments?.getString("Language")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<FfqGuideFragmentBinding>(
            inflater,
            R.layout.ffq_guide_fragment,
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewModel = activity?.run {
//            ViewModelProviders.of(this).get(FoodFrequencyGuideViewModel::class.java)
//        } ?: throw Exception("Invalid Activity")
//    }

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

//        binding.nextButton.singleClick {
//            if (validateNextButton()) {
//                val bundle = Bundle()
//                bundle.putParcelable("ParticipantRequest", participantRequest)
//                navController().navigate(R.id.action_FFQ_GuideFragment_to_ConfirmationFragment, bundle)
//            }
//        }

        binding.usernameCopyButton.singleClick {

            copyId()
        }

        binding.passwordCopyButton.singleClick {

            copyPassword()
        }

        binding.refreshButton.singleClick {

//            viewModel.setScreeningId("AAA")
//
//            viewModel.getParticipantCredintials.observe(this, Observer { participantResource ->
//
//                if (participantResource?.status == Status.SUCCESS) {
//                    participantData = participantResource.data!!.data!!
//
//                    if (participantData != null)
//                    {
//                        binding.ffqLoginId.setText(participantData!!.username)
//                        binding.ffqPassword.setText(participantData!!.password)
//                        viewModel.setHaveCredintials(true)
//                    }
//                } else if (participantResource?.status == Status.ERROR) {
//                    val errorDialogFragment = ErrorDialogFragment()
//                    errorDialogFragment.setErrorMessage(participantResource.message!!.data!!.message!!)
//                    errorDialogFragment.show(fragmentManager!!)
//                    //Crashlytics.logException(Exception(participantResource.toString()))
//                }
//                binding.executePendingBindings()
//
//
//            })
//
//            viewModel.setScreeningId(participantRequest!!.screeningId)

            viewModel.setLanguageId("AAA" ,"aaaa")

            viewModel.getLanguage!!.observe(this, Observer { participantResource ->

                if (participantResource?.status == Status.SUCCESS) {
                    participantData = participantResource.data!!.data!!

                    if (participantData != null)
                    {
                        binding.ffqLoginId.setText(participantData!!.username)
                        binding.ffqPassword.setText(participantData!!.password)
                        viewModel.setHaveCredintials(true)
                    }
                } else if (participantResource?.status == Status.ERROR) {
                    val errorDialogFragment = ErrorDialogFragment()
                    errorDialogFragment.setErrorMessage(participantResource.message!!.data!!.message!!)
                    errorDialogFragment.show(fragmentManager!!)
                    //Crashlytics.logException(Exception(participantResource.toString()))
                }
                binding.executePendingBindings()


            })

            if (language != null)
            {
                viewModel.setLanguageId(participantRequest!!.screeningId, language!!)
            }
            else
            {
                viewModel.setLanguageId(participantRequest!!.screeningId, "NA")
            }


        }

//        viewModel.setScreeningId(participantRequest!!.screeningId)
//
//        viewModel.getParticipantCredintials.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantData = participantResource.data!!.data!!
//
//                if (participantData != null)
//                {
//                    binding.ffqLoginId.setText(participantData!!.username)
//                    binding.ffqPassword.setText(participantData!!.password)
//                    viewModel.setHaveCredintials(true)
//                }
//            } else if (participantResource?.status == Status.ERROR) {
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage(participantResource.message!!.data!!.message!!)
//                errorDialogFragment.show(fragmentManager!!)
//                //Crashlytics.logException(Exception(participantResource.toString()))
//            }
//            binding.executePendingBindings()
//        })

        //viewModel.setLanguageId(participantRequest!!.screeningId, "NA")

        Log.d("FFQ_GUIDE_FRAG", "DATA: " + language)

        if (language != null)
        {
            Log.d("FFQ_GUIDE_FRAG", "DATA_1: " + language)
            viewModel.setLanguageId(participantRequest!!.screeningId, language!!)
        }
        else
        {
            Log.d("FFQ_GUIDE_FRAG", "DATA_2: " + language)
            viewModel.setLanguageId(participantRequest!!.screeningId, "NA")
        }

        viewModel.getLanguage!!.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                participantData = participantResource.data!!.data!!

                if (participantData != null)
                {
                    binding.ffqLoginId.setText(participantData!!.username)
                    // generate barcode and return it as a bitmap
                    createUsernameBarcode(participantData!!.username!!)
                    //createUsernameBarcode("123456DFGHJYUTE124594NJDVKLJMN")

                    binding.ffqPassword.setText(participantData!!.password)
                    createPasswordBarcode(participantData!!.password!!)
                    //createPasswordBarcode("123456DFGHJYUTE124594NJDVKLJMN")
                    viewModel.setHaveCredintials(true)
                }
            } else if (participantResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message!!.data!!.message!!)
                errorDialogFragment.show(fragmentManager!!)
                //Crashlytics.logException(Exception(participantResource.toString()))
            }
            binding.executePendingBindings()
        })

        viewModel.ffqPostComplete?.observe(this, Observer { ffqPocess ->

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
                    FFQRequestNew1(meta = participantRequest?.meta, body = ffqData).toString()
                )
                Crashlytics.setString("participant", participantRequest.toString())
                Crashlytics.logException(Exception("BodyMeasurementMeta " + ffqPocess.message.toString()))
            }
        })

        Log.d("GUIDE_FRAG", "ONLOAD_META: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

        binding.nextButton.singleClick {
            if (validateNextButton()) {

                ffqData = FfqDataNew1(
                    status = "1"
                )
                //ffqData!!.contraindications = getContraindications()

                Log.d("GUIDE_FRAG", "BEFORE_ASSING: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val endTime: String = convertTimeTo24Hours()
                val endDate: String = getDate()
                val endDateTime:String = endDate + " " + endTime

                participantRequest?.meta?.endTime = endDateTime

                Log.d("GUIDE_FRAG", "AFTER_ASSIGN: " + participantRequest?.meta + " END_TIME: " + participantRequest?.meta?.endTime)

                val ffqRequest = FFQRequestNew1(meta = participantRequest?.meta, body = ffqData)
                ffqRequest.screeningId = participantRequest?.screeningId!!
                if(isNetworkAvailable()){
                    ffqRequest.syncPending =false
                }else{
                    ffqRequest.syncPending =true

                }

                Log.d("FFQ_CONFIRAMTION", "DATA:" + ffqRequest)

                viewModel.setPostFFQ(ffqRequest, participantRequest!!.screeningId)

                binding.progressBar.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE

            } else {

                binding.executePendingBindings()

            }
//                viewModel.setPostFFQ(ffqRequest, participantRequest!!.screeningId)
//
//                binding.progressBar.visibility = View.VISIBLE
//                binding.nextButton.visibility = View.GONE


        }

        binding.skipButton.singleClick {
            val reasonDialogFragment = ReasonDialogFragmentNew()
            reasonDialogFragment.arguments = bundleOf("participant" to participantRequest)
            reasonDialogFragment.show(fragmentManager!!)
        }
    }

    private fun validateNextButton(): Boolean {
        if(viewModel.haveCredentials.value == null) {
            binding.isCredentialsValue = true
            binding.executePendingBindings()
            return false
        }

        return true
    }

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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        questionnaireViewModel = activity?.run {
//            ViewModelProviders.of(this).get(FoodFrequencyQuestionnaireViewModel::class.java)
//        } ?: throw Exception("Invalid Activity")
//    }

//    private fun getContraindications(): MutableList<Map<String, String>> {
//        val contraindications = mutableListOf<Map<String, String>>()
//
//        val haveCommunicate = questionnaireViewModel.haveCommunicate.value
//        val communicationMap = mutableMapOf<String, String>()
//        communicationMap["id"] = "FFCI1"
//        communicationMap["question"] = getString(R.string.ffq_question)
//        communicationMap["answer"] = if (haveCommunicate!!) "yes" else "no"
//
//        contraindications.add(communicationMap)
//
//        return contraindications
//    }

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

    private fun copyId(){

        if (!binding.ffqLoginId.text.toString().equals(""))
        {
            var clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("label", binding.ffqLoginId.text.toString())
            clipboard.primaryClip = clip
            Toast.makeText(activity!!, "Login id copied to clip board", Toast.LENGTH_LONG).show()
        }
    }

    private fun copyPassword(){

        if (!binding.ffqPassword.text.toString().equals(""))
        {
            var clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clip = ClipData.newPlainText("label", binding.ffqPassword.text.toString())
            clipboard.primaryClip = clip

            Toast.makeText(activity!!, "Password copied to clip board", Toast.LENGTH_LONG).show()
        }
    }

    private fun createUsernameBarcode(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.barcode_width)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.barcode_height)

        binding.usernameBarcode.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = getColor(activity!!, R.color.black),
                backgroundColor = getColor(activity!!, android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )

        binding.ffqLoginId.setText(value)
    }

    private fun createPasswordBarcode(value: String) {
        val widthPixels = resources.getDimensionPixelSize(R.dimen.barcode_width)
        val heightPixels = resources.getDimensionPixelSize(R.dimen.barcode_height)

        binding.passwordBarcode.setImageBitmap(
            createBarcodeBitmap(
                barcodeValue = value,
                barcodeColor = getColor(activity!!, R.color.black),
                backgroundColor = getColor(activity!!, android.R.color.white),
                widthPixels = widthPixels,
                heightPixels = heightPixels
            )
        )

        binding.ffqPassword.setText(value)
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
