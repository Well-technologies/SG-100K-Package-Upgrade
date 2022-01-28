package org.singapore.ghru.ui.registerpatient_new.basicdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.ParticipantMetaRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Date
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.User
import org.singapore.ghru.vo.request.Gender
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject

class BasicDetailsViewModelNew
@Inject constructor(userRepository: UserRepository, participantMetaRepository: ParticipantMetaRepository) : ViewModel() {

    var gender: MutableLiveData<String> = MutableLiveData<String>()

    var birthYear: Int = 1998

    var birthDate: MutableLiveData<String> = MutableLiveData<String>()

    var birthDateVal: MutableLiveData<Date> = MutableLiveData<Date>()

    var birthYearVal: MutableLiveData<String> = MutableLiveData<String>()

    var contactNo: MutableLiveData<String> = MutableLiveData<String>()

    var age: MutableLiveData<String> = MutableLiveData<String>()


    fun setGender(g: Gender) {
        gender.postValue(g.gender)
    }

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

    private val _screeningId: MutableLiveData<String> = MutableLiveData()

    var screeningIdCheck: LiveData<Resource<ParticipantRequest>>? = Transformations
        .switchMap(_screeningId) { screeningId ->
            if (screeningId == null) {
                AbsentLiveData.create()
            } else {
                participantMetaRepository.getItemId(screeningId)
            }
        }

    fun setScreeningId(screeningId: String?) {
        _screeningId.value = screeningId
    }
}
