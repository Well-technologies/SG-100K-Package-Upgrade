package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import com.google.gson.GsonBuilder
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.GripStrengthRequestDao
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
class GripStrengthRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val spiromentryRequestDao : GripStrengthRequestDao,
    private val jobManager : JobManager
) : Serializable {

    fun gripMeasurementMeta(
        bodyMeasurementMeta: GripStrengthRequest
    ): LiveData<Resource<GripStrengthRequest>> {
        return object : MyNetworkBoundResource<GripStrengthRequest, ResourceData<Message>>(appExecutors) {
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
                return bodyMeasurementMeta.syncPending
            }

            override fun saveDb(): Long {
                return spiromentryRequestDao.insert(bodyMeasurementMeta)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addGripStrengthSync(bodyMeasurementMeta.screeningId, bodyMeasurementMeta)
            }
        }.asLiveData()
    }


//    fun getSpirometryRequestFromLocalDB(
//
//    ): LiveData<Resource<List<SpirometryRequest>>> {
//        return object : LocalBoundResource<List<SpirometryRequest>>(appExecutors) {
//            override fun loadFromDb(): LiveData<List<SpirometryRequest>> {
//
//                var requestList : LiveData<List<SpirometryRequest>> = spiromentryRequestDao.getSpirometryRequestSyncPending()
//                return requestList
//            }
//        }.asLiveData()
//    }

    fun syncSpirometryRequest(
        spirometryRequest: SpirometryRequest
    ): LiveData<Resource<ResourceData<CommonResponce>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<CommonResponce>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<CommonResponce>>> {
                return nghruService.addSpirometrySync(spirometryRequest.screeningId, spirometryRequest)
            }

            override fun deleteCall() {
                spiromentryRequestDao.deleteRequest(spirometryRequest.id)
            }
        }.asLiveData()
    }

}
