package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ParticipantListMeta (
    @Expose @SerializedName("current_page") var current_page: String?,
    @Expose @SerializedName("from") var from: String?,
    @Expose @SerializedName("last_page") var last_page: String?,
    @Expose @SerializedName("path") var path: String?,
    @Expose @SerializedName("per_page") var per_page: String?,
    @Expose @SerializedName("to") var to: String?,
    @Expose @SerializedName("total") var total: String?

) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(current_page)
        parcel.writeString(from)
        parcel.writeString(last_page)
        parcel.writeString(path)
        parcel.writeString(per_page)
        parcel.writeString(to)
        parcel.writeString(total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantListMeta> {
        override fun createFromParcel(parcel: Parcel): ParticipantListMeta {
            return ParticipantListMeta(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantListMeta?> {
            return arrayOfNulls(size)
        }
    }

}