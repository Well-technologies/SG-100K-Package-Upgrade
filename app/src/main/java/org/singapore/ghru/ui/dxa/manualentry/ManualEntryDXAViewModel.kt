package org.singapore.ghru.ui.dxa.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.BodyMeasurementRequestRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.SpirometryRequest
import org.singapore.ghru.vo.StationData
import org.singapore.ghru.vo.request.BodyMeasurementMeta
import org.singapore.ghru.vo.request.BodyMeasurementMetaNew
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class ManualEntryDXAViewModel
@Inject constructor(participantRepository: ParticipantRepository,
                    bodyMeasurementRequestRepository: BodyMeasurementRequestRepository
) : ViewModel() {

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
                participantRepository.getParticipantRequest(screeningId, "dxa")
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