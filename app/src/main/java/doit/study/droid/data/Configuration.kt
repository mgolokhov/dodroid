package doit.study.droid.data

import com.google.gson.annotations.SerializedName

data class Configuration(
        @SerializedName("content_version")
        val contentVersion: Int = 1
)