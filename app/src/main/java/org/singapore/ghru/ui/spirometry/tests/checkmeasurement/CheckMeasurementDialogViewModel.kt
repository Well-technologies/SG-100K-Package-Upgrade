package org.singapore.ghru.ui.spirometry.tests.checkmeasurement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.BloodPressureRequestRepository
import org.singapore.ghru.repository.SpirometryRepository
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECGData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.SpirometryRequest
import org.singapore.ghru.vo.request.BloodPressureMetaRequest
import org.singapore.ghru.vo.request.BodyMeasurementMeta
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CheckMeasurementDialogViewModel
@Inject constructor(spirometryRepository: SpirometryRepository
) : ViewModel() {

    // post spiro data -------------------------------------------------------------------------------------

    private val _spirometryRequest: MutableLiveData<SpirometryRequest> = MutableLiveData()

    val spirometryRequest: LiveData<Resource<BodyMeasurementMeta>>? = Transformations
        .switchMap(_spirometryRequest) { spirometryRequest ->
            if (spirometryRequest == null) {
                AbsentLiveData.create()
            } else {
                spirometryRepository.bodyMeasurementMeta(spirometryRequest)
            }
        }

    fun setSpirometryRequest(spirometryRequest: SpirometryRequest) {
        if (_spirometryRequest.value == spirometryRequest) {
            return
        }
        _spirometryRequest.value = spirometryRequest
    }

    // --------------------------------------------------------------------------------------------------
}