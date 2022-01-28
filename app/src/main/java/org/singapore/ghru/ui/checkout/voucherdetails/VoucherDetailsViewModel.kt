package org.singapore.ghru.ui.checkout.voucherdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.CheckoutRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Message
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.request.CheckoutRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class VoucherDetailsViewModel
@Inject constructor(participantRepository: ParticipantRepository,
                    checkoutRepository: CheckoutRepository) : ViewModel() {

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
                participantRepository.getParticipantRequest(screeningId, "checkout")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

    //    Post checkout ------------------------------------------------------------------------------------------

    private val _chkPostRequest: MutableLiveData<CheckoutRequest> = MutableLiveData()
    private var _participant: String? = null

    fun setPostChk(cogRequest: CheckoutRequest, participantId: String?) {
        _participant = participantId
        if (_chkPostRequest.value == cogRequest) {
            return
        }
        _chkPostRequest.value = cogRequest
    }

    var chkPostComplete: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_chkPostRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                checkoutRepository.syncChk(cogPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------
}