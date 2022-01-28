package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ParticipantStationsItem(
    var id: Int = 0,
    var participant_id: String,
    var reg_status: Int = 0,
    var height_status: Int = 0,
    var hip_status: Int = 0,
    var grip_status: Int = 0,
    var st_hlq_status: Int = 0,
    var pa_hlq_status: Int = 0,
    var biological_status: Int = 0,
    var ecg_status: Int = 0,
    var spiro_status: Int = 0,
    var fundo_status: Int = 0,
    var bp_status: Int = 0,
    var axivity_status: Int = 0,
    var dxa_status: Int = 0,
    var ultra_status: Int = 0,
    var visual_status: Int = 0,
    var tred_status: Int = 0,
    var ffq_status: Int = 0,
    var cog_status: Int = 0


) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()


    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(participant_id)
        parcel.writeInt(reg_status)
        parcel.writeInt(height_status)
        parcel.writeInt(hip_status)
        parcel.writeInt(grip_status)
        parcel.writeInt(st_hlq_status)
        parcel.writeInt(pa_hlq_status)
        parcel.writeInt(biological_status)
        parcel.writeInt(ecg_status)
        parcel.writeInt(spiro_status)
        parcel.writeInt(fundo_status)
        parcel.writeInt(bp_status)
        parcel.writeInt(axivity_status)
        parcel.writeInt(dxa_status)
        parcel.writeInt(ultra_status)
        parcel.writeInt(visual_status)
        parcel.writeInt(tred_status)
        parcel.writeInt(ffq_status)
        parcel.writeInt(cog_status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantStationsItem> {
        override fun createFromParcel(parcel: Parcel): ParticipantStationsItem {
            return ParticipantStationsItem(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantStationsItem?> {
            return arrayOfNulls(size)
        }
    }

}
