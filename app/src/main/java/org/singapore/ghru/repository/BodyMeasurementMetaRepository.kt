package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.BodyMeasurementMetaDao
import org.singapore.ghru.jobs.SyncBodyMeasurementMetaJob
import org.singapore.ghru.vo.Message
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.StationDeviceData
import org.singapore.ghru.vo.request.*
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class BodyMeasurementMetaRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val bodyMeasurementMetaDao: BodyMeasurementMetaDao,
    private val nghruService: NghruService,
    private val jobManager: JobManager
) : Serializable {

    fun bodyMeasurementMeta(
        bodyMeasurementMeta: BodyMeasurementMeta
    ): LiveData<Resource<BodyMeasurementMeta>> {
        return object : MyNetworkBoundResource<BodyMeasurementMeta, ResourceData<Message>>(appExecutors) {
            override fun createJob(insertedID: Long) {
                bodyMeasurementMeta.id = insertedID
                jobManager.addJobInBackground(

                    SyncBodyMeasurementMetaJob(
                        bodyMeasurementMeta = bodyMeasurementMeta,
                        screeningId = bodyMeasurementMeta.screeningId
                    )
                )
            }

            override fun isNetworkAvilable(): Boolean {
                return bodyMeasurementMeta.syncPending
            }

            override fun saveDb(): Long {
                return bodyMeasurementMetaDao.insert(bodyMeasurementMeta)
            }


            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addBodyMeasurementMeta(bodyMeasurementMeta.screeningId, bodyMeasurementMeta)
            }
        }.asLiveData()
    }

//    fun heightWeightMeasurementMeta(
//        bodyMeasurementMeta: HeightWeightMeasurementMeta
//    ): LiveData<Resource<HeightWeightMeasurementMeta>> {
//        return object : MyNetworkBoundResource<HeightWeightMeasurementMeta, ResourceData<Message>>(appExecutors) {
//            override fun createJob(insertedID: Long) {
//                bodyMeasurementMeta.id = insertedID
//                jobManager.addJobInBackground(
//
//                    SyncBodyMeasurementMetaJob(
//                        bodyMeasurementMeta = bodyMeasurementMeta,
//                        screeningId = bodyMeasurementMeta.screeningId
//                    )
//                )
//            }
//
//            override fun isNetworkAvilable(): Boolean {
//                return bodyMeasurementMeta.syncPending
//            }
//
//            override fun saveDb(): Long {
//                return bodyMeasurementMetaDao.insert(bodyMeasurementMeta)
//            }
//
//
//            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
//                return nghruService.addBodyMeasurementMeta(bodyMeasurementMeta.screeningId, bodyMeasurementMeta)
//            }
//        }.asLiveData()
//    }

    fun getBodyMeasurementMetaListFromLocalDB(

    ): LiveData<Resource<List<BodyMeasurementMeta>>> {
        return object : LocalBoundResource<List<BodyMeasurementMeta>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<BodyMeasurementMeta>> {
                return bodyMeasurementMetaDao.getBodyMeasurementMetasSyncPending()
            }
        }.asLiveData()
    }

    fun syncBodyMeasurementMeta(
        bodyMeasurementMeta: BodyMeasurementMeta,
        screeningID : String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : SyncNetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return  nghruService.addBodyMeasurementMeta(screeningID,bodyMeasurementMeta)
            }

            override fun deleteCall() {
                bodyMeasurementMetaDao.deleteRequest(bodyMeasurementMeta.id)
            }
        }.asLiveData()
    }

    fun syncHeightAndWeight(
        HWRequest: BodyMeasurementMetaWithoutReadings,
        screening_id: String

    ): LiveData<Resource<ResourceData<BodyMeasurementMetaWithoutReadings>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<BodyMeasurementMetaWithoutReadings>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<BodyMeasurementMetaWithoutReadings>>> {
                return nghruService.syncHeightAndWeight(screening_id, HWRequest)
            }
        }.asLiveData()
    }

    fun syncHeightAndWeightManualAuto(
        HWRequest: BodyMeasurementMetaManualAuto,
        screening_id: String

    ): LiveData<Resource<ResourceData<BodyMeasurementMetaManualAuto>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<BodyMeasurementMetaManualAuto>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<BodyMeasurementMetaManualAuto>>> {
                return nghruService.syncHeightAndWeightWithManuAuto(screening_id, HWRequest)
            }
        }.asLiveData()
    }
}
