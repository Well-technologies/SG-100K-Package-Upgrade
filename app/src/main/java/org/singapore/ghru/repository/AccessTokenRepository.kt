package org.singapore.ghru.repository

import androidx.lifecycle.LiveData
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.api.ApiResponse
import org.singapore.ghru.api.NghruService
import org.singapore.ghru.db.AccessTokenDao
import org.singapore.ghru.util.RateLimiter
import org.singapore.ghru.util.TokenManager
import org.singapore.ghru.vo.AccessToken
import org.singapore.ghru.vo.RefreshToken
import org.singapore.ghru.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */

@Singleton
class AccessTokenRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val accessTokenDao: AccessTokenDao,
    private val nghruService: NghruService,
    private val tokenManager: TokenManager

) {

    private val repoListRateLimit = RateLimiter<AccessToken>(1, TimeUnit.SECONDS)

    fun loginUser(
        email: String,
        password: String,
        isOnline : Boolean
    ): LiveData<Resource<AccessToken>> {
        return object : NetworkBoundResource<AccessToken, AccessToken>(appExecutors) {
            override fun saveCallResult(item: AccessToken) {
                tokenManager.saveToken(item);
                item.userName = email
                item.passwordEN = password
                accessTokenDao.insert(item);
            }

            override fun shouldFetch(data: AccessToken?) = isOnline

            override fun loadFromDb(): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmailPasword(email)
            }

            override fun createCall(): LiveData<ApiResponse<AccessToken>> {
                val params = HashMap<String, String>()
                params["username"] = email
                params["password"] = password
                return nghruService.getAccessToken(params)
            }
        }.asLiveData()
    }

    fun deleteToken(): Int {
        return accessTokenDao.nukeTable()
    }


    fun refreshToken(
        accessToken: AccessToken
    ): LiveData<Resource<AccessToken>> {
        return object : NetworkOnlyBoundResource<AccessToken>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<AccessToken>> {
                return nghruService.getRefresh(
                    RefreshToken(
                        refresh_token = accessToken.refreshToken!!
                    )
                )
            }
        }.asLiveData()
    }

    fun getTokerByEmail(
        email: String
    ): LiveData<Resource<AccessToken>> {
        return object : LocalBoundResource<AccessToken>(appExecutors) {
            override fun loadFromDb(): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmail(email)
            }
        }.asLiveData()
    }

    fun setLogout(accessToken: AccessToken
    ): LiveData<Resource<AccessToken>> {
        return object : LocalBoundUpateResource<AccessToken, Int>(appExecutors) {
            override fun loadFromDb(rowId: Int): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmail(accessToken.userName)
            }

            override fun updateDb(): Int {
                return accessTokenDao.logout(accessToken)

            }

        }.asLiveData()
    }

    fun loginUserNew(
        email: String,
        isOnline : Boolean
    ): LiveData<Resource<AccessToken>> {
        return object : NetworkBoundResource<AccessToken, AccessToken>(appExecutors) {
            override fun saveCallResult(item: AccessToken) {
                tokenManager.saveToken(item);
                item.userName = email
                item.passwordEN = "Qwerty123#"
                accessTokenDao.insert(item);
            }

            override fun shouldFetch(data: AccessToken?) = isOnline

            override fun loadFromDb(): LiveData<AccessToken> {
                return accessTokenDao.getTokerByEmail(email)
            }

            override fun createCall(): LiveData<ApiResponse<AccessToken>> {
                val params = HashMap<String, String>()
                params["username"] = email
                return nghruService.getAccessTokenNew(params)
            }
        }.asLiveData()
    }
}
