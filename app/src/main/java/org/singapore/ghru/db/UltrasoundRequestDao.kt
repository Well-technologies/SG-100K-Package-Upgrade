package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.request.UltrasoundRequest


@Dao
interface UltrasoundRequestDao {

    @Query("DELETE FROM ultrasound_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ultrasoundRequest: UltrasoundRequest): Long

    @Query("UPDATE ultrasound_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(ultrasoundRequest: UltrasoundRequest)

    @Query("DELETE FROM ultrasound_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM ultrasound_request WHERE id = :id")
    fun getUltrasound(id: Long): LiveData<UltrasoundRequest>

    @Query("SELECT * FROM ultrasound_request")
    fun getAllUltrasound(): LiveData<List<UltrasoundRequest>>

    @Query("SELECT * FROM ultrasound_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getUltrasoundRequestSyncPending(): LiveData<List<UltrasoundRequest>>
}