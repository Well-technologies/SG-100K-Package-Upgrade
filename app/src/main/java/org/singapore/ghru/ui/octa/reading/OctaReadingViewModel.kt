package org.singapore.ghru.ui.octa.reading

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
import org.singapore.ghru.vo.request.OctaRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.VicorderRequest
import javax.inject.Inject


class OctaReadingViewModel
@Inject constructor(
    val stationDevicesRepository: StationDevicesRepository,
    vicorderRepository: VicorderRepository
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

    val haveExport = MutableLiveData<String>()
    val haveDilatation = MutableLiveData<String>()
    val haveObsveed = MutableLiveData<String>()
    val haveMovement = MutableLiveData<String>()
    val haveSurgery = MutableLiveData<String>()
    val haveCataract = MutableLiveData<String>()

    fun setHaveExport(item: String) {
        haveExport.value = item
    }

    fun setHaveDilatation(item: String) {
        haveDilatation.value = item
    }

    fun setHaveObsveed(item: String) {
        haveObsveed.value = item
    }

    fun setHaveMovement(item: String) {
        haveMovement.value = item
    }

    fun setHaveSurgery(item: String) {
        haveSurgery.value = item
    }

    fun setHaveCataract(item: String) {
        haveCataract.value = item
    }

    // -------------------------------------------------------------------------------------------------

    //    Post Vicorder ------------------------------------------------------------------------------------------

    private val _octaPostRequest: MutableLiveData<OctaRequest> = MutableLiveData()
    private var _participant: String? = null

    fun setPostOcta(octaRequest: OctaRequest, participantId: String?) {
        _participant = participantId
        if (_octaPostRequest.value == octaRequest) {
            return
        }
        _octaPostRequest.value = octaRequest
    }

    var octaPostComplete: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_octaPostRequest) { octaPostRequest ->
            if (octaPostRequest == null) {
                AbsentLiveData.create()
            } else {
                vicorderRepository.syncOcta(octaPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------

}
