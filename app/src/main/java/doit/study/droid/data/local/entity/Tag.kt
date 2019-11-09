package doit.study.droid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag (
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val name: String,
        // Read/Write
        val selected: Boolean = false
)