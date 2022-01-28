package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StationParticipant(
    @Expose @SerializedName("participants") var listRequest: ArrayList<ParticipantListItem?>?= null
)

data class ParticipantListItem (
    @Expose @SerializedName("screening_id") var screening_id: String?,
    @Expose @SerializedName("registerd_date") var registerd_date: String?,
    @Expose @SerializedName("status") var status: String?,
    @Expose @SerializedName("is_cancelled") var isCancelled: Int? = 0

) : Serializable, Parcelable {

    var id: Int = 0
    var statusId: Int =0

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        isCancelled = parcel.readValue(Int::class.java.classLoader) as? Int
        id = parcel.readInt()
        statusId = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(screening_id)
        parcel.writeString(registerd_date)
        parcel.writeString(status)
        parcel.writeValue(isCancelled)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantListItem> {
        override fun createFromParcel(parcel: Parcel): ParticipantListItem {
            return ParticipantListItem(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantListItem?> {
            return arrayOfNulls(size)
        }
    }

}
