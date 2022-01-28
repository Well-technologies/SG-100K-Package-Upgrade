package org.singapore.ghru.ui.visualacuity.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.*
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import javax.inject.Inject


class VisualAcuityHomeViewModel
@Inject constructor(
    userRepository: UserRepository,
    visualAcuityRepository: VisualAcuityRepository

) : ViewModel() {

    private val _email = MutableLiveData<String>()

    val user: LiveData<Resource<User>>? = Transformations
        .switchMap(_email) { emailx ->
            if (emailx == null) {
                AbsentLiveData.create()
            } else {
                userRepository.loadUserDB()
            }
        }

    fun setUser(email: String?) {
        if (_email.value != email) {
            _email.value = email
        }
    }


//    body measurement updated into height and weight

    private val _visualMeasurementMetaOffline = MutableLiveData<VisualAcuityRequest>()

    val visualMeasurementMetaOffline: LiveData<Resource<VisualAcuityRequest>>? = Transformations
        .switchMap(_visualMeasurementMetaOffline) { bodyMeasurementMetaX ->
            if (bodyMeasurementMetaX == null) {
                AbsentLiveData.create()
            } else {
                visualAcuityRepository.visualMeasurementMeta(bodyMeasurementMetaX)
            }
        }

    fun setVisualMeasurementMeta(
        visualAcuityRequest: VisualAcuityRequest
    ) {
        if (_visualMeasurementMetaOffline.value == visualAcuityRequest) {
            return
        }
        _visualMeasurementMetaOffline.value = visualAcuityRequest
    }

    val haveImageExport = MutableLiveData<Boolean>()

    fun setImageExport(item: Boolean) {
        haveImageExport.value = item
    }
}
