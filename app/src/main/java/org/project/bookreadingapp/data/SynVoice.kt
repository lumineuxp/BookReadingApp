package org.project.bookreadingapp.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SynVoice(
    val text: String,
    @SerializedName("syn_voice")
    val synVoice: String
        ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(text)
        parcel.writeString(synVoice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SynVoice> {
        override fun createFromParcel(parcel: Parcel): SynVoice {
            return SynVoice(parcel)
        }

        override fun newArray(size: Int): Array<SynVoice?> {
            return arrayOfNulls(size)
        }
    }
}