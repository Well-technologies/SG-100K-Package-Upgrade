package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ParticipantStationItemNewNew (
    @Expose @SerializedName("screening_id") var participant_id: String?,
    @Expose @SerializedName("stations") var listRequest: ArrayList<ParticipantStationItemNew?>?= null


) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.createTypedArrayList(ParticipantStationItemNew)
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(participant_id)
        parcel.writeTypedList(listRequest)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantStationItemNewNew> {
        override fun createFromParcel(parcel: Parcel): ParticipantStationItemNewNew {
            return ParticipantStationItemNewNew(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantStationItemNewNew?> {
            return arrayOfNulls(size)
        }
    }

}