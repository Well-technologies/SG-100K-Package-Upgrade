package org.singapore.ghru.ui.samplecollection.bagscanned.partialreadings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.BloodPressureRequestRepository
import org.singapore.ghru.repository.SampleRepository
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECGData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.SampleData
import org.singapore.ghru.vo.request.BloodPressureMetaRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.SampleCreateRequest
import javax.inject.Inject


class PartialReadingsDialogViewModel
@Inject constructor(
    sampleRepository: SampleRepository
) : ViewModel() {

    private val _sampleRequest: MutableLiveData<SampleId> = MutableLiveData()

    fun setSampleRequest(
        participantRequest: ParticipantRequest?, sampleCreateRequest: SampleCreateRequest
    ) {
        val update = SampleId(participantRequest, sampleCreateRequest)
        if (_sampleRequest.value == update) {
            return
        }
        _sampleRequest.value = update
    }

    var postSample: LiveData<Resource<ResourceData<SampleData>>>? = Transformations
        .switchMap(_sampleRequest) { input ->
            input.ifExists { participantRequest, sampleCreateRequest ->
                sampleRepository.syncSample(participantRequest, sampleCreateRequest)
            }
        }

    data class SampleId(val participantRequest: ParticipantRequest?, val comment : SampleCreateRequest) {
        fun <T> ifExists(f: (ParticipantRequest, SampleCreateRequest) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, comment)
            }
        }
    }
}