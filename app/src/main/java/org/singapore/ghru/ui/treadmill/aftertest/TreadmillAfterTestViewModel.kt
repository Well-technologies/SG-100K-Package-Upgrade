package org.singapore.ghru.ui.treadmill.aftertest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECG
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class TreadmillAfterTestViewModel
@Inject constructor(val treadmillRepository: TreadmillRepository) : ViewModel() {

    var isValidRating: Boolean = false
    val rating = MutableLiveData<String>()
    val chestPain = MutableLiveData<Boolean>()
    val breathless = MutableLiveData<Boolean>()
    val frontBar = MutableLiveData<Boolean>()

    private val _participantId: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var isOnline : Boolean = false
    private var body: String? = null

    fun setChestPain(item: Boolean) {
        chestPain.value = item
    }

    fun setBreathless(item: Boolean) {
        breathless.value = item
    }

    fun setFrontBar(item: Boolean) {
        frontBar.value = item
    }

    var treadmillComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                treadmillRepository.syncTreadmill(participantId, body, isOnline)
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
