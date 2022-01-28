package org.singapore.ghru.ui.ecg.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class ManualEntryECGModel @Inject constructor(participantRepository: ParticipantRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

    private val _screeningIdOffline: MutableLiveData<String> = MutableLiveData()
    val screeningIdOffline: LiveData<String>
        get() = _screeningIdOffline

    var participantOffline: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_screeningIdOffline) { screeningIdOffline ->
            if (screeningIdOffline == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantOffline(screeningIdOffline)
            }
        }

    fun setScreeningIdOffline(screeningIdOffline: String?) {
        if (_screeningIdOffline.value == screeningIdOffline) {
            return
        }
        _screeningIdOffline.value = screeningIdOffline
    }

    var participant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "ecg")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

    private val _checkoutScreeningId: MutableLiveData<String> = MutableLiveData()

    var checkoutParticipant: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_checkoutScreeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "checkout")
            }
        }

    fun setCheckoutScreeningId(screeningId: String?) {
        if (_checkoutScreeningId.value == screeningId) {
            return
        }
        _checkoutScreeningId.value = screeningId
    }
}