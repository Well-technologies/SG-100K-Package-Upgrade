package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.request.TreadmillRequest
import org.singapore.ghru.vo.request.UltrasoundRequest


@Dao
interface TreadmillRequestDao {

    @Query("DELETE FROM treadmill_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(treadmillRequest: TreadmillRequest): Long

    @Query("UPDATE treadmill_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(treadmillRequest: TreadmillRequest)

    @Query("DELETE FROM treadmill_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM treadmill_request WHERE id = :id")
    fun getTreadmill(id: Long): LiveData<TreadmillRequest>

    @Query("SELECT * FROM treadmill_request")
    fun getAllTreadmill(): LiveData<List<TreadmillRequest>>

    @Query("SELECT * FROM treadmill_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getTreadmillRequestSyncPending(): LiveData<List<TreadmillRequest>>
}