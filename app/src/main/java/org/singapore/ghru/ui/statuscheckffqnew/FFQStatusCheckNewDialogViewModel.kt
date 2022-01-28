package org.singapore.ghru.ui.statuscheckffqnew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.FFQRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ParticipantCre
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import javax.inject.Inject


class FFQStatusCheckNewDialogViewModel
@Inject constructor(ffqRepository : FFQRepository) : ViewModel() {

    var codecheckMsg: MutableLiveData<String> = MutableLiveData<String>().apply { }

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

    val haveCredentials = MutableLiveData<Boolean>()

    fun setHaveCredintials(item: Boolean) {
        haveCredentials.value = item
    }

    // --------------get creds by passing language----------------------------------------------------------------------------

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