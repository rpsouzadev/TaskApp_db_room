package com.rpsouza.taskapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.database.entity.toTaskEntity
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
  private val _taskStateData = MutableLiveData<StateTask>()
  val taskStateData: LiveData<StateTask> = _taskStateData

  private val _taskStateMessage = MutableLiveData<Int>()
  val taskStateMessage: LiveData<Int> = _taskStateMessage

  fun insertOrUpdateTask(id: Long = 0, description: String, status: Status) {
    if (id == 0L) {
      insertTask(Task(description = description, status = status))
    } else {
      updateTask(Task(id, description, status))
    }
  }

  private fun insertTask(task: Task) = viewModelScope.launch {
    try {
      val id = repository.insertTask(task.toTaskEntity())

      if (id > 0) {
        _taskStateData.postValue(StateTask.Inserted)
        _taskStateMessage.postValue(R.string.text_save_sucess_form_task_fragment)
      }

    } catch (e: Exception) {
      _taskStateMessage.postValue(R.string.error_generic)
    }
  }

  private fun updateTask(task: Task) = viewModelScope.launch {
    try {
      repository.updateTask(task.toTaskEntity())

      _taskStateData.postValue(StateTask.Inserted)
      _taskStateMessage.postValue(R.string.text_update_sucess_form_task_fragment)
    } catch (e: Exception) {
      _taskStateMessage.postValue(R.string.error_generic)
    }
  }

  fun deleteTask(id: Long) = viewModelScope.launch {
    try {
      repository.deleteTask(id)

      _taskStateData.postValue(StateTask.Deleted)
      _taskStateMessage.postValue(R.string.text_delete_sucess_form_task_fragment)
    } catch (e: Exception) {
      _taskStateMessage.postValue(R.string.error_generic)
    }
  }
}

sealed class StateTask {
  object Inserted : StateTask()
  object Updated : StateTask()
  object Deleted : StateTask()
}