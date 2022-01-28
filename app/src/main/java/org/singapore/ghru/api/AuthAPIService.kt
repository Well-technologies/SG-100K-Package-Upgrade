package org.singapore.ghru.api

import com.pixplicity.easyprefs.library.Prefs
import okhttp3.OkHttpClient
import org.singapore.ghru.BuildConfig
import org.singapore.ghru.vo.AccessToken
import org.singapore.ghru.vo.RefreshToken
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Prefs.getString("Ipaddress", BuildConfig.SERVER_URL))
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}

interface AuthAPI {

    @Headers("Content-Type: application/json")
    @POST("api/refresh")
    fun refreshToken(@Body refreshToken: RefreshToken): Call<AccessToken>

}

class AuthAPIService {

    fun refreshToken(@Body refreshToken: RefreshToken): Response<AccessToken>{
        val service = ServiceBuilder.buildService(AuthAPI::class.java)
        val callSync: Call<AccessToken> = service.refreshToken(refreshToken)

        return callSync.execute()
    }

}