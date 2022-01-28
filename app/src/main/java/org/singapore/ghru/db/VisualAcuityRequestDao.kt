package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.HipWaistRequest
import org.singapore.ghru.vo.VisualAcuityRequest

/**
 * Interface for database access for User related operations.
 */
@Dao
interface VisualAcuityRequestDao {


    @Query("DELETE FROM visual_acuity_request")
    fun nukeTable(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(visualAcuity: VisualAcuityRequest): Long

    @Query("UPDATE visual_acuity_request SET sync_pending = 0 WHERE sync_pending = 1 AND screening_id = :screeningId")
    fun update(screeningId: String): Int

    @Delete
    fun delete(visualAcuity: VisualAcuityRequest)

    @Query("DELETE FROM visual_acuity_request WHERE id = :id")
    fun deleteRequest(id : Long)

    @Query("SELECT * FROM visual_acuity_request WHERE id = :id")
    fun getVisualAcuityRequest(id: Long): LiveData<VisualAcuityRequest>

    @Query("SELECT * FROM visual_acuity_request")
    fun getVisualAcuityRequests(): LiveData<List<VisualAcuityRequest>>

    @Query("SELECT * FROM visual_acuity_request WHERE screening_id=:screeningId ORDER BY id DESC LIMIT 1")
    fun getVisualAcuityRequests(screeningId: String): LiveData<VisualAcuityRequest>

    @Query("SELECT * FROM visual_acuity_request WHERE sync_pending = 1 ORDER BY id ASC")
    fun getVisualAcuityRequestSyncPending():  LiveData<List<VisualAcuityRequest>>

}
