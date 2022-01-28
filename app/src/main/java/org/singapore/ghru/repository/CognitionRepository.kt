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
class CognitionRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val cognitionRequestDao: CognitionRequestDao
) : Serializable {

    fun cognitionMeta(
        cognitionRequest: CognitionRequest
    ): LiveData<Resource<CognitionRequest>> {
        return object : MyNetworkBoundResource<CognitionRequest, ResourceData<Message>>(appExecutors) {
            override fun createJob(insertedID: Long) {
            }

            override fun isNetworkAvilable(): Boolean {
                return cognitionRequest.syncPending
            }

            override fun saveDb(): Long {
                return cognitionRequestDao.insert(cognitionRequest)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCognitionSync(cognitionRequest.screeningId, cognitionRequest)
            }
        }.asLiveData()
    }

    fun updateCog(
        cogRequest: CognitionRequestNew,
        screening_id: String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.updateCog(cogRequest,screening_id)
            }
        }.asLiveData()
    }

    fun syncCogNew(
        cogRequest: CognitionRequestNew1,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addCogSyncNew(screening_id, cogRequest)
            }
        }.asLiveData()
    }

}
