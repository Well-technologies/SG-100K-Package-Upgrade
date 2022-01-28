package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.SampleStorageRequestDao
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.SampleData
import org.singapore.ghru.vo.request.SampleStorageRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class SampleStorageRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val sampleStorageRequestDao: SampleStorageRequestDao
) : Serializable {


    fun syncSampleStorageRequest(
        sampleStorageRequest: SampleStorageRequest

    ): LiveData<Resource<ResourceData<SampleData>>> {
        return object : NetworkOnlyBoundResource<ResourceData<SampleData>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<SampleData>>> {
                return nghruService.addSampleStorageSync(sampleStorageRequest.sampleId, sampleStorageRequest)
            }
        }.asLiveData()
    }

    fun insertSampleRequest(
        sampleStorageRequest: SampleStorageRequest
    ): LiveData<Resource<SampleStorageRequest>> {
        return object : LocalBoundInsertResource<SampleStorageRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<SampleStorageRequest> {
                return sampleStorageRequestDao.getSampleStorageRequest(rowId)
            }

            override fun insertDb(): Long {
                return sampleStorageRequestDao.insert(sampleStorageRequest)
            }
        }.asLiveData()
    }


    fun syncSampleStorageFRequest(
        sampleStorageRequest: SampleStorageRequest

    ): LiveData<Resource<ResourceData<SampleData>>> {
        return object : NetworkOnlyBoundResource<ResourceData<SampleData>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<SampleData>>> {
                return nghruService.addSampleStorageSync(sampleStorageRequest.sampleId, sampleStorageRequest)
            }
        }.asLiveData()
    }

    fun insertSampleStorageRequest(
        sampleStorageRequest: SampleStorageRequest
    ): LiveData<Resource<SampleStorageRequest>> {
        return object : LocalBoundInsertResource<SampleStorageRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<SampleStorageRequest> {
                return sampleStorageRequestDao.getSampleStorageRequest(rowId)
            }

            override fun insertDb(): Long {
                return sampleStorageRequestDao.insert(sampleStorageRequest)
            }
        }.asLiveData()
    }

}
