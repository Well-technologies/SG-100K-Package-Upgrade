package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.ParticipantRequestDao
import org.singapore.ghru.vo.Participant
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.ResourceData
import org.singapore.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class ParticipantRequestRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val participantRequestRequestDao: ParticipantRequestDao,
    private val nghruService: NghruService
) : Serializable {

    fun syncParticipantRequest(
        participantRequest: ParticipantRequest
    ): LiveData<Resource<ResourceData<Participant>>> {
        return object : NetworkOnlyBoundResource<ResourceData<Participant>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Participant>>> {
                return nghruService.addParticipantRequest(participantRequest)
            }
        }.asLiveData()
    }

    fun insertParticipantRequest(
        participantRequest: ParticipantRequest
    ): LiveData<Resource<ParticipantRequest>> {
        return object : LocalBoundInsertResource<ParticipantRequest>(appExecutors) {
            override fun loadFromDb(rowId: Long): LiveData<ParticipantRequest> {
                return participantRequestRequestDao.getParticipantRequest(rowId)
            }

            override fun insertDb(): Long {
                return participantRequestRequestDao.insert(participantRequest)
            }
        }.asLiveData()
    }

    fun getParticipantRequestsBySyncStatus(
        syncStatus: Boolean
    ): LiveData<Resource<List<ParticipantRequest>>> {
        return object : LocalBoundResource<List<ParticipantRequest>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<ParticipantRequest>> {
                return participantRequestRequestDao.getParticipantRequestsBySyncStatus(syncStatus)
            }
        }.asLiveData()
    }


}
