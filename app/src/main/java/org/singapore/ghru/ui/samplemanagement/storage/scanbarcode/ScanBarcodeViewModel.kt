package org.singapore.ghru.ui.samplemanagement.storage.scanbarcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.SampleRequestRepository
import org.singapore.ghru.repository.UserRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.User
import org.singapore.ghru.vo.request.SampleRequest
import javax.inject.Inject


class ScanBarcodeViewModel
@Inject constructor(sampleRequestRepository: SampleRequestRepository, userRepository: UserRepository) : ViewModel() {

    private val _sampleId: MutableLiveData<String> = MutableLiveData()

    private val _sampleIdOffline: MutableLiveData<String> = MutableLiveData()

    var sampleOffline: LiveData<Resource<SampleRequest>>? = Transformations
            .switchMap(_sampleIdOffline) { sampleIdOffline ->
                if (sampleIdOffline == null) {
                    AbsentLiveData.create()
                } else {
                    sampleRequestRepository.getSampleRequestByStorageIDOfflineByStorageID(sampleIdOffline)
                }
            }

    fun setSampleIdOffline(sampleIdOffline: String?) {
        if (_sampleIdOffline.value == sampleIdOffline) {
            return
        }
        _sampleIdOffline.value = sampleIdOffline
    }


    fun setSampleId(sampleId: String?) {
        if (_sampleId.value == sampleId) {
            return
        }
        _sampleId.value = sampleId
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
}
