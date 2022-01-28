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
import org.singapore.ghru.db.VisualAcuityRequestDao
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
class VisualAcuityRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val visualAcuityRequestDao: VisualAcuityRequestDao,
    private val jobManager : JobManager
) : Serializable {

    fun visualMeasurementMeta(
        visualAcuityRequest: VisualAcuityRequest
    ): LiveData<Resource<VisualAcuityRequest>> {
        return object : MyNetworkBoundResource<VisualAcuityRequest, ResourceData<Message>>(appExecutors) {
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
                return visualAcuityRequest.syncPending
            }

            override fun saveDb(): Long {
                return visualAcuityRequestDao.insert(visualAcuityRequest)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addVisualAcuitySync(visualAcuityRequest.screeningId, visualAcuityRequest)
            }
        }.asLiveData()
    }

//    fun syncVisualRequest(
//        spirometryRequest: SpirometryRequest
//    ): LiveData<Resource<ResourceData<CommonResponce>>> {
//        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<CommonResponce>>(appExecutors) {
//            override fun createCall(): LiveData<ApiResponse<ResourceData<CommonResponce>>> {
//                return nghruService.addSpirometrySync(spirometryRequest.screeningId, spirometryRequest)
//            }
//
//            override fun deleteCall() {
//                visualAcuityRequestDao.deleteRequest(spirometryRequest.id)
//            }
//        }.asLiveData()
//    }

}
