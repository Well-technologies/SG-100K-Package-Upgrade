package org.singapore.ghru.ui.dashboard.allparticipants

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.HomeRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import javax.inject.Inject


class AllParticipantsViewModel
@Inject constructor(repository: HomeRepository,
                    participantRepository: ParticipantRepository) : ViewModel() {
    private val _home = MutableLiveData<String>()
    val home: LiveData<String>
        get() = _home

    val homeItem: LiveData<Resource<List<ParticipantStationsItem>>> = Transformations
        .switchMap(_home) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getAllParticipantData()
            }
        }

    fun setId(lang: String?) {
        if (_home.value != lang) {
            _home.value = lang
        }
    }

    private val _filterId: MutableLiveData<FilterId> = MutableLiveData()

    var filterparticipantListItems: LiveData<Resource<ResourceDataWithMeta<List<ParticipantStationItemNewNew>>>>? = Transformations
        .switchMap(_filterId) { input ->
            input.ifExists {page, keyWord ->
                participantRepository.getAllParticipantStations(page!!, keyWord!!.toString())

            }
        }

    fun setFilterId(page: Int, keyWord: String) {
        val update =
            FilterId(page = page, keyWord = keyWord)
        if (_filterId.value == update) {
            return
        }
        _filterId.value = update
    }

    data class FilterId(val page: Int?, val keyWord: String?) {

        fun <T> ifExists(f: (Int?, String?) -> LiveData<T>): LiveData<T> {
            return if (page == null || keyWord == null) {
                AbsentLiveData.create()
            } else {
                f(page, keyWord)
            }
        }
    }

}
