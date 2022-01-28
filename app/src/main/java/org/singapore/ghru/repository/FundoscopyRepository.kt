package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.FundoscopyRequestDao
import org.singapore.ghru.db.MetaNewDao
import org.singapore.ghru.jobs.SyncFundoscopyJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class FundoscopyRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val metaNewDao: MetaNewDao,
    private val fundoscopyRequestDao: FundoscopyRequestDao
    ) : Serializable {

//    fun syncFundoscopy(
//        participantRequest: ParticipantRequest,
//        comment: String?,
//        device_id: String?,
//        pupil_dilation:Boolean,
//        isOnline : Boolean,
//        cataractObservation : String,
//        contraindications : List<Map<String, String>>?
//    ): LiveData<Resource<ECG>> {
//        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {
//
//            override fun createJob(insertedID: Long) {
//                val mFundoscopyRequest =  FundoscopyRequest(comment = comment, device_id = device_id, meta = participantRequest?.meta,pupil_dilation = pupil_dilation,cataract_observation = cataractObservation)
//                mFundoscopyRequest.id = insertedID
//                mFundoscopyRequest.screeningId = participantRequest?.screeningId!!
//                mFundoscopyRequest.syncPending = !isOnline
//                mFundoscopyRequest.contraindications = contraindications
//                jobManager.addJobInBackground(
//                    SyncFundoscopyJob(
//                        participantRequest,
//                        mFundoscopyRequest
//                    )
//
//                )
//            }
//            override fun isNetworkAvilable(): Boolean {
//                return !isOnline
//            }
//            override fun saveDb(): Long {
//
//                //var ecgMetaNewId = metaNewDao.insert(participantRequest?.meta!!)
//                val mFundoscopyRequest =  FundoscopyRequest(comment = comment, device_id = device_id, meta = participantRequest?.meta,pupil_dilation = pupil_dilation,cataract_observation = cataractObservation)
//                mFundoscopyRequest.screeningId = participantRequest?.screeningId!!
//                //mFundoscopyRequest.fundoscopyMetaId = ecgMetaNewId
//                mFundoscopyRequest.syncPending = !isOnline
//                mFundoscopyRequest.contraindications = contraindications
//                var id =  fundoscopyRequestDao.insert(mFundoscopyRequest)
//                return id
//            }
//            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
//                val mFundoscopyRequest = FundoscopyRequest(
//                    comment = comment,
//                    device_id = device_id,
//                    meta = participantRequest?.meta,
//                    pupil_dilation = pupil_dilation,
//                    cataract_observation = cataractObservation
//                )
//                mFundoscopyRequest.contraindications = contraindications
//                return nghruService.addFundoscopyGSync(
//                    participantRequest.screeningId,
//                    mFundoscopyRequest
//                )
//            }
//        }.asLiveData()
//
//    }

    fun syncFundoscopy(
        participantRequest: ParticipantRequest,
        comment: String?,
        device_id: String?,
        isOnline : Boolean,
        contraindications : List<Map<String, String>>?,
        confirmQuestions : List<Map<String, String>>?
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                val mFundoscopyRequest =  FundoscopyRequest(
                    comment = comment,
                    device_id = device_id,
                    meta = participantRequest?.meta)
                mFundoscopyRequest.id = insertedID
                mFundoscopyRequest.screeningId = participantRequest?.screeningId!!
                mFundoscopyRequest.syncPending = !isOnline
                mFundoscopyRequest.contraindications = contraindications
                mFundoscopyRequest.confirm_questions = confirmQuestions
                jobManager.addJobInBackground(
                    SyncFundoscopyJob(
                        participantRequest,
                        mFundoscopyRequest
                    )

                )
            }
            override fun isNetworkAvilable(): Boolean {
                return !isOnline
            }
            override fun saveDb(): Long {

                //var ecgMetaNewId = metaNewDao.insert(participantRequest?.meta!!)
                val mFundoscopyRequest =  FundoscopyRequest(
                    comment = comment,
                    device_id = device_id,
                    meta = participantRequest?.meta)
                mFundoscopyRequest.screeningId = participantRequest?.screeningId!!
                //mFundoscopyRequest.fundoscopyMetaId = ecgMetaNewId
                mFundoscopyRequest.syncPending = !isOnline
                mFundoscopyRequest.contraindications = contraindications
                mFundoscopyRequest.confirm_questions = confirmQuestions
                var id =  fundoscopyRequestDao.insert(mFundoscopyRequest)
                return id
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                val mFundoscopyRequest = FundoscopyRequest(
                    comment = comment,
                    device_id = device_id,
                    meta = participantRequest?.meta
                )
                mFundoscopyRequest.contraindications = contraindications
                mFundoscopyRequest.confirm_questions = confirmQuestions
                return nghruService.addFundoscopyGSync(
                    participantRequest.screeningId,
                    mFundoscopyRequest
                )
            }
        }.asLiveData()

    }

    fun getFundoscopyRequestFromLocalDB(

    ): LiveData<Resource<List<FundoscopyRequest>>> {
        return object : LocalBoundResource<List<FundoscopyRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<FundoscopyRequest>> {
                return fundoscopyRequestDao.getFundoscopyRequestSyncPending()
            }
        }.asLiveData()
    }

    fun syncFundoscopyRequest(
        fundoscopyRequest: FundoscopyRequest
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addFundoscopyGSync(fundoscopyRequest?.screeningId,fundoscopyRequest)
            }

            override fun deleteCall() {

                fundoscopyRequestDao.deleteRequest(fundoscopyRequest.id)
            }
        }.asLiveData()
    }
}
