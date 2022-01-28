package org.singapore.ghru.ui.fundoscopy.reading.missingvalues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.util.AbsentLiveData
import org.singapore.ghru.vo.ECG
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.request.ParticipantRequest
import javax.inject.Inject


class MissingDialogViewModel
@Inject constructor(
    fundoscopyRepository: FundoscopyRepository
    ) : ViewModel() {

    private val _participantIdComplte: MutableLiveData<ParticipantRequest> = MutableLiveData()
    private var comment: String? = null
    private var device_id: String? = null
    private var isOnline : Boolean = false

    private val _stationName = MutableLiveData<String>()

    private var contraindications : List<Map<String, String>>? = null
    private var confirmQuestions : List<Map<String, String>>? = null

    var fundoscopyComplete: LiveData<Resource<ECG>>? = Transformations
        .switchMap(_participantIdComplte) { participantId ->
            if (participantId == null) {
                AbsentLiveData.create()
            } else {
                fundoscopyRepository.syncFundoscopy(participantId, comment, device_id,isOnline,contraindications,confirmQuestions)
            }
        }

    fun setParticipantComplete(participantId: ParticipantRequest, mComment: String?, mDevice_id: String, online: Boolean, indications : List<Map<String, String>>, questions : List<Map<String, String>>) {
        comment = mComment
        device_id = mDevice_id
        isOnline = online
        contraindications = indications
        confirmQuestions = questions

        if (_participantIdComplte.value == participantId) {
            return
        }
        _participantIdComplte.value = participantId
    }

}
