package com.rpsouza.taskapp.data.database.dao

import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.TaskEntity

class TaskDao : ITaskDAO {
  override suspend fun getAllTask(): List<TaskEntity> {
    TODO("Not yet implemented")
  }

  override suspend fun insertTask(taskEntity: TaskEntity): Long {
    TODO("Not yet implemented")
  }

  override suspend fun deleteTask(id: Long) {
    TODO("Not yet implemented")
  }

  override suspend fun updateTask(id: Long, description: String, status: Status) {
    TODO("Not yet implemented")
  }
}