package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.CheckoutRequest
import org.singapore.ghru.vo.request.OctaRequest
import org.singapore.ghru.vo.request.SkinRequest
import org.singapore.ghru.vo.request.VicorderRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class VicorderRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService
) : Serializable {

    fun syncVicorder(
        vicorderRequest: VicorderRequest,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addVicorder(screening_id, vicorderRequest)
            }
        }.asLiveData()
    }

    fun syncSkin(
        skinRequest: SkinRequest,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addSkin(screening_id, skinRequest)
            }
        }.asLiveData()
    }

    fun syncOcta(
        octaRequest: OctaRequest,
        screening_id: String

    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.addOcta(screening_id, octaRequest)
            }
        }.asLiveData()
    }
}
