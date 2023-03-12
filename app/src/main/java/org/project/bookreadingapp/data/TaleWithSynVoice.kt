package org.project.bookreadingapp.data

import com.google.gson.annotations.SerializedName
import java.util.Dictionary
import java.util.Objects

data class TaleWithSynVoice(
    val tale_id :Int,
    @SerializedName("syn_voice")
    val synVoice: List<SynVoice>

)