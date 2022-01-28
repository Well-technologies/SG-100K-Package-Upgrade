package org.singapore.ghru.ui.vicorder.stationquestion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.AssertRepository
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.repository.VicorderRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.VicorderRequest
import javax.inject.Inject


class StationQuestionViewModel
@Inject constructor(
    val assertRepository: AssertRepository,
    vicorderRepository: VicorderRepository,
    stationDevicesRepository: StationDevicesRepository
) : ViewModel() {
    private val _stationName = MutableLiveData<String>()

    fun setStationName(stationName: Measurements) {
        val update = stationName.toString().toLowerCase()
        if (_stationName.value == update) {
            return
        }
        _stationName.value = update
    }

    var stationDeviceList: LiveData<Resource<List<StationDeviceData>>>? = Transformations
        .switchMap(_stationName) { input ->
            stationDevicesRepository.getStationDeviceList(_stationName.value!!)
        }

    // ---------------------------- new questions ------------------------------------------------------

    val haveACP = MutableLiveData<String>()
    val haveACS = MutableLiveData<String>()
    val haveTCP = MutableLiveData<String>()
    val haveTCS = MutableLiveData<String>()

    fun setHaveACP(item: String) {
        haveACP.value = item
    }

    fun setHaveACS(item: String) {
        haveACS.value = item
    }

    fun setHaveTCP(item: String) {
        haveTCP.value = item
    }

    fun setHaveTCS(item: String) {
        haveTCS.value = item
    }

    // -------------------------------------------------------------------------------------------------

    //    Post Vicorder ------------------------------------------------------------------------------------------

    private val _vicPostRequest: MutableLiveData<VicorderRequest> = MutableLiveData()
    private var _participant: String? = null

    fun setPostVic(vicRequest: VicorderRequest, participantId: String?) {
        _participant = participantId
        if (_vicPostRequest.value == vicRequest) {
            return
        }
        _vicPostRequest.value = vicRequest
    }

    var vicPostComplete: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_vicPostRequest) { vicPostRequest ->
            if (vicPostRequest == null) {
                AbsentLiveData.create()
            } else {
                vicorderRepository.syncVicorder(vicPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------
}
