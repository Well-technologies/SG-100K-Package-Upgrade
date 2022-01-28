package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.FFQRequest

/**
 * Interface for database access for User related operations.
 */
@Dao
interface FFQRequestDao {


    @Query("DELETE FROM ffq_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ffqRequest: FFQRequest): Long

    @Query("UPDATE ffq_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(ffqRequest: FFQRequest)

    @Query("DELETE FROM ffq_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM ffq_request WHERE id = :id")
    fun getFfqRequest(id: Long): LiveData<FFQRequest>

    @Query("SELECT * FROM ffq_request")
    fun getFfqRequests(): LiveData<List<FFQRequest>>

    @Query("SELECT * FROM ffq_request WHERE screening_id=:screeningId ORDER BY id DESC LIMIT 1")
    fun getFfqRequests(screeningId: String): LiveData<FFQRequest>

    @Query("SELECT * FROM ffq_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getFfqRequestSyncPending():  LiveData<List<FFQRequest>>

}
