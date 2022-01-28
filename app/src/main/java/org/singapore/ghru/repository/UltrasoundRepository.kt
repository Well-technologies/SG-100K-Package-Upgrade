package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.UltrasoundRequestDao
import org.singapore.ghru.jobs.SyncUltrasoundJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.UltrasoundRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class UltrasoundRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val jobManager: JobManager,
    private val ultrasoundRequestDao: UltrasoundRequestDao
    ) : Serializable {

    fun syncUltrasound(
        participantRequest: ParticipantRequest,
        ultraBody: String?,
        isOnline : Boolean
    ): LiveData<Resource<ECG>> {
        return object : MyNetworkBoundResource<ECG,ResourceData<ECG>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                val mUltraRequest =  UltrasoundRequest(meta = participantRequest.meta, body = ultraBody)
                mUltraRequest.id = insertedID
                mUltraRequest.screeningId = participantRequest?.screeningId!!
                mUltraRequest.syncPending = !isOnline
                jobManager.addJobInBackground(
                    SyncUltrasoundJob(
                        participantRequest,
                        mUltraRequest
                    )

                )
            }
            override fun isNetworkAvilable(): Boolean {
                return !isOnline
            }
            override fun saveDb(): Long {

                val mUltraRequest =  UltrasoundRequest(meta = participantRequest.meta, body = ultraBody)
                mUltraRequest.screeningId = participantRequest?.screeningId!!
                mUltraRequest.syncPending = !isOnline
                var id =  ultrasoundRequestDao.insert(mUltraRequest)
                return id
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                val mUltraRequest =  UltrasoundRequest(meta = participantRequest.meta, body = ultraBody)
                return nghruService.addUltrasoundGSync(
                    participantRequest.screeningId,
                    mUltraRequest
                )
            }
        }.asLiveData()

    }

    fun getUltrasoundRequestFromLocalDB(
    ): LiveData<Resource<List<UltrasoundRequest>>> {
        return object : LocalBoundResource<List<UltrasoundRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<UltrasoundRequest>> {
                return ultrasoundRequestDao.getUltrasoundRequestSyncPending()
            }
        }.asLiveData()
    }

    fun syncUltrasoundRequest(
        ultrasoundRequest: UltrasoundRequest
    ): LiveData<Resource<ResourceData<ECG>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<ECG>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ECG>>> {
                return nghruService.addUltrasoundGSync(ultrasoundRequest?.screeningId,ultrasoundRequest)
            }

            override fun deleteCall() {
                ultrasoundRequestDao.deleteRequest(ultrasoundRequest.id)
            }
        }.asLiveData()
    }
}
