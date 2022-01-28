package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.ParticipantRequestDao
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.HL7Readings
import org.singapore.ghru.vo.request.ParticipantRequest
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class ParticipantRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val nghruService: NghruService,
    private val participantRequestDao: ParticipantRequestDao
) : Serializable {
    fun getParticipant(
        screeningId: String
    ): LiveData<Resource<ResourceData<Participant>>> {
        return object : NetworkOnlyBoundResource<ResourceData<Participant>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Participant>>> {
                return nghruService.getParticipant(screeningId)
            }
        }.asLiveData()
    }

    fun getParticipantRequest(
        screeningId: String, station: String
    ): LiveData<Resource<ResourceData<ParticipantRequest>>> {
        return object : NetworkOnlyBoundResource<ResourceData<ParticipantRequest>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantRequest>>> {
                return nghruService.getParticipantRequest(screeningId, station)
            }
        }.asLiveData()
    }


    fun getParticipantOffline(
        screeningId: String
    ): LiveData<Resource<ParticipantRequest>> {
        return object : LocalBoundResource<ParticipantRequest>(appExecutors) {
            override fun loadFromDb(): LiveData<ParticipantRequest> {
                return participantRequestDao.getParticipantRequestByScreenId(screeningId)
            }
        }.asLiveData()
    }

    fun getParticipantByIdnumber(
        idNumber: String
    ): LiveData<Resource<ParticipantRequest>> {
        return object : LocalBoundResource<ParticipantRequest>(appExecutors) {
            override fun loadFromDb(): LiveData<ParticipantRequest> {
                return participantRequestDao.getParticipantByIdnumber(idNumber)
            }
        }.asLiveData()
    }

    fun getItemId(
        screeningId: String
    ): LiveData<Resource<ParticipantRequest>> {
        return object : NetworkBoundResource<ParticipantRequest, ResourceData<ParticipantRequest>>(appExecutors) {
            override fun saveCallResult(item: ResourceData<ParticipantRequest>) {
                participantRequestDao.insert(item.data!!)
            }


            override fun shouldFetch(data: ParticipantRequest?): Boolean = data == null

            override fun loadFromDb(): LiveData<ParticipantRequest> {
                return participantRequestDao.getParticipantRequestByScreenId(screeningId)
            }

            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantRequest>>> {
                return nghruService.getParticipantRequest(screeningId)
            }

        }.asLiveData()
    }

    fun hL7Readings(
        screeningId: String, station: String
    ): LiveData<Resource<ResourceData<HL7Readings>>> {
        return object : NetworkOnlyBoundResource<ResourceData<HL7Readings>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<HL7Readings>>> {
                return nghruService.getHL7Readings(screeningId, station)
            }
        }.asLiveData()
    }

    fun getSingleParticipantStations(
        participantID: String):
            LiveData<Resource<ResourceData<ParticipantStationsData<List<ParticipantStation>>>>> {

        return object : NetworkOnlyBoundResource<ResourceData<ParticipantStationsData<List<ParticipantStation>>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<ParticipantStationsData<List<ParticipantStation>>>>> {
                return nghruService.getSingleParticipant(participantID)
            }
        }.asLiveData()
    }

    fun getAllParticipantStations(
        page: Int,
        keyWord: String):
            LiveData<Resource<ResourceDataWithMeta<List<ParticipantStationItemNewNew>>>> {

        return object : NetworkOnlyBoundResource<ResourceDataWithMeta<List<ParticipantStationItemNewNew>>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceDataWithMeta<List<ParticipantStationItemNewNew>>>> {
                return nghruService.allParticipants(page, keyWord)
            }
        }.asLiveData()
    }

    fun filterStationParticipants(
        page:Int, station:String, status: String, date:String, sort: String):
            LiveData<Resource<ResourceDataWithMeta<StationParticipant>>> {

        return object : NetworkOnlyBoundResource<ResourceDataWithMeta<StationParticipant>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceDataWithMeta<StationParticipant>>> {
                return nghruService.getStationParticipants(page, station, status, date, sort)
            }
        }.asLiveData()
    }

    fun filterNotStartedStationParticipants(
        page: Int, station:String, status: String, date:String, sort: String):
            LiveData<Resource<ResourceDataWithMeta<StationParticipant>>> {

        return object : NetworkOnlyBoundResource<ResourceDataWithMeta<StationParticipant>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceDataWithMeta<StationParticipant>>> {
                return nghruService.getNotStartedStationParticipants(page, station, status, date, sort)
            }
        }.asLiveData()
    }
}
