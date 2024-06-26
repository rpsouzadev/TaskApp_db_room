package com.rpsouza.taskapp.data.repository

import com.rpsouza.taskapp.data.database.dao.ITaskDAO
import com.rpsouza.taskapp.data.database.entity.TaskEntity
import com.rpsouza.taskapp.data.model.Task

class TaskRepository(private val taskDAO: ITaskDAO) {

  suspend fun getAllTasks(): List<Task> {
    return taskDAO.getAllTask()
  }

  suspend fun insertTask(taskEntity: TaskEntity): Long {
    return taskDAO.insertTask(taskEntity)
  }

  suspend fun deleteTask(id: Long) {
    return taskDAO.deleteTask(id)
  }

  suspend fun updateTask(taskEntity: TaskEntity) {
    return taskDAO.updateTask(
      taskEntity.id,
      taskEntity.description,
      taskEntity.status
    )
  }
}