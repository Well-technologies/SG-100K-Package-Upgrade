package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.BodyMeasurementMetaDao
import org.singapore.ghru.db.BodyMeasurementRequestDao
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class BodyMeasurementRequestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val bodyMeasurementRequestDao: BodyMeasurementRequestDao,
    //  private val ploodPresureRequestDao: BloodPresureItemRequestDao,
    private val bodyMeasurementMetaDao: BodyMeasurementMetaDao,
    private val nghruService: NghruService
) : Serializable {

    fun syncBodyMeasurementRequest(
        bodyMeasurementRequest: BodyMeasurementRequest,
        particap: ParticipantRequest
    ): LiveData<Resource<ResourceData<BodyMeasurementRequest>>> {
        return object : NetworkOnlyBoundResource<ResourceData<BodyMeasurementRequest>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<BodyMeasurementRequest>>> {
                return nghruService.addBodyMeasurementRequest(particap.screeningId, bodyMeasurementRequest)
            }
        }.asLiveData()
    }


    fun syncBodyMeasurementMeta(
        bodyMeasurementRequest: BodyMeasurementMeta,
        particap: ParticipantRequest
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addBodyMeasurementMeta(particap.screeningId, bodyMeasurementRequest)
            }
        }.asLiveData()
    }

    fun getBodyMeasurementMetaNew(
        particap: ParticipantRequest,
        isOnline : Boolean
    ): LiveData<Resource<ResourceData<StationData<BodyMeasurementMeta>>>> {
        return object : NetworkOnlyBoundResource<ResourceData<StationData<BodyMeasurementMeta>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<StationData<BodyMeasurementMeta>>>> {
                return nghruService.getBodyMeasurementMeta(particap.screeningId)
            }
        }.asLiveData()
    }

    fun getBodyMeasurementMetaNewNew(
        particap: ParticipantRequest,
        isOnline : Boolean
    ): LiveData<Resource<ResourceData<StationData<BodyMeasurementMetaNew>>>> {
        return object : NetworkOnlyBoundResource<ResourceData<StationData<BodyMeasurementMetaNew>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<StationData<BodyMeasurementMetaNew>>>> {
                return nghruService.getBodyMeasurementMetaNew(particap.screeningId)
            }
        }.asLiveData()
    }

//    fun getBodyMeasurementMetaLocal(
//        particap: ParticipantRequest
//    ): LiveData<Resource<BodyMeasurementMetaResonce>> {
//        return object : NetworkOnlyBoundResource<BodyMeasurementMetaResonce>(appExecutors) {
//            override fun createCall(): LiveData<ApiResponse<BodyMeasurementMetaResonce>> {
//                return nghruService.getBodyMeasurementMeta(particap.screeningId)
//            }
//        }.asLiveData()
//    }



//    fun getBodyMeasurementMeta(
//        particap: ParticipantRequest,
//        isOnline : Boolean
//    ): LiveData<Resource<BodyMeasurementMeta>> {
//        return object : NetworkBoundResource<BodyMeasurementMeta, ResourceData<Message>>(appExecutors) {
//            override fun saveCallResult(item: BodyMeasurementMetaResonce) {
////                val bodyMeasurementMeta = item.data?.station?.data!!
////                bodyMeasurementMeta.screeningId = particap.screeningId
////                bodyMeasurementMetaDao.insert(bodyMeasurementMeta)
//            }
//
//            override fun shouldFetch(data: BodyMeasurementMeta?) = isOnline
//
//            override fun loadFromDb(): LiveData<BodyMeasurementMeta> {
//               return bodyMeasurementMetaDao.getBodyMeasurementMetas(particap.screeningId)
//            }
//
//            override fun createCall(): LiveData<ApiResponse<ResourceData<BodyMeasurementMeta>>> {
//                return nghruService.getBodyMeasurementMeta(particap.screeningId)
//            }
//        }.asLiveData()
//    }


    fun insertBodyMeasurementRequest(
        participantRequest: BodyMeasurementRequest
    ): LiveData<Resource<BodyMeasurementRequest>> {
        return object : LocalBoundInsertResource<BodyMeasurementRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<BodyMeasurementRequest> {
                return bodyMeasurementRequestDao.getBodyMeasurementRequest(rowId)
            }

            override fun insertDb(): Long {
                return bodyMeasurementRequestDao.insert(participantRequest)
            }
        }.asLiveData()
    }


//    fun insertBPs(
//            members: List<BloodPresureItemRequest>,
//            bodyMeasurementRequestId: BodyMeasurementRequest
//    ): LiveData<Resource<List<BloodPresureItemRequest>>> {
//        return object : LocalBoundInsertAllResource<List<BloodPresureItemRequest>>(appExecutors) {
//            override fun loadFromDb(): LiveData<List<BloodPresureItemRequest>> {
//                return ploodPresureRequestDao.getBloodPressureRequest(bodyMeasurementRequestId.id)
//            }
//
//            override fun insertDb(): Unit {
//                return ploodPresureRequestDao.insertAll(members)
//            }
//        }.asLiveData()
//    }
}
