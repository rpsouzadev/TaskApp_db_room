package com.rpsouza.taskapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.TaskEntity

@Dao
interface ITaskDAO {
  @Query("SELECT * FROM task_table ORDER BY id DESC")
  suspend fun getAllTask(): List<TaskEntity>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertTask(taskEntity: TaskEntity): Long

  @Query("DELETE FROM task_table WHERE id = :id")
  suspend fun deleteTask(id: Long)

  @Query("UPDATE task_table SET description = :description, status = :status WHERE id = :id")
  suspend fun updateTask(id: Long, description: String, status: Status)
}