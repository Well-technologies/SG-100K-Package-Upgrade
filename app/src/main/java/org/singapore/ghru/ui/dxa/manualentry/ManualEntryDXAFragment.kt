package org.singapore.ghru.ui.dxa.manualentry

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import io.reactivex.disposables.CompositeDisposable

import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.ManualEntryBodyMeasurementFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.event.StationCheckRxBus
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.dxa.noheight.NoHeightDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.util.*
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.Status
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.vo.request.BodyMeasurementMetaNew
import javax.inject.Inject

class ManualEntryDXAFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<ManualEntryBodyMeasurementFragmentBinding>()

    @Inject
    lateinit var viewModel: ManualEntryDXAViewModel

    private val disposables = CompositeDisposable()

    private var participantRequest: ParticipantRequest? = null

    var meta: Meta? = null
    var bodyMeasurementMeta: BodyMeasurementMetaNew? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            meta = arguments?.getParcelable<Meta>("meta")!!
        } catch (e: KotlinNullPointerException) {
            //Crashlytics.logException(e)
        }

        disposables.add(
            StationCheckRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    val bundle = bundleOf("ParticipantRequest" to participantRequest, "BodyMeasurementData" to bodyMeasurementMeta)
                    findNavController().navigate(R.id.action_manualScanFragment_to_dxaContraFragment, bundle)
                }, { error ->
                    print(error)
                    error.printStackTrace()
                })
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ManualEntryBodyMeasurementFragmentBinding>(
            inflater,
            R.layout.manual_entry_body_measurement_fragment,
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
            binding.root.hideKeyboard()
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

        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        viewModel.participant.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantRequest = participantResource.data?.data
//                participantRequest?.meta = meta
//                if (!participantResource.data?.stationStatus!!) {
//                    val bundle = bundleOf("ParticipantRequest" to participantResource.data?.data)
//                    findNavController().navigate(R.id.action_manualScanFragment_to_dxaContraFragment, bundle)
//                } else {
//                    val stationCheckDialogFragment = StationCheckDialogFragment()
//                    stationCheckDialogFragment.show(fragmentManager!!)
//                }
//            } else if (participantResource?.status == Status.ERROR) {
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
//                errorDialogFragment.show(fragmentManager!!)
//                //Crashlytics.logException(Exception(participantResource.toString()))
//            }
//            binding.executePendingBindings()
//        })

        viewModel.participant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS) {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta

                if (!participantResource.data?.stationStatus!!)
                {
                    // need to add height and weight data
                    val bundle = bundleOf("ParticipantRequest" to participantResource.data?.data, "BodyMeasurementData" to bodyMeasurementMeta)
                    findNavController().navigate(R.id.action_manualScanFragment_to_dxaContraFragment, bundle)
                }
                else
                {
                    val stationCheckDialogFragment = StationCheckDialogFragment()
                    stationCheckDialogFragment.show(fragmentManager!!)
                }
            } else if (participantResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
                //Crashlytics.logException(Exception(participantResource.toString()))
            }
            binding.executePendingBindings()
        })

//        viewModel.participantOffline?.observe(this, Observer { participantResource ->
//
//            if (participantResource?.status == Status.SUCCESS) {
//                participantRequest = participantResource.data
//                participantRequest?.meta = meta
//                val bundle = bundleOf("ParticipantRequest" to participantRequest)
//                findNavController().navigate(R.id.action_manualScanFragment_to_dxaContraFragment, bundle)
//            } else if (participantResource?.status == Status.ERROR) {
//                val errorDialogFragment = ErrorDialogFragment()
//                errorDialogFragment.setErrorMessage("The Paticipant ID is not found")
//                errorDialogFragment.show(fragmentManager!!)
//                //Crashlytics.logException(Exception(participantResource.toString()))
//            }
//            binding.executePendingBindings()
//        })

        binding.editTextCode.filters = binding.editTextCode.filters + InputFilter.AllCaps()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
        binding.editTextCode.requestFocus()

        viewModel.checkoutParticipant.observe(this, Observer { participantResource ->

            if (participantResource?.status == Status.SUCCESS)
            {
                participantRequest = participantResource.data?.data
                participantRequest?.meta = meta
                if (!participantResource.data?.stationStatus!!)
                {
                    // need to check height and weight before dxa
                    //viewModel.setScreeningId(binding.editTextCode.text.toString())
                    viewModel.setParticipantToGetHeightAndWeight(participantRequest!!, true)
                }
                else
                {
                    if (participantResource.data.is_cancelled == 1)
                    {
                        // need to check height and weight
                        //viewModel.setScreeningId(binding.editTextCode.text.toString())
                        viewModel.setParticipantToGetHeightAndWeight(participantRequest!!, true)
                    }
                    else
                    {
                        val stationCheckDialogFragment = CheckoutCheckDialogFragment()
                        stationCheckDialogFragment.show(fragmentManager!!)
                    }
                }
            } else if (participantResource?.status == Status.ERROR) {
                val errorDialogFragment = ErrorDialogFragment()
                errorDialogFragment.setErrorMessage(participantResource.message?.message!!)
                errorDialogFragment.show(fragmentManager!!)
            }
            binding.executePendingBindings()
        })

        viewModel.getHeightAndWeight?.observe(this, Observer { participant ->

            if (participant?.status == Status.SUCCESS) {
                bodyMeasurementMeta = participant.data!!.data!!.station
                if (bodyMeasurementMeta!!.isCancelled == 0)
                {
                    if (bodyMeasurementMeta!!.body!!.height!!.value != null && bodyMeasurementMeta!!.body!!.bodyComposition!!.value != null)
                    {
                        Log.d("DXA_MANUAL_FRAGMENT", "BODY_MEASUREMENT_META_HEIGHT: SUCCESS")
                        viewModel.setScreeningId(binding.editTextCode.text.toString())
                    }
                    else
                    {
                        val noHeightDialogFragment = NoHeightDialogFragment()
                        noHeightDialogFragment.show(fragmentManager!!)
                    }
                }
                else
                {
                    val noHeightDialogFragment = NoHeightDialogFragment()
                    noHeightDialogFragment.show(fragmentManager!!)
                    Log.d("DXA_MANUAL_FRAGMENT", "BODY_MEASUREMENT_META_HEIGHT: FAILED")
                    // need to implement error dialog for height and weight
                }

                Log.d("DXA_MANUAL_FRAGMENT", "BODY_MEASUREMENT_META_DATA: Success")
            }
            else if (participant?.status == Status.ERROR)
            {
                // need to implement error dialog for height and weight
                val noHeightDialogFragment = NoHeightDialogFragment()
                noHeightDialogFragment.show(fragmentManager!!)
                Log.d("DXA_MANUAL_FRAGMENT", "BODY_MEASUREMENT_META_DATA: Failed")
            }

        })
    }

    fun handleContinue() {
        val checkSum = validateChecksum(binding.editTextCode.text.toString(), Constants.TYPE_PARTICIPANT)
        if (!checkSum.error) {
            activity?.runOnUiThread {

                if (isNetworkAvailable()) {
                    //viewModel.setScreeningId(binding.editTextCode.text.toString())
                    viewModel.setCheckoutScreeningId(binding.editTextCode.text.toString())
                } else {
                    //viewModel.setScreeningIdOffline(binding.editTextCode.text.toString())
                }
            }
        } else {

            binding.textLayoutCode.error = getString(R.string.invalid_code) //checkSum.message
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
        return when (item.itemId) {
            android.R.id.home -> {
                binding.root.hideKeyboard()
                navController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun navController() = findNavController()
}
