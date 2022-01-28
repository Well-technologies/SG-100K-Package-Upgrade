package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ParticipantStationItemNew (
    @Expose @SerializedName("station_id") var station_id: String?,
    @Expose @SerializedName("station_name") var station_name: String?,
    @Expose @SerializedName("status_text") var status_text: String?,
    @Expose @SerializedName("status_code") var status_code: String?,
    @Expose @SerializedName("is_cancelled") var isCancelled: Int? = 0

) : Serializable, Parcelable {

    var id: Int = 0
    var reg_status: Int = 0
    var st_hlq_status: Int = 0
    var pa_hlq_status: Int = 0
    var biological_status: Int = 0
    var ecg_status: Int = 0
    var spiro_status: Int = 0
    var fundo_status: Int = 0
    var bp_status: Int = 0
    var axivity_status: Int = 0
    var intake_status: Int = 0
    var report_status: Int = 0
    var checkout_status: Int = 0
    var bm_status: Int = 0

    constructor(parcel: Parcel) : this(

        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        isCancelled = parcel.readValue(Int::class.java.classLoader) as? Int
        id = parcel.readInt()
        reg_status = parcel.readInt()
        st_hlq_status = parcel.readInt()
        pa_hlq_status = parcel.readInt()
        biological_status = parcel.readInt()
        ecg_status = parcel.readInt()
        spiro_status = parcel.readInt()
        fundo_status = parcel.readInt()
        bp_status = parcel.readInt()
        axivity_status = parcel.readInt()
        intake_status = parcel.readInt()
        report_status = parcel.readInt()
        checkout_status = parcel.readInt()
        bm_status = parcel.readInt()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(station_id)
        parcel.writeString(station_name)
        parcel.writeString(status_text)
        parcel.writeString(status_code)
        parcel.writeValue(isCancelled)
        parcel.writeInt(id)
        parcel.writeInt(reg_status)
        parcel.writeInt(st_hlq_status)
        parcel.writeInt(pa_hlq_status)
        parcel.writeInt(biological_status)
        parcel.writeInt(ecg_status)
        parcel.writeInt(spiro_status)
        parcel.writeInt(fundo_status)
        parcel.writeInt(bp_status)
        parcel.writeInt(axivity_status)
        parcel.writeInt(intake_status)
        parcel.writeInt(report_status)
        parcel.writeInt(checkout_status)
        parcel.writeInt(bm_status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantStationItemNew> {
        override fun createFromParcel(parcel: Parcel): ParticipantStationItemNew {
            return ParticipantStationItemNew(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantStationItemNew?> {
            return arrayOfNulls(size)
        }
    }

}