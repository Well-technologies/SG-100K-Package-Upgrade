package org.singapore.ghru.ui.checkout.selectedparticipant

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.*
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.AllParticipantsFragmentBinding
import org.singapore.ghru.databinding.AllStationsFragmentBinding
import org.singapore.ghru.databinding.HomeFragmentBinding
import org.singapore.ghru.databinding.SelectedParticipantFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.checkout.notcomplete.NotCompleteDialogFragment
import org.singapore.ghru.ui.checkout.notstarted.NotStartedDialogFragment
//import org.singapore.ghru.network.ConnectivityReceiver
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.setTitleColor
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import timber.log.Timber
import javax.inject.Inject


class SelectedParticipantFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<SelectedParticipantFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeViewModel: SelectedParticipantViewModel

    @Inject
    lateinit var  jobManager: JobManager

    private var ParticipantStationsItem: ParticipantStationsItem? = null

    private var participantRequest: ParticipantRequest? = null

    private var isAllStationsCompleted: Boolean = false
    private var overallStatus: String? = ""
    //var meta: Meta? = null
    //var user: User? = null

    private var HEIGHT_Status: String? = "Not started"
    private var HIP_Status: String? = "Not started"
    private var GRIP_Status: String? = "Not started"
    private var DXA_Status: String? = "Not started"
    private var ULTRA_Status: String? = "Not started"
    private var ACUITY_Status: String? = "Not started"
    private var TRED_Status: String? = "Not started"
    private var FFQ_Status: String? = "Not started"
    private var COG_Status: String? = "Not started"
    private var BP_Status: String? = "Not started"
    private var SP_Status: String? = "Not started"
    private var SAM_Status: String? = "Not started"
    private var PA_QU_Status: String? = "Not started"
    private var ECG_Status: String? = "Not started"
    private var FUN_Status: String? = "Not started"
    private var REG_Status: String? = "Not started"
    private var ST_QU_Status: String? = "Not started"
    private var AX_Status: String? = "Not started"
    private var CHK_Status: String? = "Not started"
    private var VIC_Status: String? = "Not started"
    private var SKI_Status: String? = "Not started"
    private var OCT_Status: String? = "Not started"

    private var isHeight: Boolean? = false
    private var isHip: Boolean? = false
    private var isGrip: Boolean? = false
    private var isDxa: Boolean? = false
    private var isUltra: Boolean? = false
    private var isVisual: Boolean? = false
    private var isTread: Boolean? = false
    private var isFFQ: Boolean? = false
    private var isCog: Boolean? = false
    private var isBp: Boolean? = false
    private var isSpiro: Boolean? = false
    private var isSample: Boolean? = false
    private var isSelf: Boolean? = false
    private var isEcg: Boolean? = false
    private var isFundo: Boolean? = false
    private var isReg: Boolean? = false
    private var isStaff: Boolean? = false
    private var isAxivity: Boolean? = false
    private var isCheckout: Boolean? = false
    private var isVic: Boolean? = false
    private var isSkin: Boolean? = false
    private var isOcta: Boolean? = false

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
        val dataBinding = DataBindingUtil.inflate<SelectedParticipantFragmentBinding>(
            inflater,
            R.layout.selected_participant_fragment,
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        jobManager.stop()
        binding.homeViewModel = homeViewModel

        if (participantRequest != null)
        {
            binding.participantId.setText(participantRequest!!.screeningId)
        }

        binding.buttonCancel.singleClick {

            activity?.finish()
        }

        binding.progressBar.visibility = View.VISIBLE
        homeViewModel.setParticipantId(participant = participantRequest!!.screeningId!!)

        homeViewModel.getSingleParticipantStations?.observe(activity!!, Observer {

            if (isNetworkAvailable())
            {
                if (it.status.equals(Status.SUCCESS))
                {
                    val stationList: ArrayList<ParticipantStation> = ArrayList<ParticipantStation>()

                    it.data!!.data!!.stations?.forEach { stations ->

                        stationList.add(stations)
                    }

                    isAllStationsCompleted = findOverallStatusStatus(stationList)
                    binding.progressBar.visibility = View.GONE

                    Log.d("SELECTED_PARTICIPANT", "STATUS: " + isAllStationsCompleted)

                }
                else if (it.status == Status.ERROR)
                {
                    Log.d("SELECTED_PARTICIPANT", "ERROR: " + it.status)
                }
            }
            else
            {
                Toast.makeText(activity, "Check internet connection", Toast.LENGTH_LONG).show()
            }
        })

        binding.buttonSubmit.singleClick {

            if (isAllStationsCompleted) // confirmation message and continue
            {
                if (participantRequest != null)
                {
//                    val bundle = bundleOf("ParticipantRequest" to participantRequest)
//                    findNavController().navigate(R.id.action_selectedParticipantFragment_to_CheckoutCompletionFragment, bundle)

                    val notCompleteFragment = NotCompleteDialogFragment()
                    notCompleteFragment.arguments = bundleOf("ParticipantRequest" to participantRequest)
                    notCompleteFragment.show(fragmentManager!!)

                }
            }
            else // not started message and exit
            {
                val notStartedFragment = NotStartedDialogFragment()
                //notStartedFragment.arguments = bundleOf("ParticipantRequest" to participantRequest)
                notStartedFragment.show(fragmentManager!!)
            }
        }

    }

    private fun findOverallStatusStatus(stationList: ArrayList<ParticipantStation>): Boolean
    {
        for (station in stationList)
        {
            if (station.station_name == "Height and Weight")
            {
                if (station.isCancelled == 1)
                {
                    HEIGHT_Status= "Canceled"
                    binding.heightIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isHeight = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    HEIGHT_Status = "In Progress"
                    binding.heightIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isHeight = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    HEIGHT_Status = "Completed"
                    binding.heightIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isHeight = true
                }
                else
                {
                    HEIGHT_Status = "Not started"
                    binding.heightIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isHeight = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Waist and Hip")
            {
                if (station.isCancelled == 1)
                {
                    HIP_Status= "Canceled"
                    binding.hipIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isHip = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    HIP_Status = "In Progress"
                    binding.hipIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isHip = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    HIP_Status = "Completed"
                    binding.hipIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isHip = true
                }
                else
                {
                    HIP_Status = "Not started"
                    binding.hipIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isHip = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Grip Strength")
            {
                if (station.isCancelled == 1)
                {
                    GRIP_Status= "Canceled"
                    binding.gripIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isGrip = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    GRIP_Status = "In Progress"
                    binding.gripIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isGrip = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    GRIP_Status = "Completed"
                    binding.gripIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isGrip = true
                }
                else
                {
                    GRIP_Status = "Not started"
                    binding.gripIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isGrip = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Blood Pressure") {
                if (station.isCancelled == 1) {
                    BP_Status = "Canceled"
                    binding.bpIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isBp = true
                }
                else if (station.status_code!!.toInt() == 1) {
                    BP_Status = "In Progress"
                    binding.bpIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isBp = true
                }
                else if (station.status_code!!.toInt() == 100) {
                    BP_Status = "Completed"
                    binding.bpIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isBp = true
                }
                else {
                    BP_Status = "Not started"
                    binding.bpIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isBp = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Biological Samples") {
                if (station.isCancelled == 1) {
                    SAM_Status = "Canceled"
                    binding.sampleIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isSample = true
                }
                else if (station.status_code!!.toInt() == 1) {
                    SAM_Status = "In Progress"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isSample = true
                }
                else if (station.status_code!!.toInt() == 1000) {
                    SAM_Status = "In Progress"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isSample = true
                }
                else if (station.status_code!!.toInt() == 100) {
                    SAM_Status = "Completed"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isSample = true
                }
                else if (station.status_code!!.toInt() == 10000) {
                    SAM_Status = "Completed"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isSample = true
                }
                else {
                    SAM_Status = "Not started"
                    binding.sampleIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isSample = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Spirometry") {
                if (station.isCancelled == 1) {
                    SP_Status = "Canceled"
                    binding.spiroIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isSpiro = true
                } else if (station.status_code!!.toInt() == 1) {
                    SP_Status = "In Progress"
                    binding.spiroIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isSpiro = true
                } else if (station.status_code!!.toInt() == 100) {
                    SP_Status = "Completed"
                    binding.spiroIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isSpiro = true
                } else {
                    SP_Status = "Not started"
                    binding.spiroIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isSpiro = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Staff HLQ") {
                if (station.isCancelled == 1) {
                    ST_QU_Status = "Canceled"
                    binding.hlqIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isStaff = true
                } else if (station.status_code!!.toInt() == 1) {
                    ST_QU_Status = "In Progress"
                    binding.hlqIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isStaff = true
                } else if (station.status_code!!.toInt() == 100) {
                    ST_QU_Status = "Completed"
                    binding.hlqIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isStaff = true
                } else {
                    ST_QU_Status = "Not started"
                    binding.hlqIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isStaff = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Participant HLQ") {
                if (station.isCancelled == 1) {
                    PA_QU_Status = "Canceled"
                    binding.selfIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isSelf = true
                } else if (station.status_code!!.toInt() == 1) {
                    PA_QU_Status = "In Progress"
                    binding.selfIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isSelf = true
                } else if (station.status_code!!.toInt() == 100) {
                    PA_QU_Status = "Completed"
                    binding.selfIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isSelf = true
                } else {
                    PA_QU_Status = "Not started"
                    binding.selfIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isSelf = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Dual X-ray absorptometry (DXA)") {
                if (station.isCancelled == 1) {
                    DXA_Status = "Canceled"
                    binding.dxaIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isDxa = true
                } else if (station.status_code!!.toInt() == 1) {
                    DXA_Status = "In Progress"
                    binding.dxaIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isDxa = true
                } else if (station.status_code!!.toInt() == 100) {
                    DXA_Status = "Completed"
                    binding.dxaIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isDxa = true
                } else {
                    DXA_Status = "Not started"
                    binding.dxaIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isDxa = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Carotid Ultrasound") {
                if (station.isCancelled == 1) {
                    ULTRA_Status = "Canceled"
                    binding.ultraIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isUltra = true
                } else if (station.status_code!!.toInt() == 1) {
                    ULTRA_Status = "In Progress"
                    binding.ultraIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isUltra = true
                } else if (station.status_code!!.toInt() == 100) {
                    ULTRA_Status = "Completed"
                    binding.ultraIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isUltra = true
                } else {
                    ULTRA_Status = "Not started"
                    binding.ultraIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isUltra = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Fundoscopy") {
                if (station.isCancelled == 1) {
                    FUN_Status = "Canceled"
                    binding.fundoIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isFundo = true
                } else if (station.status_code!!.toInt() == 1) {
                    FUN_Status = "In Progress"
                    binding.fundoIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isFundo = true
                } else if (station.status_code!!.toInt() == 100) {
                    FUN_Status = "Completed"
                    binding.fundoIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isFundo = true
                } else {
                    FUN_Status = "Not started"
                    binding.fundoIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isFundo = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Registration") {
                if (station.isCancelled == 1) {
                    REG_Status = "Canceled"
                    binding.registerIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isReg = true
                } else if (station.status_code!!.toInt() == 1) {
                    REG_Status = "In Progress"
                    binding.registerIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isReg = true
                } else if (station.status_code!!.toInt() == 100) {
                    REG_Status = "Completed"
                    binding.registerIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isReg = true
                } else {
                    REG_Status = "Not started"
                    binding.registerIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isReg = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "ECG") {
                if (station.isCancelled == 1) {
                    ECG_Status = "Canceled"
                    binding.ecgIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isEcg = true
                } else if (station.status_code!!.toInt() == 1) {
                    ECG_Status = "In Progress"
                    binding.ecgIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isEcg = true
                } else if (station.status_code!!.toInt() == 100) {
                    ECG_Status = "Completed"
                    binding.ecgIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isEcg = true
                } else {
                    ECG_Status = "Not started"
                    binding.ecgIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isEcg = false
                }
            }
        }
        for (station in stationList) {
            if (station.station_name == "Checkout") {
                if (station.isCancelled == 1) {
                    CHK_Status = "Canceled"
                    binding.checkoutIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isCheckout = true
                } else if (station.status_code!!.toInt() == 1) {
                    CHK_Status = "In Progress"
                    binding.checkoutIcon.background =
                        resources.getDrawable(R.drawable.status_progress)

                    isCheckout = true
                } else if (station.status_code!!.toInt() == 100) {
                    CHK_Status = "Completed"
                    binding.checkoutIcon.background =
                        resources.getDrawable(R.drawable.status_complete)

                    isCheckout = true
                } else {
                    CHK_Status = "Not started"
                    binding.checkoutIcon.background =
                        resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isCheckout = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Axivity")
            {
                if (station.isCancelled == 1)
                {
                    AX_Status = "Canceled"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isAxivity = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    AX_Status = "In Progress"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isAxivity = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    AX_Status = "Completed"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isAxivity = true
                }
                else
                {
                    AX_Status = "Not started"
                    binding.axivityIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isAxivity = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Visual Acuity")
            {
                if (station.isCancelled == 1)
                {
                    ACUITY_Status = "Canceled"
                    binding.visualIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isVisual = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    ACUITY_Status = "In Progress"
                    binding.visualIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isVisual = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    ACUITY_Status = "Completed"
                    binding.visualIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isVisual = true
                }
                else
                {
                    ACUITY_Status = "Not started"
                    binding.visualIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isVisual = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Treadmill")
            {
                if (station.isCancelled == 1)
                {
                    TRED_Status = "Canceled"
                    binding.tredIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isTread = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    TRED_Status = "In Progress"
                    binding.tredIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isTread = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    TRED_Status = "Completed"
                    binding.tredIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isTread = true
                }
                else
                {
                    TRED_Status = "Not started"
                    binding.tredIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isTread = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Food Frequency")
            {
                if (station.isCancelled == 1)
                {
                    FFQ_Status = "Canceled"
                    binding.ffqIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isFFQ = true
                }
                else if (station.status_code!!.toInt() == 1 || station.status_code!!.toInt() == 10)
                {
                    FFQ_Status = "In Progress"
                    binding.ffqIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isFFQ = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    FFQ_Status = "Completed"
                    binding.ffqIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isFFQ = true
                }
                else
                {
                    FFQ_Status = "Not started"
                    binding.ffqIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isFFQ = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Cognition")
            {
                if (station.isCancelled == 1)
                {
                    COG_Status = "Canceled"
                    binding.cogIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isCog = true
                }
                else if (station.status_code!!.toInt() == 1 || station.status_code!!.toInt() == 10)
                {
                    COG_Status = "In Progress"
                    binding.cogIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isCog = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    COG_Status = "Completed"
                    binding.cogIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isCog = true
                }
                else
                {
                    COG_Status = "Not started"
                    binding.cogIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isCog = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Vicorder")
            {
                if (station.isCancelled == 1)
                {
                    VIC_Status = "Canceled"
                    binding.vicIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isVic = true
                }
                else if (station.status_code!!.toInt() == 1)
                {
                    VIC_Status = "In Progress"
                    binding.vicIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isVic = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    VIC_Status = "Completed"
                    binding.vicIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isVic = true
                }
                else
                {
                    VIC_Status = "Not started"
                    binding.vicIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isVic = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Skin")
            {
                if (station.isCancelled == 1)
                {
                    SKI_Status = "Canceled"
                    binding.skinIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isSkin = true
                }
                else if (station.status_code!!.toInt() == 1 || station.status_code!!.toInt() == 10)
                {
                    SKI_Status = "In Progress"
                    binding.skinIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isSkin = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    SKI_Status = "Completed"
                    binding.skinIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isSkin = true
                }
                else
                {
                    SKI_Status = "Not started"
                    binding.skinIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isSkin = false
                }
            }
        }
        for (station in stationList)
        {
            if (station.station_name == "Octa")
            {
                if (station.isCancelled == 1)
                {
                    OCT_Status = "Canceled"
                    binding.octaIcon.background = resources.getDrawable(R.drawable.status_cancel)

                    isOcta = true
                }
                else if (station.status_code!!.toInt() == 1 || station.status_code!!.toInt() == 10)
                {
                    OCT_Status = "In Progress"
                    binding.octaIcon.background = resources.getDrawable(R.drawable.status_progress)

                    isOcta = true
                }
                else if (station.status_code!!.toInt() == 100)
                {
                    OCT_Status = "Completed"
                    binding.octaIcon.background = resources.getDrawable(R.drawable.status_complete)

                    isOcta = true
                }
                else
                {
                    OCT_Status = "Not started"
                    binding.octaIcon.background = resources.getDrawable(R.drawable.ic_icon_status_warning_yellow)

                    isOcta = false
                }
            }
        }

        Log.d("MEASUREMENT_FRAGMENT", "STATION_STATUSES:"
                + "HEI - "+ isHeight
                + "BP - "+ isBp
                + "FBG - "+ isSample
                + "SP - "+ isSpiro
                + "QU - "+ isStaff
                + "ECG - "+ isEcg
                + "AX - "+ isAxivity
                + "FUN - "+ isFundo
                + "DXA - "+ isDxa
                + "REG - "+ isReg
                + "CHK - "+ isCheckout
                + "ULTRA - "+ isUltra
                + "PA_QU - "+ isSelf
                + "VISU - "+ isVisual
                + "TRE - "+ isTread
                + "FFQ - "+ isFFQ
                + "COG - "+ isCog
                + "HIP - "+ isHip
                + "GRI - "+ isGrip
                + "VIC - "+ isVic
                + "SKIN - "+ isSkin
                + "OCTA - "+ isOcta)

        var overallStatus : Boolean

//        if ((BP_Status != "Not started" || BP_Status != "In Progress")
//            && (REG_Status != "Not started" || REG_Status != "In Progress")
//            && (HEIGHT_Status != "Not started" || HEIGHT_Status != "In Progress")
//            && (HIP_Status != "Not started" || HIP_Status != "In Progress")
//            && (GRIP_Status != "Not started" || GRIP_Status != "In Progress")
//            && (ST_QU_Status != "Not started" || ST_QU_Status != "In Progress")
//            && (SAM_Status != "Not started" || SAM_Status != "In Progress")
//            && (ECG_Status != "Not started" || ECG_Status != "In Progress")
//            && (SP_Status != "Not started" || SP_Status != "In Progress")
//            && (FUN_Status != "Not started" || FUN_Status != "In Progress")
//            && (BP_Status != "Not started" || BP_Status != "In Progress")
//            && (AX_Status != "Not started" || AX_Status != "In Progress")
//            && (DXA_Status != "Not started" || DXA_Status != "In Progress")
//            && (ULTRA_Status != "Not started" || ULTRA_Status != "In Progress")
//            && (ACUITY_Status != "Not started" || ACUITY_Status != "In Progress")
//            && (TRED_Status != "Not started" || TRED_Status != "In Progress")
//            && (FFQ_Status != "Not started" || FFQ_Status != "In Progress")
//            && (COG_Status != "Not started" || COG_Status != "In Progress")
//            && (PA_QU_Status != "Not started" || PA_QU_Status != "In Progress"))
//        {
        if (isBp!! && isReg!! && isHeight!! && isHip!! && isGrip!! && isStaff!! && isSample!! && isEcg!! && isSpiro!! && isFundo!! &&
                isAxivity!! && isDxa!! && isUltra!! && isVisual!! && isTread!! && isFFQ!! && isCog!! && isSelf!! && isVic!! && isSkin!! && isOcta!!)
        {
            return true
        }
//        else
//        {
//            return false
//        }

        binding.progressBar.visibility = View.GONE
//
        return false
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

//    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater?.inflate(R.menu.menu_main, menu)
//        //checkConnection(menu!!)
//    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
