package org.singapore.ghru.ui.heightweight.weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.StationDevicesRepository
import org.singapore.ghru.vo.Measurements
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.StationDeviceData
import javax.inject.Inject


class WeightViewModel
@Inject constructor(stationDevicesRepository: StationDevicesRepository) : ViewModel() {


    var isValidateError: Boolean = false

    var isValidWeight: Boolean = false

    var isValifFatComp: Boolean = false

    var isValidVisceralFat: Boolean = false

    var isValidMuscle: Boolean = false

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
}
