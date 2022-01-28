package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class StationItem(
    var id: Int = 0,
    var name: String,
    var date: String,
    var resourceId: Int = 0
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(date)
        parcel.writeInt(resourceId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StationItem> {
        override fun createFromParcel(parcel: Parcel): StationItem {
            return StationItem(parcel)
        }

        override fun newArray(size: Int): Array<StationItem?> {
            return arrayOfNulls(size)
        }
    }

}
