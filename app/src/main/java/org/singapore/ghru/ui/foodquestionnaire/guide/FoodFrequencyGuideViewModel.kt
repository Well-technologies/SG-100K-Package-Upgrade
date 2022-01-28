package org.singapore.ghru.ui.foodquestionnaire.guide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.FFQRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class FoodFrequencyGuideViewModel
@Inject constructor(ffqRepository : FFQRepository, userRepository: UserRepository) : ViewModel() {

    val haveCredentials = MutableLiveData<Boolean>()

    fun setHaveCredintials(item: Boolean) {
        haveCredentials.value = item
    }

    private val _screeningId: MutableLiveData<String> = MutableLiveData()
    val screeningId: LiveData<String>
        get() = _screeningId

    var getParticipantCredintials: LiveData<Resource<ResourceData<ParticipantCre>>> = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                ffqRepository.getParticipantCredintials(screeningId)
            }
        }

    fun setScreeningId(screeningId: String?) {
        if (_screeningId.value == screeningId) {
            return
        }
        _screeningId.value = screeningId
    }

    // ----------------------------- post FFQ -----------------------------------

    private val _ffqPostRequest: MutableLiveData<FFQRequestNew1> = MutableLiveData()
    private var _participantId: String? = null

    fun setPostFFQ(ffqRequest: FFQRequestNew1, participantId: String?) {
        _participantId = participantId
        if (_ffqPostRequest.value == ffqRequest) {
            return
        }
        _ffqPostRequest.value = ffqRequest
    }

    var ffqPostComplete:LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_ffqPostRequest) { ffqPostRequest ->
            if (ffqPostRequest == null) {
                AbsentLiveData.create()
            } else {
                ffqRepository.syncFFQNew(ffqPostRequest,_participantId!!)
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

    // --------------------------get creds by passing language------------------------------------------------------------------

    private val _filterId: MutableLiveData<LanguageId> = MutableLiveData()

    var getLanguage: LiveData<Resource<ResourceData<ParticipantCre>>>? = Transformations
        .switchMap(_filterId) { input ->
            input.ifExists {screnn_id, language ->
                ffqRepository.getFFQLanguage(screnn_id!!, language!!.toString())

            }
        }

    fun setLanguageId(screeningId: String, language: String) {
        val update =
            LanguageId(screeningId = screeningId, language = language)
        if (_filterId.value == update) {
            return
        }
        _filterId.value = update
    }

    data class LanguageId(val screeningId: String?, val language: String?) {

        fun <T> ifExists(f: (String?, String?) -> LiveData<T>): LiveData<T> {
            return if (screeningId == null || language == null) {
                AbsentLiveData.create()
            } else {
                f(screeningId, language)
            }
        }
    }

    // -------------------------------------------------------------------------------

}
