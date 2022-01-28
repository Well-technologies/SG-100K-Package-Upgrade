package org.singapore.ghru.ui.bodymeasurements.bp.checkmeasurement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.BloodPressureRequestRepository
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECGData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.request.BloodPressureMetaRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CheckMeasurementDialogViewModel
@Inject constructor(treadmillRepository: TreadmillRepository,
                    bloodPressureRequestRepository: BloodPressureRequestRepository
) : ViewModel() {

//    private val _screeningId1: MutableLiveData<String> = MutableLiveData()
//
//    var getTraceStatus: LiveData<Resource<ResourceData<ECGData>>> = Transformations
//        .switchMap(_screeningId1) { screeningId ->
//            if (screeningId == null) {
//                AbsentLiveData.create()
//            } else {
//                treadmillRepository.getEcgData(screeningId)
//            }
//        }
//
//    fun setScreeningIdEcgTrace(screeningId: String?) {
//        if (_screeningId1.value == screeningId) {
//            return
//        }
//        _screeningId1.value = screeningId
//    }

    // post bp data -------------------------------------------------------------------------------------

    private val _bloodPressureRequestRemote: MutableLiveData<BloodPressureMetaRequestId> = MutableLiveData()

    var bloodPressureRequestRemote: LiveData<Resource<BloodPressureMetaRequest>>? = Transformations
        .switchMap(_bloodPressureRequestRemote) { member ->
            member.ifExists { bloodPressureMetaRequest, participantRequest ->
                bloodPressureRequestRepository.syncBloodPressureMetaRequest(bloodPressureMetaRequest!!, participantRequest!!)
            }
        }

    fun setBloodPressureMetaRequestRemote(bloodPressureMetaRequest: BloodPressureMetaRequest, participant: ParticipantRequest) {
        val measurementRequestId = BloodPressureMetaRequestId(bloodPressureMetaRequest, participant)
        if (_bloodPressureRequestRemote.value == measurementRequestId) {
            return
        }
        _bloodPressureRequestRemote.value = measurementRequestId
    }

    data class BloodPressureMetaRequestId(val bloodPressureMetaRequest: BloodPressureMetaRequest?, val participant: ParticipantRequest?) {
        fun <T> ifExists(f: (BloodPressureMetaRequest?, ParticipantRequest?) -> LiveData<T>): LiveData<T> {
            return if (bloodPressureMetaRequest == null || participant == null) {
                AbsentLiveData.create()
            } else {
                f(bloodPressureMetaRequest, participant)
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
}