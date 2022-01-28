package org.singapore.ghru.ui.ecg.trace.complete

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.ECGRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECG
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class CompleteDialogViewModel
@Inject constructor(eCGRepository: ECGRepository) : ViewModel() {

//    private val _participantRequestRemote: MutableLiveData<ECGId> = MutableLiveData()
//    private var isOnline : Boolean = false
//    private var contraindications: List<Map<String, String>>? = null
//
////    var eCGSaveRemote: LiveData<Resource<ResourceData<ECG>>>? = Transformations
////            .switchMap(_participantRequestRemote) { participant ->
////                if (participant == null) {
////                    AbsentLiveData.create()
////                } else {
////                    eCGRepository.syncECG(participant)
////                }
////            }
//
//    var eCGSaveRemote: LiveData<Resource<ECG>>? = Transformations
//        .switchMap(_participantRequestRemote) { input ->
//            input.ifExists { participantRequest, status, comment, device_id ->
//                eCGRepository.syncECG(participantRequest, status, comment, device_id, isOnline, contraindications)
//            }
//        }
//
//    fun setECGRemote(participantRequest: ParticipantRequest, status: String, comment: String?, device_id: String, online : Boolean, contra: List<Map<String, String>>) {
//
//        isOnline = online
//        contraindications = contra
//        val update = ECGId(participantRequest, status, comment, device_id)
//        if (_participantRequestRemote.value == update) {
//            return
//        }
//        _participantRequestRemote.value = update
////        if (_participantRequestRemote.value != participantRequest) {
////            _participantRequestRemote.postValue(participantRequest)
////        }
//    }
//
//    data class ECGId(
//        val participantRequest: ParticipantRequest?,
//        val status: String?,
//        val comment: String?,
//        val device_id: String
//    ) {
//        fun <T> ifExists(f: (ParticipantRequest, String, String?, String) -> LiveData<T>): LiveData<T> {
//            return if (participantRequest == null || status.isNullOrBlank()) {
//                AbsentLiveData.create()
//            } else {
//                f(participantRequest, status!!, comment, device_id)
//            }
//        }
//    }
}
