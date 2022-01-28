package org.singapore.ghru.ui.heightweight.height.reason

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.CancelRequestRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Message
import org.singapore.ghru.vo.MessageCancel
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.request.CancelRequest
import org.singapore.ghru.vo.request.CancelRequestWithMeta
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class ReasonDialogViewModel
@Inject constructor(cancelRequestRepository: CancelRequestRepository) : ViewModel() {

    private val _cancelId: MutableLiveData<CancelId> = MutableLiveData()


    var cancelId: LiveData<Resource<MessageCancel>>? = Transformations
        .switchMap(_cancelId) { input ->
            input.ifExists { participantRequest, cancelRequest ->
                cancelRequestRepository.newSyncCancelRequest(participantRequest, cancelRequest)
            }
        }

    fun setLogin(participantRequest: ParticipantRequest?, cancelRequest: CancelRequest?) {
        val update = CancelId(participantRequest, cancelRequest)
        if (_cancelId.value == update) {
            return
        }
        _cancelId.value = update
    }

    data class CancelId(val participantRequest: ParticipantRequest?, val cancelRequest: CancelRequest?) {
        fun <T> ifExists(f: (ParticipantRequest, CancelRequest) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null || cancelRequest == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, cancelRequest)
            }
        }
    }

    private val _cancelIdWithMeta: MutableLiveData<CancelIdWithMeta> = MutableLiveData()


    var cancelIdWithMeta: LiveData<Resource<ResourceData<MessageCancel>>>? = Transformations
        .switchMap(_cancelIdWithMeta) { input ->
            input.ifExists { participantRequest, cancelRequestWithMeta ->
                cancelRequestRepository.syncCancelRequestWithMeta(participantRequest, cancelRequestWithMeta)
            }
        }

    fun setCancelWithMeta(participantRequest: ParticipantRequest?, cancelRequestWithMeta: CancelRequestWithMeta?) {
        val update = CancelIdWithMeta(participantRequest, cancelRequestWithMeta)
        if (_cancelIdWithMeta.value == update) {
            return
        }
        _cancelIdWithMeta.value = update
    }

    data class CancelIdWithMeta(val participantRequest: ParticipantRequest?, val cancelRequestWithMeta: CancelRequestWithMeta?) {
        fun <T> ifExists(f: (ParticipantRequest, CancelRequestWithMeta) -> LiveData<T>): LiveData<T> {
            return if (participantRequest == null || cancelRequestWithMeta == null) {
                AbsentLiveData.create()
            } else {
                f(participantRequest, cancelRequestWithMeta)
            }
        }
    }
}
