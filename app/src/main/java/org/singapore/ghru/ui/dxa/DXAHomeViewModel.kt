package org.singapore.ghru.ui.dxa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.AssertRepository
import org.singapore.ghru.repository.DXARepository
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class DXAHomeViewModel
@Inject constructor(val assertRepository: AssertRepository,
                    dxaRepository: DXARepository,
                    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {
    var dxaSyncError: MutableLiveData<Boolean>? = MutableLiveData<Boolean>().apply { }

    private val _participantId: MutableLiveData<ParticipantRequest> = MutableLiveData()

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var isOnline : Boolean = false

    private val _stationName = MutableLiveData<String>()

    private var body: String? = null

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

    var dxaComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                dxaRepository.syncDXA(participantId, body, isOnline)
            }
        }

    fun setParticipant(participantId: ParticipantRequest, bd: String) {
        body = bd

        if (_participantId.value == participantId) {
            return
        }
        _participantId.value = participantId
    }

    fun setParticipantComplete(participantId: ParticipantRequest, online: Boolean, bd: String) {
        isOnline = online
        body = bd

        if (_participantIdComplte.value == participantId) {
            return
        }
        _participantIdComplte.value = participantId
    }
}
