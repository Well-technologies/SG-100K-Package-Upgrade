package org.singapore.ghru.ui.cognition.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.CognitionRepository
import org.singapore.ghru.repository.FFQRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class CognitionGuideViewModel
@Inject constructor(userRepository: UserRepository, cognitionRepository: CognitionRepository) : ViewModel() {

//    val haveCredentials = MutableLiveData<Boolean>()
//
//    fun setHaveCredintials(item: Boolean) {
//        haveCredentials.value = item
//    }

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

//    var getParticipantCredintials: LiveData<Resource<ResourceData<ParticipantCre>>> = Transformations
//        .switchMap(_screeningId) { screeningId ->
//            if (screeningId == null) {
//                AbsentLiveData.create()
//            } else {
//                ffqRepository.getParticipantCredintials(screeningId)
//            }
//        }
//
//    fun setScreeningId(screeningId: String?) {
//        if (_screeningId.value == screeningId) {
//            return
//        }
//        _screeningId.value = screeningId
//    }

    // ----------------------------- post Cognition-----------------------------------

    private val _cogPostRequest: MutableLiveData<CognitionRequestNew1> = MutableLiveData()
    private var _participantId: String? = null

    fun setPostCog(cogRequest: CognitionRequestNew1, participantId: String?) {
        _participantId = participantId
        if (_cogPostRequest.value == cogRequest) {
            return
        }
        _cogPostRequest.value = cogRequest
    }

    var cogPostComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_cogPostRequest) { cogPostRequest ->
            if (cogPostRequest == null) {
                AbsentLiveData.create()
            } else {
                cognitionRepository.syncCogNew(cogPostRequest,_participantId!!)
            }
        }

    // ----------------------------------------------------------------------------

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

}
