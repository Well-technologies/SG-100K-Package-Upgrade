package org.singapore.ghru.ui.skin.reading

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
import org.singapore.ghru.vo.request.SkinRequest
import org.singapore.ghru.vo.request.VicorderRequest
import javax.inject.Inject


class SkinReadingViewModel
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

    val haveVapometer = MutableLiveData<String>()
    val haveMoisture = MutableLiveData<String>()
    val havePh = MutableLiveData<String>()
    val haveManual = MutableLiveData<Boolean>()

    fun setHaveVapometer(item: String) {
        haveVapometer.value = item
    }

    fun setHaveMoisture(item: String) {
        haveMoisture.value = item
    }

    fun setHavePH(item: String) {
        havePh.value = item
    }

    fun setHaveManual(item: Boolean) {
        haveManual.value = item
    }

    // -------------------------------------------------------------------------------------------------

    //    Post Vicorder ------------------------------------------------------------------------------------------

    private val _skinPostRequest: MutableLiveData<SkinRequest> = MutableLiveData()
    private var _participant: String? = null

    fun setPostSkin(skinRequest: SkinRequest, participantId: String?) {
        _participant = participantId
        if (_skinPostRequest.value == skinRequest) {
            return
        }
        _skinPostRequest.value = skinRequest
    }

    var skinPostComplete: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_skinPostRequest) { skinPostRequest ->
            if (skinPostRequest == null) {
                AbsentLiveData.create()
            } else {
                vicorderRepository.syncSkin(skinPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------
}
