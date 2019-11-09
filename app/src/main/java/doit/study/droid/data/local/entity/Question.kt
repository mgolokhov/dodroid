package doit.study.droid.data.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Question (
        @PrimaryKey
        @NonNull
        // Read only
        val id: Int,
        val text: String,
        val wrong: List<String>,
        val right: List<String>,
        val docLink: String,
        // Read/Write
        val wrongCounter: Int = 0,
        val rightCounter: Int = 0,
        val consecutiveRightCounter: Int = 0,
        val lastViewedAt: Long = Date().time,
        val studiedAt: Long = 0
)
