package com.rpsouza.taskapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rpsouza.taskapp.data.model.Status
import com.rpsouza.taskapp.data.model.Task

@Entity(tableName = "task_table")
class TaskEntity(
  @PrimaryKey(true)
  var id: Long = 0,
  var description: String,
  var status: Status
)

fun Task.toTaskEntity(): TaskEntity {
  return with(this) {
    TaskEntity(
      id = this.id,
      description = this.description,
      status = this.status
    )
  }
}