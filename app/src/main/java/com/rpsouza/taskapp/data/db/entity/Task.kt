package com.rpsouza.taskapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rpsouza.taskapp.data.model.Status

@Entity(tableName = "task_table")
class Task(
  @PrimaryKey(true)
  var id: Long = 0,
  val description: String,
  val status: Status
)