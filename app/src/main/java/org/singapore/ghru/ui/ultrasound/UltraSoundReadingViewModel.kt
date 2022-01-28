package org.singapore.ghru.ui.ultrasound

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.AssertRepository
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.repository.UltrasoundRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class UltraSoundReadingViewModel
@Inject constructor(val assertRepository: AssertRepository,
                    ultrasoundRepository: UltrasoundRepository,
                    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {

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


    var ultrasoundComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                ultrasoundRepository.syncUltrasound(participantId, body, isOnline)
            }
        }

    fun setParticipant(participantId: ParticipantRequest, bd: String) {
        body = bd

        if (_participantId.value == participantId) {
            return
        }
        _participantId.value = participantId
    }

    fun setParticipantComplete(participantId: ParticipantRequest, online: Boolean, bd : String) {
        body = bd
        isOnline = online

        if (_participantIdComplte.value == participantId) {
            return
        }
        _participantIdComplte.value = participantId
    }
}
