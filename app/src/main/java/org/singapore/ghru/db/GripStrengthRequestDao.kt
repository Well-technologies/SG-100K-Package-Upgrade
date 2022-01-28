package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.GripStrengthRequest

/**
 * Interface for database access for User related operations.
 */
@Dao
interface GripStrengthRequestDao {


    @Query("DELETE FROM grip_strength_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gripStrengthRequest: GripStrengthRequest): Long

    @Query("UPDATE grip_strength_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(gripStrengthRequest: GripStrengthRequest)

    @Query("DELETE FROM grip_strength_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM grip_strength_request WHERE id = :id")
    fun getGripStrengthRequest(id: Long): LiveData<GripStrengthRequest>

    @Query("SELECT * FROM grip_strength_request")
    fun getGripStrengthRequests(): LiveData<List<GripStrengthRequest>>

    @Query("SELECT * FROM grip_strength_request WHERE screening_id=:screeningId ORDER BY id DESC LIMIT 1")
    fun getGripStrengthRequests(screeningId: String): LiveData<GripStrengthRequest>

    @Query("SELECT * FROM grip_strength_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getGripStrengthRequestSyncPending():  LiveData<List<GripStrengthRequest>>

}
