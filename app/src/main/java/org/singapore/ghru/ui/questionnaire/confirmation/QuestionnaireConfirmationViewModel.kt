package org.singapore.ghru.ui.questionnaire.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.FFQRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.repository.QuestionnaireRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.HLQResponse
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class QuestionnaireConfirmationViewModel
@Inject constructor(questionnaireRepository: QuestionnaireRepository) : ViewModel() {

    private val _hlqUpdateRequest: MutableLiveData<HLQResponse> = MutableLiveData()
    private var _participantId: String? = null

    fun setUpdateHLQ(hlqRequest: HLQResponse, participantId: String?) {
        _participantId = participantId
        if (_hlqUpdateRequest.value == hlqRequest) {
            return
        }
        _hlqUpdateRequest.value = hlqRequest
    }

    var hlqUpdateComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_hlqUpdateRequest) { hlqRequest ->
            if (hlqRequest == null) {
                AbsentLiveData.create()
            } else {
                questionnaireRepository.updateHLQ(hlqRequest,_participantId!!)
            }
        }

    val haveStaff = MutableLiveData<Boolean>()

    fun setHaveStaff(item: Boolean) {
        haveStaff.value = item
    }

    val haveAssistance = MutableLiveData<Boolean>()

    fun setHaveAssistance(item: Boolean) {
        haveAssistance.value = item
    }
}
