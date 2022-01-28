package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ECGData(
    @Expose @field:SerializedName("participant_id") var participant_id: String?,
    @Expose @field:SerializedName("station_id") var stationId: String?,
    @Expose @field:SerializedName("station_name") var stationName: String?,
    @Expose @field:SerializedName("status_text") var statusText: String?,
    @Expose @field:SerializedName("status_code") var statusCode: String?,
    @Expose @field:SerializedName("trace_status") var trace_status: String?,
    @Expose @field:SerializedName("is_cancelled") var isCancelled: Int? = 0
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(participant_id)
        parcel.writeString(stationId)
        parcel.writeString(stationName)
        parcel.writeString(statusText)
        parcel.writeString(statusCode)
        parcel.writeString(trace_status)
        parcel.writeValue(isCancelled)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ECGData> {
        override fun createFromParcel(parcel: Parcel): ECGData {
            return ECGData(parcel)
        }

        override fun newArray(size: Int): Array<ECGData?> {
            return arrayOfNulls(size)
        }
    }

}
