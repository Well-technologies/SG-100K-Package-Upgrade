package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.AccessToken


/**
 * Interface for database access for User related operations.
 */
@Dao
interface AccessTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accessToken: AccessToken)

    @Query("SELECT * FROM access_token")
    fun fetchAllData(): List<AccessToken>

    @Query("SELECT * FROM access_token LIMIT 1")
    fun getAccessToken(): LiveData<AccessToken>

    @Query("DELETE FROM access_token")
    fun nukeTable(): Int

    @Update
    fun logout(accessToken: AccessToken): Int

    @Update
    fun login(accessToken: AccessToken): Int

    @Query("SELECT * FROM access_token where user_name=:userName ORDER BY id DESC LIMIT 1")
    fun getTokerByEmail(userName: String): LiveData<AccessToken>

//    @Query("SELECT * FROM access_token where user_name=:userName and password=:password ORDER BY id DESC LIMIT 1")
//    fun getTokerByEmailPasword(userName: String, password: String): LiveData<AccessToken>

    @Query("SELECT * FROM access_token where user_name=:userName ORDER BY id DESC LIMIT 1")
    fun getTokerByEmailPasword(userName: String): LiveData<AccessToken>

    @Query("SELECT * FROM access_token where user_name=:userName ORDER BY id DESC LIMIT 1")
    fun getTokerByEmailSync(userName: String): AccessToken
}
