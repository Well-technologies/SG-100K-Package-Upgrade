package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.CancelRequestDao
import org.singapore.ghru.db.SampleRequestDao
import org.singapore.ghru.jobs.SyncCancelrequestJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.CancelRequest
import org.singapore.ghru.vo.request.CancelRequestWithMeta
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.SampleRequest
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */

@Singleton
class CancelRequestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val sampleRequestDao: SampleRequestDao,
    private val jobManager: JobManager,
    private val cancelRequestDao : CancelRequestDao
    ) {
///ecg cancel request
    fun syncCancelRequest(
        participantRequest: ParticipantRequest,
        cancelRequest: CancelRequest
    ): LiveData<Resource<MessageCancel>>{

        return object : MyNetworkBoundResource<MessageCancel,ResourceData<MessageCancel>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                cancelRequest.id = insertedID
                cancelRequest.createdDateTime = getLocalTimeString()
                jobManager.addJobInBackground(SyncCancelrequestJob(participantRequest!!, cancelRequest))
            }
            override fun isNetworkAvilable(): Boolean {

                return cancelRequest.syncPending
            }
            override fun saveDb(): Long {
                cancelRequest.createdDateTime = getLocalTimeString()
                return cancelRequestDao.insert(cancelRequest)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<MessageCancel>>> {
                return nghruService.addCancelRequest(participantRequest.screeningId, cancelRequest)
            }

        }.asLiveData()
    }

    // new cancel request

    fun newSyncCancelRequest(
        participantRequest: ParticipantRequest,
        cancelRequest: CancelRequest
    ): LiveData<Resource<MessageCancel>>{

        return object : MyNetworkBoundResource<MessageCancel,ResourceData<MessageCancel>>(appExecutors) {

            override fun createJob(insertedID: Long) {
                cancelRequest.id = insertedID
                cancelRequest.createdDateTime = getLocalTimeString()
                jobManager.addJobInBackground(SyncCancelrequestJob(participantRequest!!, cancelRequest))
            }
            override fun isNetworkAvilable(): Boolean {

                return cancelRequest.syncPending
            }
            override fun saveDb(): Long {
                cancelRequest.createdDateTime = getLocalTimeString()
                return cancelRequestDao.insert(cancelRequest)
            }
            override fun createCall(): LiveData<ApiResponse<ResourceData<MessageCancel>>> {
                return nghruService.addNewCancelRequest(participantRequest.screeningId, cancelRequest)
            }

        }.asLiveData()
    }

    fun syncCancelRequest(
        sampleRequest: SampleRequest,
        cancelRequest: CancelRequest
    ): LiveData<Resource<ResourceData<Message>>> {

        return object : NetworkOnlyBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCancelSampleRequest(sampleRequest.sampleId, cancelRequest)
            }
        }.asLiveData()
    }


    fun syncCancelStorageRequest(
        sampleRequest: SampleRequest,
        cancelRequest: CancelRequest
    ): LiveData<Resource<ResourceData<Message>>> {

        return object : NetworkOnlyBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCancelStorageRequest(sampleRequest.storageId!!, cancelRequest)
            }
        }.asLiveData()
    }

    fun delete(
        sampleRequest: SampleRequest
    ): LiveData<Resource<SampleRequest>> {
        return object : LocalBoundIDeleteResource<SampleRequest>(appExecutors) {
            override fun deleteDb() {
                return sampleRequestDao.delete(sampleRequest)
            }

        }.asLiveData()
    }

    fun getCancelRequestListFromLocalDB(

    ): LiveData<Resource<List<CancelRequest>>> {
        return object : LocalBoundResource<List<CancelRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<CancelRequest>> {
                return cancelRequestDao.getCancelRequestSyncPending()
            }
        }.asLiveData()
    }
    fun getLocalTimeString(): String {
        val s = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US)
        return s.format(Date())
    }

    fun syncCancel(
        cancelRequest: CancelRequest
    ): LiveData<Resource<ResourceData<MessageCancel>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<MessageCancel>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<MessageCancel>>> {
                return nghruService.addCancelRequest(cancelRequest.screeningId, cancelRequest)
            }

            override fun deleteCall() {
                cancelRequestDao.deleteRequest(cancelRequest.id)
            }
        }.asLiveData()
    }

    fun syncCancelRequestWithMeta(
        participantRequest: ParticipantRequest,
        cancelRequest: CancelRequestWithMeta
    ): LiveData<Resource<ResourceData<MessageCancel>>> {

        return object : NetworkOnlyBoundResource<ResourceData<MessageCancel>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<MessageCancel>>> {
                return nghruService.addNewCancelRequestWithMeta(participantRequest.screeningId, cancelRequest)
            }
        }.asLiveData()
    }
}
