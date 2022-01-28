package org.singapore.ghru.ui.enumeration.manualentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.HouseholdRequestRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.request.HouseholdRequestMeta
import javax.inject.Inject

class ManualEntryViewModel @Inject constructor(householdRequestRepository: HouseholdRequestRepository) : ViewModel() {

    private val _invitationId: MutableLiveData<String> = MutableLiveData()

    var householdRequestCheck: LiveData<Resource<HouseholdRequestMeta>>? = Transformations
        .switchMap(_invitationId) { householdRequest ->
            if (householdRequest == null) {
                AbsentLiveData.create()
            } else {
                householdRequestRepository.getItemId(householdRequest)
            }
        }

    fun getItemId(screeningId: String?) {
        _invitationId.value = screeningId
    }
}