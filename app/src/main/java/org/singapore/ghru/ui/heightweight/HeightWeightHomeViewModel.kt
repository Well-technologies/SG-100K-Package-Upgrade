package org.singapore.ghru.ui.heightweight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.*
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import javax.inject.Inject


class HeightWeightHomeViewModel
@Inject constructor(
    sampleRepository: SampleRepository,
    userRepository: UserRepository,
    bodyMeasurementRequestRepository: BodyMeasurementRequestRepository,
    bodyMeasurementMetaRepository: BodyMeasurementMetaRepository,
    stationDevicesRepository: StationDevicesRepository,
    participantRepository: ParticipantRepository
) : ViewModel() {

    private val _sampleMangementId: MutableLiveData<BodyMeasurementId> = MutableLiveData()

    fun setSync(
        hb1Ac: Hb1AcDto?,
        fastingBloodGlucose: FastingBloodGlucoseDto?,
        lipidProfile: LipidProfileAllDto?,
        hOGTT: HOGTTDto?,
        hemoglobin: HemoglobinDto?,
        sampleId: SampleRequest?
    ) {
        val update = BodyMeasurementId(hb1Ac, fastingBloodGlucose, lipidProfile, hOGTT, hemoglobin, sampleId)
        if (_sampleMangementId.value == update) {
            return
        }
        _sampleMangementId.value = update
    }

    var sampleMangementPocess: LiveData<Resource<Message>>? = Transformations
        .switchMap(_sampleMangementId) { input ->
            input.ifExists { hb1Ac, fastingBloodGlucose, lipidProfile, hOGTT, hemo, sampleId ->

                sampleRepository.syncSampleProcess(hb1Ac, fastingBloodGlucose, lipidProfile, hOGTT, hemo, sampleId)
            }
        }

    data class BodyMeasurementId(
        val hb1Ac: Hb1AcDto?,
        val fastingBloodGlucose: FastingBloodGlucoseDto?,
        val lipidProfile: LipidProfileAllDto?,
        val hOGTT: HOGTTDto?,
        val hemoglobin: HemoglobinDto?,
        val sampleId: SampleRequest?
    ) {
        fun <T> ifExists(f: (Hb1AcDto?, FastingBloodGlucoseDto?, LipidProfileAllDto, HOGTTDto?, HemoglobinDto?, SampleRequest?) -> LiveData<T>): LiveData<T> {
            return if (lipidProfile == null && (hb1Ac == null || fastingBloodGlucose == null || hOGTT == null) || sampleId == null) {
                AbsentLiveData.create()
            } else {
                f(hb1Ac, fastingBloodGlucose, lipidProfile!!, hOGTT, hemoglobin, sampleId)
            }
        }
    }

    data class BodyMeasurementOfflineId(
        val hb1Ac: Hb1AcDto?,
        val fastingBloodGlucose: FastingBloodGlucoseDto?,
        val lipidProfile: LipidProfileAllDto?,
        val hOGTT: HOGTTDto?,
        val sampleId: SampleRequest?,
        val syncPending: Boolean
    ) {
        fun <T> ifExists(f: (Hb1AcDto?, FastingBloodGlucoseDto?, LipidProfileAllDto, HOGTTDto?, SampleRequest, Boolean) -> LiveData<T>): LiveData<T> {
            return if (lipidProfile == null && (hb1Ac == null || fastingBloodGlucose == null || hOGTT == null) || sampleId == null) {
                AbsentLiveData.create()
            } else {
                f(hb1Ac, fastingBloodGlucose, lipidProfile!!, hOGTT, sampleId, syncPending)
            }
        }
    }

    private val _email = MutableLiveData<String>()

    private val _bodyMeasurementMeta = MutableLiveData<BodyMeasurementMetaId>()

//    private val _bodyMeasurementMetaOffline = MutableLiveData<BodyMeasurementMeta>()


    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUserDB()
            }
        }


