package com.rpsouza.taskapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.TaskEntity
import com.rpsouza.taskapp.data.repository.TaskRepository

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
  private val _taskStateData = MutableLiveData<StateTask>()
  val taskStateData: LiveData<StateTask> = _taskStateData

  private val _taskStateMessage = MutableLiveData<Int>()
  val taskStateMessage: LiveData<Int> = _taskStateMessage

  fun insertOrUpdateTask(id: Long, description: String, status: Status) {
    if (id == 0L) {
      insertTask(TaskEntity(description = description, status = status))
    } else {
      updateTask(TaskEntity(id, description, status))
    }
  }

  fun getTasks() {

  }

  private fun insertTask(task: TaskEntity) {

  }

  private fun updateTask(task: TaskEntity) {

  }

  fun deleteTask(task: TaskEntity) {

  }
}

sealed class StateTask {
  object Inserted: StateTask()
  object Updated: StateTask()
  object Deleted: StateTask()
  object Listed: StateTask()
}