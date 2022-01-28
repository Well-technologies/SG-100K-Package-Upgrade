package org.singapore.ghru.ui.samplecollection.bagscanned

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.SampleRepository
import org.singapore.ghru.repository.SampleRequestRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.SampleCreateRequest
import org.singapore.ghru.vo.request.SampleRequest
import javax.inject.Inject


class BagScannedViewModel
@Inject constructor(
    sampleRepository: SampleRepository,
    sampleRequestRepository: SampleRequestRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val _participantRequestRemote: MutableLiveData<SampleId> = MutableLiveData()

    private val _sampleRequestLocal: MutableLiveData<SampleRequest> = MutableLiveData()

    lateinit var sampleId: String

    var comment : Comment? = null

    private val _email = MutableLiveData<String>()

    var sample: LiveData<Resource<ResourceData<SampleData>>>? = Transformations
        .switchMap(_participantRequestRemote) { input ->
            input.ifExists { participantRequest, comment ->
                sampleRepository.syncSample(participantRequest, comment)
            }
        }

    var sampleRequestLocal: LiveData<Resource<SampleRequest>>? = Transformations
        .switchMap(_sampleRequestLocal) { sampleRequestLocal ->
            if (sampleRequestLocal == null) {
                AbsentLiveData.create()
            } else {
                sampleRequestRepository.insertSampleRequest(sampleRequestLocal)
            }
        }


    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUserDB()
            }
        }


    fun setSample(
        participantRequest: ParticipantRequest?, comment: SampleCreateRequest
    ) {

        val update = SampleId(participantRequest, comment)
        if (_participantRequestRemote.value == update) {
            return
        }
        _participantRequestRemote.value = update
    }

    fun setSampleLocal(sampleRequest: SampleRequest) {
        if (_sampleRequestLocal.value != sampleRequest) {
            _sampleRequestLocal.postValue(sampleRequest)
        }
    }

    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
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
