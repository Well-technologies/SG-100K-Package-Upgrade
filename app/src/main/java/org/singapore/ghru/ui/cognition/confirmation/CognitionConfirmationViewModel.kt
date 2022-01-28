package org.singapore.ghru.ui.cognition.confirmation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.CognitionRepository
import org.singapore.ghru.repository.FFQRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CognitionConfirmationViewModel
@Inject constructor(cognitionRepository: CognitionRepository, userRepository: UserRepository) : ViewModel() {

    private val _cognitionMeta = MutableLiveData<CognitionRequest>()

    val cognitionMetaSync: LiveData<Resource<CognitionRequest>>? = Transformations
        .switchMap(_cognitionMeta) { cognitionMetaX ->
            if (cognitionMetaX == null) {
                AbsentLiveData.create()
            } else {
                cognitionRepository.cognitionMeta(cognitionMetaX)
            }
        }

    fun setCognitionMeta(
        cognitionRequest: CognitionRequest
    ) {
        if (_cognitionMeta.value == cognitionRequest) {
            return
        }
        _cognitionMeta.value = cognitionRequest
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

    val haveStaff = MutableLiveData<Boolean>()

    fun setHaveStaff(item: Boolean) {
        haveStaff.value = item
    }

    val haveAssistance = MutableLiveData<Boolean>()

    fun setHaveAssistance(item: Boolean) {
        haveAssistance.value = item
    }

    // ----------------------------- update FFQ -----------------------------------

    private val _cogUpdateRequest: MutableLiveData<CognitionRequestNew> = MutableLiveData()
    private var _participantId: String? = null

    fun setUpdateCog(cogRequest: CognitionRequestNew, participantId: String?) {
        _participantId = participantId
        if (_cogUpdateRequest.value == cogRequest) {
            return
        }
        _cogUpdateRequest.value = cogRequest
    }

    var cogUpdateComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_cogUpdateRequest) { cogRequest ->
            if (cogRequest == null) {
                AbsentLiveData.create()
            } else {
                cognitionRepository.updateCog(cogRequest,_participantId!!)
            }
        }

    // ----------------------------------------------------------------------------
}
