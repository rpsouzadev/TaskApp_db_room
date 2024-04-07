package com.rpsouza.taskapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.TaskEntity
import com.rpsouza.taskapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
  private val _taskStateData = MutableLiveData<StateTask>()
  val taskStateData: LiveData<StateTask> = _taskStateData

  private val _taskStateMessage = MutableLiveData<Int>()
  val taskStateMessage: LiveData<Int> = _taskStateMessage

  fun insertOrUpdateTask(id: Long = 0, description: String, status: Status) {
    if (id == 0L) {
      insertTask(TaskEntity(description = description, status = status))
    } else {
      updateTask(TaskEntity(id, description, status))
    }
  }

  fun getTasks() {

  }

  private fun insertTask(task: TaskEntity) = viewModelScope.launch {
    try {
      val id = repository.insertTask(task)

      if (id > 0) {
        _taskStateData.postValue(StateTask.Inserted)
        _taskStateMessage.postValue(R.string.text_save_sucess_form_task_fragment)
      }

    } catch (e: Exception) {
      _taskStateMessage.postValue(R.string.error_generic)
    }
  }

  private fun updateTask(task: TaskEntity) {

  }

  fun deleteTask(id: Long) {

  }
}

sealed class StateTask {
  object Inserted: StateTask()
  object Updated: StateTask()
  object Deleted: StateTask()
  object Listed: StateTask()
}