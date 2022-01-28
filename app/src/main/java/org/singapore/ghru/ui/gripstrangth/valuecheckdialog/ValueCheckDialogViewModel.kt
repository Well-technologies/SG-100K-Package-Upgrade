package org.singapore.ghru.ui.gripstrangth.valuecheckdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.GripStrengthRepository
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECGData
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import javax.inject.Inject


class ValueCheckDialogViewModel
@Inject constructor(gripStrengthRepository: GripStrengthRepository) : ViewModel() {

    private val _gripMeasurementMetaOffline = MutableLiveData<GripStrengthRequest>()

    val gripMeasurementMetaOffline: LiveData<Resource<GripStrengthRequest>>? = Transformations
        .switchMap(_gripMeasurementMetaOffline) { bodyMeasurementMetaX ->
            if (bodyMeasurementMetaX == null) {
                AbsentLiveData.create()
            } else {
                gripStrengthRepository.gripMeasurementMeta(bodyMeasurementMetaX)
            }
        }

    fun setGripMeasurementMeta(
        bodyMeasurementRequest: GripStrengthRequest
    ) {
        if (_gripMeasurementMetaOffline.value == bodyMeasurementRequest) {
            return
        }
        _gripMeasurementMetaOffline.value = bodyMeasurementRequest
    }
}