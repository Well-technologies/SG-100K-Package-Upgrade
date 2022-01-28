package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.*

/**
 * Interface for database access for User related operations.
 */
@Dao
interface CognitionRequestDao {


    @Query("DELETE FROM cognition_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cognitionRequest: CognitionRequest): Long

    @Query("UPDATE cognition_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(cognitionRequest: CognitionRequest)

    @Query("DELETE FROM cognition_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM cognition_request WHERE id = :id")
    fun getCogRequest(id: Long): LiveData<CognitionRequest>

    @Query("SELECT * FROM cognition_request")
    fun getCogRequests(): LiveData<List<CognitionRequest>>

    @Query("SELECT * FROM cognition_request WHERE screening_id=:screeningId ORDER BY id DESC LIMIT 1")
    fun getCogRequests(screeningId: String): LiveData<CognitionRequest>

    @Query("SELECT * FROM cognition_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getCogRequestSyncPending():  LiveData<List<CognitionRequest>>

}
