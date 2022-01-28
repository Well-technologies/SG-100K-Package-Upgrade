package org.singapore.ghru.ui.gripstrangth.strength

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.vo.GripStrengthRecord
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.StationDeviceData
import javax.inject.Inject


class StrengthViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository) : ViewModel() {


    var isValidateError: Boolean = false

    var isValidHipSize: Boolean = false

    var isValidWaistSize: Boolean = false

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


    var gripStrengthRecord: MutableLiveData<GripStrengthRecord>? = null


    fun spirometryRecord(): LiveData<GripStrengthRecord> {
        if (gripStrengthRecord == null) {
            gripStrengthRecord = MutableLiveData<GripStrengthRecord>()
            loadSpirometryRecord()
        }
        return gripStrengthRecord as LiveData<GripStrengthRecord>
    }

    fun loadSpirometryRecord() {
        gripStrengthRecord?.value = GripStrengthRecord()
    }
}
