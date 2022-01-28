package org.singapore.ghru.ui.dxa.missingvalues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.DXARepository
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECG
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class DXAMissingDialogViewModel
@Inject constructor(
    dxaRepository: DXARepository
    ) : ViewModel() {

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var isOnline : Boolean = false
    private var body: String? = null

    var dxaComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                dxaRepository.syncDXA(participantId, body, isOnline)
            }
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
