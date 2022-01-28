package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.HipWaistRequest

/**
 * Interface for database access for User related operations.
 */
@Dao
interface HipWaistRequestDao {


    @Query("DELETE FROM hip_waist_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(hipWaistRequest: HipWaistRequest): Long

    @Query("UPDATE hip_waist_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(hipWaistRequest: HipWaistRequest)

    @Query("DELETE FROM hip_waist_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM hip_waist_request WHERE id = :id")
    fun getGripStrengthRequest(id: Long): LiveData<HipWaistRequest>

    @Query("SELECT * FROM hip_waist_request")
    fun getGripStrengthRequests(): LiveData<List<HipWaistRequest>>

    @Query("SELECT * FROM hip_waist_request WHERE screening_id=:screeningId ORDER BY id DESC LIMIT 1")
    fun getGripStrengthRequests(screeningId: String): LiveData<HipWaistRequest>

    @Query("SELECT * FROM hip_waist_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getGripStrengthRequestSyncPending():  LiveData<List<HipWaistRequest>>

}
