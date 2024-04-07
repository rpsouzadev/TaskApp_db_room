package com.rpsouza.taskapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: Long,
    var description: String,
    var status: Status
) : Parcelable
