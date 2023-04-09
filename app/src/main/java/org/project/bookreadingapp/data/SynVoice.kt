package org.project.bookreadingapp.data

import com.google.gson.annotations.SerializedName

data class SynVoice (
    val text : String,
    @SerializedName("syn_voice")
    val synVoice: String
        )