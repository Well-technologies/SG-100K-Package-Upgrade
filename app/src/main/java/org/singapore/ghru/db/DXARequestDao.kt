package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.request.DXARequest


@Dao
interface DXARequestDao {

    @Query("DELETE FROM dxa_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dxaRequest: DXARequest): Long

    @Query("UPDATE dxa_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(dxaRequest: DXARequest)

    @Query("DELETE FROM dxa_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM dxa_request WHERE id = :id")
    fun getDXA(id: Long): LiveData<DXARequest>

    @Query("SELECT * FROM dxa_request")
    fun getAllDXA(): LiveData<List<DXARequest>>

    @Query("SELECT * FROM dxa_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getDXARequestSyncPending(): LiveData<List<DXARequest>>
}