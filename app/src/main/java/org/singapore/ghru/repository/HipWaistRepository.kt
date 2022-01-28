package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import com.google.gson.GsonBuilder
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.GripStrengthRequestDao
import org.singapore.ghru.db.HipWaistRequestDao
import org.singapore.ghru.db.SpiromentryRequestDao
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
class HipWaistRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val hipwaistRequestDao : HipWaistRequestDao,
    private val jobManager : JobManager
) : Serializable {

    fun hipMeasurementMeta(
        hipMeasurementMeta: HipWaistRequest
    ): LiveData<Resource<HipWaistRequest>> {
        return object : MyNetworkBoundResource<HipWaistRequest, ResourceData<Message>>(appExecutors) {
            override fun createJob(insertedID: Long) {
//                bodyMeasurementMeta.id = insertedID
//                jobManager.addJobInBackground(
//
//                    SyncBodyMeasurementMetaJob(
//                        bodyMeasurementMeta = bodyMeasurementMeta,
//                        screeningId = bodyMeasurementMeta.screeningId
//                    )
//                )
            }

            override fun isNetworkAvilable(): Boolean {
                return hipMeasurementMeta.syncPending
            }

            override fun saveDb(): Long {
                return hipwaistRequestDao.insert(hipMeasurementMeta)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addHipWaistSync(hipMeasurementMeta.screeningId, hipMeasurementMeta)
            }
        }.asLiveData()
    }

    fun syncSpirometryRequest(
        spirometryRequest: SpirometryRequest
    ): LiveData<Resource<ResourceData<CommonResponce>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<CommonResponce>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<CommonResponce>>> {
                return nghruService.addSpirometrySync(spirometryRequest.screeningId, spirometryRequest)
            }

            override fun deleteCall() {
                hipwaistRequestDao.deleteRequest(spirometryRequest.id)
            }
        }.asLiveData()
    }

}
