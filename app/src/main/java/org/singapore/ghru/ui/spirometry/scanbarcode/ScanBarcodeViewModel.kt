package org.singapore.ghru.ui.spirometry.scanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.BodyMeasurementRequestRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.StationData
import org.singapore.ghru.vo.User
import org.singapore.ghru.vo.request.BodyMeasurementMetaNew
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ScanBarcodeViewModel
@Inject constructor(participantRepository: ParticipantRepository,
                    userRepository: UserRepository,
                    bodyMeasurementRequestRepository: BodyMeasurementRequestRepository
) : ViewModel() {

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
                participantRepository.getParticipantRequest(screeningId, "spirometry")
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

    // to fetch height and weight data to proceed the station

    private val _bodyMeasurementMetaId: MutableLiveData<BodyMeasurementMetaId> = MutableLiveData()

    var getHeightAndWeight: LiveData<Resource<ResourceData<StationData<BodyMeasurementMetaNew>>>>? = Transformations
        .switchMap(_bodyMeasurementMetaId) { input ->
            input.ifExists { participant, isOnline ->
                bodyMeasurementRequestRepository.getBodyMeasurementMetaNewNew(participant, isOnline)

            }
        }


    fun setParticipantToGetHeightAndWeight(participantRequest: ParticipantRequest, isOnline : Boolean) {
        val update = BodyMeasurementMetaId(participantRequest, isOnline)
        if (_bodyMeasurementMetaId.value == update) {
            return
        }
        _bodyMeasurementMetaId.value = update
    }

    data class BodyMeasurementMetaId(
        val participant: ParticipantRequest?,
        val isOnline: Boolean
    ) {
        fun <T> ifExists(f: (ParticipantRequest, Boolean) -> LiveData<T>): LiveData<T> {
            return if (participant == null || !isOnline) {
                AbsentLiveData.create()
            } else {
                f(participant, isOnline)
            }
        }
    }

    // ------------------------------------------------------------------------
}
