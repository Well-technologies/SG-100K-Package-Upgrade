package org.singapore.ghru.ui.skin.partialreadings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.BloodPressureRequestRepository
import org.singapore.ghru.repository.SampleRepository
import org.singapore.ghru.repository.TreadmillRepository
import org.singapore.ghru.repository.VicorderRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.BloodPressureMetaRequest
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.SampleCreateRequest
import org.singapore.ghru.vo.request.SkinRequest
import javax.inject.Inject


class PartialReadingsDialogViewModel
@Inject constructor(
    viorderRepository: VicorderRepository
) : ViewModel() {

    //    Post Vicorder ------------------------------------------------------------------------------------------

    private val _skinPostRequest: MutableLiveData<SkinRequest> = MutableLiveData()
    private var _participant: String? = null

    fun setPostSkin(skinRequest: SkinRequest, participantId: String?) {
        _participant = participantId
        if (_skinPostRequest.value == skinRequest) {
            return
        }
        _skinPostRequest.value = skinRequest
    }

    var skinPostComplete: LiveData<Resource<ResourceData<Message>>>? = Transformations
        .switchMap(_skinPostRequest) { skinPostRequest ->
            if (skinPostRequest == null) {
                AbsentLiveData.create()
            } else {
                viorderRepository.syncSkin(skinPostRequest,_participant!!)
            }
        }

    // ----------------------------------------------------------------------------
}