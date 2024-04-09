package com.rpsouza.taskapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.database.AppDatabase
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.data.repository.TaskRepository
import com.rpsouza.taskapp.databinding.FragmentTasksBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter
import com.rpsouza.taskapp.utils.showBottomSheet

class TasksFragment : Fragment() {
  private var _binding: FragmentTasksBinding? = null
  private val binding get() = _binding!!

  private lateinit var taskAdapter: TaskAdapter

  private val taskListViewModel: TaskListViewModel by viewModels {
    object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
          val database = AppDatabase.getDatabase(requireContext())
          val repository = TaskRepository(database.taskDAO())

          @Suppress("UNCHECKED_CAST")
          return TaskListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
      }
    }
  }

  private val taskViewModel: TaskViewModel by viewModels {
    object : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
          val database = AppDatabase.getDatabase(requireContext())
          val repository = TaskRepository(database.taskDAO())

          @Suppress("UNCHECKED_CAST")
          return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
      }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentTasksBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initListener()
    initRecyclerView()
    observeViewModel()

  }

  override fun onResume() {
    super.onResume()
    taskListViewModel.getAllTasks()
  }

  private fun initListener() {
    binding.floatingActionButton.setOnClickListener {
      val action = TasksFragmentDirections.actionTasksFragmentToFormTaskFragment(null)
      findNavController().navigate(action)
    }
  }

  private fun observeViewModel() {
    taskListViewModel.taskList.observe(viewLifecycleOwner) { taskList ->
      taskAdapter.submitList(taskList)
      listEmpty(taskList)
    }

    taskViewModel.taskStateData.observe(viewLifecycleOwner) { stateTask ->
      if (stateTask == StateTask.Deleted) {
        taskListViewModel.getAllTasks()
      }
    }
  }

  private fun initRecyclerView() {
    taskAdapter = TaskAdapter { task, option ->
      optionSelected(task, option)
    }

    with(binding.rvTasks) {
      layoutManager = LinearLayoutManager(requireContext())
      setHasFixedSize(true)
      adapter = taskAdapter
    }
  }

  private fun optionSelected(task: Task, option: Int) {
    when (option) {
      TaskAdapter.SELECT_DETAILS -> {
        Toast.makeText(
          requireContext(),
          "Detalhes da task: ${task.id}",
          Toast.LENGTH_SHORT
        ).show()
      }

      TaskAdapter.SELECT_EDIT -> {
        val action = TasksFragmentDirections
          .actionTasksFragmentToFormTaskFragment(task)
        findNavController().navigate(action)
      }

      TaskAdapter.SELECT_REMOVE -> {
        showBottomSheet(
          R.string.text_title_dialog_delete,
          R.string.text_button_dialog_confirm,
          getString(R.string.text_message_dialog_delete)
        ) { taskViewModel.deleteTask(task.id) }
      }
    }
  }

  private fun listEmpty(taskList: List<Task>) {
    binding.textInfo.text = if (taskList.isEmpty()) {
      getString(R.string.text_list_task_empty)
    } else {
      ""
    }

    binding.progressBar.isVisible = false
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}