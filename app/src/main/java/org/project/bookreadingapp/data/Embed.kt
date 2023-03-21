package org.project.bookreadingapp.data

import com.google.gson.annotations.SerializedName

data class Embed (
    val embed : String,
    @SerializedName("ex_synthesize_voice")
    val exSynVoice : String

        )