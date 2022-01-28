package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import com.google.gson.GsonBuilder
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.*
import org.singapore.ghru.jobs.SyncBodyMeasurementMetaJob
import org.singapore.ghru.jobs.SyncSpirometryJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.BodyMeasurementMeta
import org.singapore.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class FFQRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val ffqRequestDao: FFQRequestDao,
    private val jobManager : JobManager
) : Serializable {

    fun ffqMeta(
        ffqRequest: FFQRequest
    ): LiveData<Resource<FFQRequest>> {
        return object : MyNetworkBoundResource<FFQRequest, ResourceData<Message>>(appExecutors) {
            override fun createJob(insertedID: Long) {
            }

            override fun isNetworkAvilable(): Boolean {
                return ffqRequest.syncPending
            }

            override fun saveDb(): Long {
                return ffqRequestDao.insert(ffqRequest)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addFFQSync(ffqRequest.screeningId, ffqRequest)
            }
        }.asLiveData()
    }

    fun getParticipantCredintials(
        screeningId: String
    ): LiveData<Resource<ResourceData<ParticipantCre>>> {
        return object : NetworkOnlyBoundResource<ResourceData<ParticipantCre>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantCre>>> {
                return nghruService.getParticipantCredin(screeningId)
            }
        }.asLiveData()
    }

    fun updateFFQ(
        ffqRequest: FFQRequestNew,
        screening_id: String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.updateFFQ(ffqRequest,screening_id)
            }
        }.asLiveData()
    }

    fun syncFFQNew(
        ffqRequest: FFQRequestNew1,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addFFQSyncNew(screening_id, ffqRequest)
            }
        }.asLiveData()
    }

    fun getFFQLanguage(
        screeningId: String,
        langauge: String):
            LiveData<Resource<ResourceData<ParticipantCre>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantCre>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantCre>>> {
                return nghruService.getFFQLanguage(screeningId, langauge)
            }
        }.asLiveData()
    }

}
