package org.singapore.ghru.ui.dashboard.selectedstation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.HomeRepository
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import javax.inject.Inject


class SelectedStationViewModel
@Inject constructor(repository: HomeRepository,
                    participantRepository: ParticipantRepository) : ViewModel() {
    private val _home = MutableLiveData<String>()
    val home: LiveData<String>
        get() = _home

    val homeItem: LiveData<Resource<List<StationItem>>> = Transformations
        .switchMap(_home) { login ->
            if (login == null) {
                AbsentLiveData.create()
            } else {
                repository.getSelectedStationData()
            }
        }

    fun setId(lang: String?) {
        if (_home.value != lang) {
            _home.value = lang
        }
    }

    //    Get participants with search and filter ---------------------------------------------------------------

    private val _filterId: MutableLiveData<FilterId> = MutableLiveData()

    var filterparticipantListItems: LiveData<Resource<ResourceDataWithMeta<StationParticipant>>>? = Transformations
        .switchMap(_filterId) { input ->
            input.ifExists { page, station, status, date, sort ->
                participantRepository.filterStationParticipants(page!!, station!!, status!!.toString(), date!!.toString(), sort!!.toString())

            }
        }

    fun setFilterId(page: Int, station: String, status: String, date: String, sort: String) {
        val update =
            FilterId(page = page, station = station, status = status, date = date, sort = sort)
        if (_filterId.value == update) {
            return
        }
        _filterId.value = update
    }

    data class FilterId(val page: Int?, val station: String?, val status: String?, val date: String?, val sort: String?) {

        fun <T> ifExists(f: (Int?, String?, String?, String?, String?) -> LiveData<T>): LiveData<T> {
            return if (page == null || station == null || status == null || date == null || sort == null) {
                AbsentLiveData.create()
            } else {
                f(page, station, status, date, sort)
            }
        }
    }
//    --------------------------------------------------------------------------------------------------------

    private val _notStartedFilterId: MutableLiveData<FilterId> = MutableLiveData()

    var notStartedStations: LiveData<Resource<ResourceDataWithMeta<StationParticipant>>>? = Transformations
        .switchMap(_notStartedFilterId) { input ->
            input.ifExists { page, station, status, date, sort ->
                participantRepository.filterNotStartedStationParticipants(page!!, station!!, status!!.toString(), date!!.toString(), sort!!.toString())

            }
        }

    fun setNotStartedFilterId(page: Int, station: String, status: String, date: String, sort: String) {
        val update =
            FilterId(page = page, station = station, status = status, date = date, sort = sort)
        if (_notStartedFilterId.value == update) {
            return
        }
        _notStartedFilterId.value = update
    }
//    --------------------------------------------------------------------------------------------------------

}
