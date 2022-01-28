package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.QuestionnaireDao
import org.singapore.ghru.db.QuestionnaireSelfDao
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.HLQResponse
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository that handles User objects.
 */

@Singleton
class QuestionnaireSelfRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val questionnaireSelfDao: QuestionnaireSelfDao,
    private val nghruService: NghruService
) : Serializable {
//    fun getQuestionnaireSelf(
//        network: Boolean,
//        language :String
//    ): LiveData<Resource<QuestionnaireSelf>> {
//
//        return object : NetworkBoundResource<QuestionnaireSelf, ResourceData<QuestionnaireSelf>>(appExecutors) {
//            override fun saveCallResult(item: ResourceData<QuestionnaireSelf>) {
//               // item.data?.json?.replace("\r", "")?.replace("\n","")?.replace("\\" , "")
//                questionnaireSelfDao.insert(item.data!!)
//            }
//
//            override fun shouldFetch(data: QuestionnaireSelf?): Boolean = network
//
//            override fun loadFromDb(): LiveData<QuestionnaireSelf> {
//                return questionnaireSelfDao.getQuestionnaire()
//            }
//
//            override fun createCall(): LiveData<ApiResponse<ResourceData<QuestionnaireSelf>>> {
//                return nghruService.getQuestionnaireSelf(language)
//            }
//        }.asLiveData()
//    }


    fun getQuestionnaireSelfList(
        network: Boolean,
        language :String
    ): LiveData<Resource<List<QuestionnaireSelf>>> {

        return object : NetworkBoundResource<List<QuestionnaireSelf>, ResourceData<List<QuestionnaireSelf>>>(appExecutors) {
            override fun saveCallResult(item: ResourceData<List<QuestionnaireSelf>>) {
                // item.data?.json?.replace("\r", "")?.replace("\n","")?.replace("\\" , "")
                if(network){
                    questionnaireSelfDao.nukeTable()
                    questionnaireSelfDao.insert(item.data!!)
                }else{
                    questionnaireSelfDao.insert(item.data!!)
                }

            }

            override fun shouldFetch(data: List<QuestionnaireSelf>?): Boolean = network

            override fun loadFromDb(): LiveData<List<QuestionnaireSelf>> {
                return questionnaireSelfDao.getQuestionnaires()
            }

            override fun createCall(): LiveData<ApiResponse<ResourceData<List<QuestionnaireSelf>>>> {
                return nghruService.getQuestionnaireSelf()
            }
        }.asLiveData()
    }

    fun updateHLQSelf(
        hlqRequest: HLQResponse,
        screening_id: String
    ): LiveData<Resource<ResourceData<Message>>> {
        return object : NetworkOnlyBcakgroundBoundResource<ResourceData<Message>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ResourceData<Message>>> {
                return nghruService.updateHLQSelf(hlqRequest,screening_id)
            }
        }.asLiveData()
    }
}
