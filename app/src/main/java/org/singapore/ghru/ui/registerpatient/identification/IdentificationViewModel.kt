package org.singapore.ghru.ui.registerpatient.identification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.ParticipantRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class IdentificationViewModel
@Inject constructor(participantRepository: ParticipantRepository) : ViewModel() {
    private val _idNumber: MutableLiveData<String> = MutableLiveData()

    var participant: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_idNumber) { idNumber ->
            if (idNumber == null) {
                AbsentLiveData.create()
            } else {
                participantRepository.getParticipantByIdnumber(idNumber)
            }
        }


    fun setIdNumber(idNumber: String?) {
        if (_idNumber.value == idNumber) {
            return
        }
        _idNumber.value = idNumber
    }
}
