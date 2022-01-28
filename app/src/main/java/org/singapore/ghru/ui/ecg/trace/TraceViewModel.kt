package org.singapore.ghru.ui.ecg.trace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.ECGRepository
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECG
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class TraceViewModel
@Inject constructor(
    eCGRepository: ECGRepository,
    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {

    //private val _participantRequestRemote: MutableLiveData<ParticipantRequest> = MutableLiveData()

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

//
//    var eCGSaveRemote: LiveData<Resource<ResourceData<ECG>>>? = Transformations
//            .switchMap(_participantRequestRemote) { participant ->
//                if (participant == null) {
//                    AbsentLiveData.create()
//                } else {
//                    eCGRepository.syncECG(participant)
//                }
//            }
//
//    fun setECGRemote(participantRequest: ParticipantRequest) {
//        if (_participantRequestRemote.value != participantRequest) {
//            _participantRequestRemote.postValue(participantRequest)
//        }
//    }

    // ------------------------ new ECG navigation without trace_status --------------------------------------

    private val _participantRequestRemote: MutableLiveData<ECGId> = MutableLiveData()
    private var isOnline : Boolean = false
    //private var contraindications: List<Map<String, String>>? = null

    var eCGSaveRemote: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantRequestRemote) { input ->
            input.ifExists { participantRequest, comment, device_id ->
                eCGRepository.syncECG(participantRequest, comment, device_id, isOnline)
            }
        }

    fun setECGRemote(participantRequest: ParticipantRequest, comment: String?, device_id: String, online : Boolean) {

        isOnline = online
        val update = ECGId(participantRequest, comment, device_id)
        if (_participantRequestRemote.value == update) {
            return
        }
        _participantRequestRemote.value = update
    }

    data class ECGId(
        val participantRequest: ParticipantRequest?,
        val comment: String?,
        val device_id: String
    ) {
        fun <T> ifExists(f: (ParticipantRequest, String?, String) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, comment, device_id)
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------
}
