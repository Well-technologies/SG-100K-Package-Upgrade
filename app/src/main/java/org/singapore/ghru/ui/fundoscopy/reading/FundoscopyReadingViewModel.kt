package org.singapore.ghru.ui.fundoscopy.reading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.AssertRepository
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FundoscopyReadingViewModel
@Inject constructor(
    val assertRepository: AssertRepository,
    fundoscopyRepository: FundoscopyRepository,
    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {
    var fundoscopySyncError: MutableLiveData<Boolean>? = MutableLiveData<Boolean>().apply { }


    private val _participantId: MutableLiveData<ParticipantRequest> = MutableLiveData()

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var comment: String? = null
    private var device_id: String? = null
    private var pupil_dilation : Boolean = false
    private var isOnline : Boolean = false
    private var small_pupil : String? = null
    private var participant_movement : String? = null
    private var eye_history : String? = null
    private var eye_missing : String? = null
    private var possible_cataract : String? = null

    private val _stationName = MutableLiveData<String>()

    private var contraindications : List<Map<String, String>>? = null
    private var confirmQuestions : List<Map<String, String>>? = null

    fun setStationName(stationName: Measurements) {
        val update = stationName.toString().toLowerCase()
        if (_stationName.value == update) {
            return
        }
        _stationName.value = update
    }

    var stationDeviceList: LiveData<Resource<List<StationDeviceData>>>? = Transformations
        .switchMap(_stationName) { input ->
            stationDevicesRepository.getStationDeviceList(_stationName.value!!)
        }

    var asserts: LiveData<Resource<ResourceData<List<Asset>>>>? = Transformations
        .switchMap(_participantId) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                assertRepository.getAssets(participantId, "fundoscopy")
            }
        }

//    var fundoscopyComplete: LiveData<Resource<ECG>>? = Transformations
//        .switchMap(_participantIdComplte) { participantId ->
//            if (participantId == null) {
//                AbsentLiveData.create()
//            } else {
//                fundoscopyRepository.syncFundoscopy(participantId, comment, device_id,pupil_dilation,isOnline,cataractObservation!!,contraindications)
//            }
//        }
//
//    fun setParticipant(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,dilation: Boolean,observation : String) {
//        comment = mComment
//        device_id = mDevice_id
//        pupil_dilation = dilation
//        cataractObservation = observation
//
//        if (_participantId.value == participantId) {
//            return
//        }
//        _participantId.value = participantId
//    }
//
//    fun setParticipantComplete(participantId: ParticipantRequest, mComment: String?, mDevice_id: String,dilation: Boolean,online: Boolean,observation : String, indications : List<Map<String, String>>) {
//        comment = mComment
//        device_id = mDevice_id
//        pupil_dilation = dilation
//        isOnline = online
//        cataractObservation = observation
//        contraindications = indications
//
//        if (_participantIdComplte.value == participantId) {
//            return
//        }
//        _participantIdComplte.value = participantId
//    }

    var fundoscopyComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                fundoscopyRepository.syncFundoscopy(participantId, comment, device_id,isOnline,contraindications,confirmQuestions)
            }
        }

    fun setParticipant(participantId: ParticipantRequest, mComment: String?, mDevice_id: String) {
        comment = mComment
        device_id = mDevice_id

        if (_participantId.value == participantId) {
            return
        }
        _participantId.value = participantId
    }

    fun setParticipantComplete(participantId: ParticipantRequest, mComment: String?, mDevice_id: String, online: Boolean, indications : List<Map<String, String>>, questions : List<Map<String, String>>) {
        comment = mComment
        device_id = mDevice_id
        isOnline = online
        contraindications = indications
        confirmQuestions = questions

        if (_participantIdComplte.value == participantId) {
            return
        }
        _participantIdComplte.value = participantId
    }

    // ---------------------------- new questions ------------------------------------------------------

    val haveLeftEye = MutableLiveData<Boolean>()
    val haveRightEye = MutableLiveData<Boolean>()
    val haveExport = MutableLiveData<Boolean>()
    val haveDialation = MutableLiveData<Boolean>()

    fun setHaveLeftEye(item: Boolean) {
        haveLeftEye.value = item
    }

    fun setHaveRightEye(item: Boolean) {
        haveRightEye.value = item
    }

    fun setHaveExported(item: Boolean) {
        haveExport.value = item
    }

    fun setHaveDialtion(item: Boolean) {
        haveDialation.value = item
    }

    // -------------------------------------------------------------------------------------------------
}
