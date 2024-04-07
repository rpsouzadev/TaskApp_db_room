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
import androidx.recyclerview.widget.RecyclerView
import com.rpsouza.taskapp.R
import com.rpsouza.taskapp.data.database.AppDatabase
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task
import com.rpsouza.taskapp.data.repository.TaskRepository
import com.rpsouza.taskapp.databinding.FragmentTasksBinding
import com.rpsouza.taskapp.ui.adapter.TaskAdapter
import com.rpsouza.taskapp.utils.StateView
import com.rpsouza.taskapp.utils.showBottomSheet

class TasksFragment : Fragment() {
  private var _binding: FragmentTasksBinding? = null
  private val binding get() = _binding!!

  private lateinit var taskAdapter: TaskAdapter

  private val viewModel: TaskViewModel by viewModels {
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
    viewModel.getTasks()
  }

  private fun initListener() {
    binding.floatingActionButton.setOnClickListener {
      val action = TasksFragmentDirections.actionTasksFragmentToFormTaskFragment(null)
      findNavController().navigate(action)
    }
  }

  private fun observeViewModel() {
    viewModel.taskList.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          val taskList = stateView.data?.filter { it.status == Status.TODO }

          listEmpty(taskList ?: emptyList())
          taskAdapter.submitList(taskList)
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.taskInsert.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          if (stateView.data?.status == Status.TODO) {
            val oldList = taskAdapter.currentList

            val newList = oldList.toMutableList().apply {
              add(0, stateView.data)
            }

            taskAdapter.submitList(newList)
            setPositionRecyclerView()
            listEmpty(newList)
          }
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.taskUpdate.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          val oldList = taskAdapter.currentList
          val newList = oldList.toMutableList().apply {
            if (!oldList.contains(stateView.data) && stateView.data?.status == Status.TODO) {
              add(0, stateView.data)
              setPositionRecyclerView()
            }

            if (stateView.data?.status == Status.TODO) {
              find { it.id == stateView.data.id }?.description = stateView.data.description
            } else {
              remove(stateView.data)
            }
          }

          val position = newList.indexOfFirst { it.id == stateView.data?.id }

          taskAdapter.submitList(newList)
          taskAdapter.notifyItemChanged(position)
          listEmpty(newList)
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
      }
    }

    viewModel.taskDelete.observe(viewLifecycleOwner) { stateView ->
      when (stateView) {
        is StateView.OnLoading -> {
          binding.progressBar.isVisible = true
        }

        is StateView.OnSuccess -> {
          binding.progressBar.isVisible = false

          if (stateView.data?.status == Status.TODO) {
            Toast.makeText(
              requireContext(),
              R.string.text_remove_task_successful,
              Toast.LENGTH_SHORT
            ).show()

            val oldList = taskAdapter.currentList
            val newList = oldList.toMutableList().apply {
              remove(stateView.data)
            }

            taskAdapter.submitList(newList)
            listEmpty(newList)
          }
        }

        is StateView.OnError -> {
          binding.progressBar.isVisible = false
          Toast.makeText(
            requireContext(),
            stateView.message,
            Toast.LENGTH_SHORT
          ).show()
        }
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
        ) { viewModel.deleteTask(task) }
      }
    }
  }

  private fun setPositionRecyclerView() {
    taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        binding.rvTasks.scrollToPosition(0)
      }
    })
  }

  private fun listEmpty(taskList: List<Task>) {
    binding.textInfo.text = if (taskList.isEmpty()) {
      getString(R.string.text_list_task_empty)
    } else {
      ""
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}