package org.singapore.ghru.ui.treadmill.ecgcheck

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECGData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import javax.inject.Inject


class EcgCheckDialogViewModel
@Inject constructor(treadmillRepository: TreadmillRepository) : ViewModel() {

    private val _screeningId1: MutableLiveData<String> = MutableLiveData()

    var getTraceStatus: LiveData<Resource<ResourceData<ECGData>>> = Transformations
        .switchMap(_screeningId1) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                treadmillRepository.getEcgData(screeningId)
            }
        }

    fun setScreeningIdEcgTrace(screeningId: String?) {
        if (_screeningId1.value == screeningId) {
            return
        }
        _screeningId1.value = screeningId
    }
}