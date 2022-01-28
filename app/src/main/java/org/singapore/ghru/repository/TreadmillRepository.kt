package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.TreadmillRequestDao
import org.singapore.ghru.jobs.SyncTreadmillJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.TreadmillRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class TreadmillRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val treadmillRequestDao: TreadmillRequestDao
    ) : Serializable {

    fun syncTreadmill(
        participantRequest: ParticipantRequest,
        treadmillBody: String?,
        isOnline : Boolean
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                val mTreadmillRequest =  TreadmillRequest(meta = participantRequest.meta, body = treadmillBody)
                mTreadmillRequest.id = insertedID
                mTreadmillRequest.screeningId = participantRequest?.screeningId!!
                mTreadmillRequest.syncPending = !isOnline
                jobManager.addJobInBackground(
                    SyncTreadmillJob(
                        participantRequest,
                        mTreadmillRequest
                    )

                )
            }
            override fun isNetworkAvilable(): Boolean {
                return !isOnline
            }
            override fun saveDb(): Long {

                val mTreadmillRequest =  TreadmillRequest(meta = participantRequest.meta, body = treadmillBody)
                mTreadmillRequest.screeningId = participantRequest?.screeningId!!
                mTreadmillRequest.syncPending = !isOnline
                var id =  treadmillRequestDao.insert(mTreadmillRequest)
                return id
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                val mTreadmillRequest =  TreadmillRequest(meta = participantRequest.meta, body = treadmillBody)
                return nghruService.addTreadmillGSync(
                    participantRequest.screeningId,
                    mTreadmillRequest
                )
            }
        }.asLiveData()

    }

    fun getTreadmillRequestFromLocalDB(
    ): LiveData<Resource<List<TreadmillRequest>>> {
        return object : LocalBoundResource<List<TreadmillRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<TreadmillRequest>> {
                return treadmillRequestDao.getTreadmillRequestSyncPending()
            }
        }.asLiveData()
    }

    fun syncTreadmillRequest(
        treadmillRequest: TreadmillRequest
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addTreadmillGSync(treadmillRequest?.screeningId,treadmillRequest)
            }

            override fun deleteCall() {
                treadmillRequestDao.deleteRequest(treadmillRequest.id)
            }
        }.asLiveData()
    }

    fun getEcgData(
        screeningId: String
    ): LiveData<Resource<ResourceData<ECGData>>> {
        return object : NetworkOnlyBoundResource<ResourceData<ECGData>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECGData>>> {
                return nghruService.getEcgData(screeningId)
            }
        }.asLiveData()
    }
}
