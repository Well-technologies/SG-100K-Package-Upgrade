package org.singapore.ghru.db

import androidx.lifecycle.LiveData
import androidx.room.*
import org.singapore.ghru.vo.Questionnaire
import org.singapore.ghru.vo.QuestionnaireSelf

/**
 * Interface for database access for User related operations.
 */
@Dao
interface QuestionnaireSelfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnaire: QuestionnaireSelf): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(questionnaires: List<QuestionnaireSelf>)

    @Update
    fun update(questionnaire: QuestionnaireSelf): Int

    @Delete
    fun delete(questionnaire: QuestionnaireSelf)

    @Query("SELECT * FROM questionnaire_self WHERE id = :id")
    fun getQuestionnaire(id: Long): LiveData<QuestionnaireSelf>

    @Query("SELECT * FROM questionnaire_self")
    fun getQuestionnaires(): LiveData<List<QuestionnaireSelf>>

    @Query("SELECT * FROM questionnaire_self LIMIT 1")
    fun getQuestionnaire(): LiveData<QuestionnaireSelf>

    @Query("DELETE FROM questionnaire_self")
    fun nukeTable()
}
