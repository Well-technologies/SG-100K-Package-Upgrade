package org.singapore.ghru.ui.treadmill.scanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECGData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.User
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class ScanBarcodeViewModel
@Inject constructor(participantRepository: ParticipantRepository,
                    userRepository: UserRepository,
                    treadmillRepository: TreadmillRepository) : ViewModel() {

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    private val _screeningIdOffline: MutableLiveData<String> = MutableLiveData()

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
                participantRepository.getParticipantRequest(screeningId, "treadmill")
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }
    private val _email = MutableLiveData<String>()

    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUserDB()
            }
        }
    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }

    // get ecg station status for validation ------------------------------------------------------

    private val _screeningIdEcg: MutableLiveData<String> = MutableLiveData()

    var participantEcg: LiveData<Resource<ResourceData<ParticipantRequest>>> = Transformations
        .switchMap(_screeningIdEcg) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantRequest(screeningId, "ecg")
            }
        }

    fun setScreeningIdEcg(screeningId: String?) {
        if (_screeningIdEcg.value == screeningId) {
            return
        }
        _screeningIdEcg.value = screeningId
    }

    //--------------------------------------------------------------------------------------------

    private val _screeningId1: MutableLiveData<String> = MutableLiveData()

    var getTraceStatus: LiveData<Resource<ResourceData<ECGData>>> = Transformations
        .switchMap(_screeningId1) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                treadmillRepository.getEcgData(screeningId)
            }
        }

    fun setScreeningIdEcgTrace(screeningId: String?) {
        if (_screeningId1.value == screeningId) {
            return
        }
        _screeningId1.value = screeningId
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
