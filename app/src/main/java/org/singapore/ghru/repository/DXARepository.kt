package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.DXARequestDao
import org.singapore.ghru.jobs.SyncDXAJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.DXARequest
import org.singapore.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class DXARepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val dxaRequestDao: DXARequestDao
    ) : Serializable {

    fun syncDXA(
        participantRequest: ParticipantRequest,
        dxaBody: String?,
        isOnline : Boolean
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                val mDXARequest =  DXARequest(meta = participantRequest.meta, body = dxaBody)
                mDXARequest.id = insertedID
                mDXARequest.screeningId = participantRequest?.screeningId!!
                mDXARequest.syncPending = !isOnline
                jobManager.addJobInBackground(
                    SyncDXAJob(
                        participantRequest,
                        mDXARequest
                    )
                )
            }
            override fun isNetworkAvilable(): Boolean {
                return !isOnline
            }
            override fun saveDb(): Long {

                val mDXARequest =  DXARequest(meta = participantRequest.meta, body = dxaBody)
                mDXARequest.screeningId = participantRequest?.screeningId!!
                mDXARequest.syncPending = !isOnline
                var id =  dxaRequestDao.insert(mDXARequest)
                return id
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                val mDXARequest =  DXARequest(meta = participantRequest.meta, body = dxaBody)
                return nghruService.addDXAGSync(
                    participantRequest.screeningId,
                    mDXARequest
                )
            }
        }.asLiveData()

    }

    fun getDXARequestFromLocalDB(
    ): LiveData<Resource<List<DXARequest>>> {
        return object : LocalBoundResource<List<DXARequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<DXARequest>> {
                return dxaRequestDao.getDXARequestSyncPending()
            }
        }.asLiveData()
    }

    fun syncDXARequest(
        dxaRequest: DXARequest
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addDXAGSync(dxaRequest?.screeningId,dxaRequest)
            }

            override fun deleteCall() {
                dxaRequestDao.deleteRequest(dxaRequest.id)
            }
        }.asLiveData()
    }
}