//    val bodyMeasurementMetaOffline: LiveData<Resource<BodyMeasurementMeta>>? = Transformations
//        .switchMap(_bodyMeasurementMetaOffline) { bodyMeasurementMetaX ->
//            if (bodyMeasurementMetaX == null) {
//                AbsentLiveData.create()
//            } else {
//                bodyMeasurementMetaRepository.bodyMeasurementMeta(bodyMeasurementMetaX)
//            }
//        }
//
//    fun setBodyMeasurementMeta(
//        bodyMeasurementRequest: BodyMeasurementMeta
//    ) {
//        if (_bodyMeasurementMetaOffline.value == bodyMeasurementRequest) {
//            return
//        }
//        _bodyMeasurementMetaOffline.value = bodyMeasurementRequest
//    }


    fun setBodyMeasurementMeta(
        bodyMeasurementRequest: BodyMeasurementMeta,
        particap: ParticipantRequest
    ) {
        val update = BodyMeasurementMetaId(bodyMeasurementRequest, particap)
        if (_bodyMeasurementMeta.value == update) {
            return
        }
        _bodyMeasurementMeta.value = update
    }


    val participantMetas: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_bodyMeasurementMeta) { input ->
            input.ifExists { bodyMeasurementMeta, participantRequest ->

                bodyMeasurementRequestRepository.syncBodyMeasurementMeta(bodyMeasurementMeta!!, participantRequest!!)
            }
        }


    data class BodyMeasurementMetaId(
        val bodyMeasurementMeta: BodyMeasurementMeta?,
        val participantRequest: ParticipantRequest?
    ) {
        fun <T> ifExists(f: (BodyMeasurementMeta?, ParticipantRequest?) -> LiveData<T>): LiveData<T> {
            return if (bodyMeasurementMeta == null && participantRequest == null) {
                AbsentLiveData.create()
            } else {
                f(bodyMeasurementMeta, participantRequest)
            }
        }
    }

    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }

//    body measurement updated into height and weight

    private val _bodyMeasurementMetaOffline = MutableLiveData<BodyMeasurementMeta>()

    val bodyMeasurementMetaOffline: LiveData<Resource<BodyMeasurementMeta>>? = Transformations
        .switchMap(_bodyMeasurementMetaOffline) { bodyMeasurementMetaX ->
            if (bodyMeasurementMetaX == null) {
                AbsentLiveData.create()
            } else {
                bodyMeasurementMetaRepository.bodyMeasurementMeta(bodyMeasurementMetaX)
            }
        }

    fun setBodyMeasurementMeta(
        bodyMeasurementRequest: BodyMeasurementMeta
    ) {
        if (_bodyMeasurementMetaOffline.value == bodyMeasurementRequest) {
            return
        }
        _bodyMeasurementMetaOffline.value = bodyMeasurementRequest
    }

    // from weight model -------------------------------

    var isValidWeight: Boolean = false

    // -------------------------------------------------

    // from height model -------------------------------

    var isValidHeight: Boolean = false

    private val _stationName = MutableLiveData<String>()

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

    // -------------------------------------------------

    // get hl7 readings --------------------------------

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

    var getHL7Readings: LiveData<Resource<ResourceData<HL7Readings>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.hL7Readings(screeningId, "height-weight")
            }
        }

    fun setHL7ScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

    // -------------------------------------------------

    // height and weight without readings --------------------------------------------------

    private val _bodyMeasurementMetaWR = MutableLiveData<BodyMeasurementMetaWithoutReadings>()
    private var _participant: String? = null

    val bodyMeasurementMetaWR: LiveData<Resource<ResourceData<BodyMeasurementMetaWithoutReadings>>>? = Transformations
        .switchMap(_bodyMeasurementMetaWR) { bodyMeasurementMetaX ->
            if (bodyMeasurementMetaX == null) {
                AbsentLiveData.create()
            } else {
                bodyMeasurementMetaRepository.syncHeightAndWeight(bodyMeasurementMetaX, _participant!!)
            }
        }

    fun setBodyMeasurementWR(bodyMeasurementRequest: BodyMeasurementMetaWithoutReadings, participantId: String?  ) {
        _participant = participantId
        if (_bodyMeasurementMetaWR.value == bodyMeasurementRequest) {
            return
        }
        _bodyMeasurementMetaWR.value = bodyMeasurementRequest
    }

    // -------------------------------------------------------------------------------------

    val haveCaptured = MutableLiveData<Boolean>()

    fun setHaveCaptured(item: Boolean) {
        haveCaptured.value = item
    }

    val haveManual = MutableLiveData<Boolean>()

    fun setHaveManual(item: Boolean) {
        haveManual.value = item
    }

    // height and weight without and without readings --------------------------------------------------

    private val _bodyMeasurementMetaManualAuto = MutableLiveData<BodyMeasurementMetaManualAuto>()
    private var _participantManualAuto: String? = null

    val bodyMeasurementMetaManualAuto: LiveData<Resource<ResourceData<BodyMeasurementMetaManualAuto>>>? = Transformations
        .switchMap(_bodyMeasurementMetaManualAuto) { bodyMeasurementMetaX ->
            if (bodyMeasurementMetaX == null) {
                AbsentLiveData.create()
            } else {
                bodyMeasurementMetaRepository.syncHeightAndWeightManualAuto(bodyMeasurementMetaX, _participantManualAuto!!)
            }
        }

    fun setBodyMeasurementManualAuto(bodyMeasurementRequest: BodyMeasurementMetaManualAuto, participantId: String?  ) {
        _participantManualAuto = participantId
        if (_bodyMeasurementMetaManualAuto.value == bodyMeasurementRequest) {
            return
        }
        _bodyMeasurementMetaManualAuto.value = bodyMeasurementRequest
    }

}
