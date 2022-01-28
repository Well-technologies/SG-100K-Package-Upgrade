package org.singapore.ghru.ui.foodquestionnaire.confirmartion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.FFQRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class FFQViewModel
@Inject constructor(ffqRepository : FFQRepository, userRepository: UserRepository) : ViewModel() {

    private val _ffqMetaOffline = MutableLiveData<FFQRequest>()

    val ffqMetaSync: LiveData<Resource<FFQRequest>>? = Transformations
        .switchMap(_ffqMetaOffline) { ffqMetaX ->
            if (ffqMetaX == null) {
                AbsentLiveData.create()
            } else {
                ffqRepository.ffqMeta(ffqMetaX)
            }
        }

    fun setVisualMeasurementMeta(
        ffqRequest: FFQRequest
    ) {
        if (_ffqMetaOffline.value == ffqRequest) {
            return
        }
        _ffqMetaOffline.value = ffqRequest
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

    val haveLanguage = MutableLiveData<Boolean>()

    fun setHaveLanguage(item: Boolean) {
        haveLanguage.value = item
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

    private val _ffqUpdateRequest: MutableLiveData<FFQRequestNew> = MutableLiveData()
    private var _participantId: String? = null

    fun setUpdateFFQ(ffqRequest: FFQRequestNew, participantId: String?) {
        _participantId = participantId
        if (_ffqUpdateRequest.value == ffqRequest) {
            return
        }
        _ffqUpdateRequest.value = ffqRequest
    }

    var ffqUpdateComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_ffqUpdateRequest) { ffqRequest ->
            if (ffqRequest == null) {
                AbsentLiveData.create()
            } else {
                ffqRepository.updateFFQ(ffqRequest,_participantId!!)
            }
        }

    // ----------------------------------------------------------------------------
}
