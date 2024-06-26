package com.rpsouza.taskapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> = _taskList

    fun getAllTasks() = viewModelScope.launch {
        try {
            _taskList.postValue(repository.getAllTasks())
        } catch (e: Exception) {
            Log.i("taskLog", e.message.toString())
        }
    }
}