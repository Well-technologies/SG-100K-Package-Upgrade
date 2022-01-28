package org.singapore.ghru.ui.ultrasound.missingvalue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.repository.UltrasoundRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECG
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class UltraMissingDialogViewModel
@Inject constructor(
    ultrasoundRepository: UltrasoundRepository
    ) : ViewModel() {


    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var isOnline : Boolean = false
    private var body: String? = null

    var ultrasoundComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                ultrasoundRepository.syncUltrasound(participantId, body, isOnline)
            }
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
