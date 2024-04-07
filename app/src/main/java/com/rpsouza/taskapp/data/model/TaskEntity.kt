package com.rpsouza.taskapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class TaskEntity(
  @PrimaryKey(true)
  var id: Long = 0,
  var description: String,
  var status: Status
)