package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable

@Entity(
    tableName = "dxa_request"
)
data class DXARequest(
    @Expose @SerializedName(value = "body")  @ColumnInfo(name = "body") var body: String? = null,
    @Embedded(prefix = "meta") @Expose @field:SerializedName("meta") var meta: Meta?
) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Meta::class.java.classLoader)
    ) {
        id = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(body)
        parcel.writeParcelable(meta, flags)
        parcel.writeLong(id)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DXARequest> {
        override fun createFromParcel(parcel: Parcel): DXARequest {
            return DXARequest(parcel)
        }

        override fun newArray(size: Int): Array<DXARequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class DXABody(
    @Expose @field:SerializedName("dxa") var body: DXABodyData,
    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null
)

data class DXABodyData(
    @Expose @SerializedName(value = "comment") var comment: String? = null,
    @Expose @SerializedName(value = "device_id") var device_id: String? = null,
    @Expose @SerializedName(value = "DXSQ6") var whole_body: String? = null,
    @Expose @SerializedName(value = "DXSQ7") var lumbar_spine: String? = null,
    @Expose @SerializedName(value = "DXSQ1") var hip: String? = null,
    @Expose @SerializedName(value = "DXSQ4") var hip_used: String? = null,
    @Expose @SerializedName(value = "DXSQ5") var implants: String? = null,
    @Expose @SerializedName(value = "DXSQ3") var surgery: String? = null

): Serializable, Parcelable
{

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(comment)
        parcel.writeString(device_id)
        parcel.writeString(whole_body)
        parcel.writeString(lumbar_spine)
        parcel.writeString(hip)
        parcel.writeString(hip_used)
        parcel.writeString(implants)
        parcel.writeString(surgery)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DXABodyData> {
        override fun createFromParcel(parcel: Parcel): DXABodyData {
            return DXABodyData(parcel)
        }

        override fun newArray(size: Int): Array<DXABodyData?> {
            return arrayOfNulls(size)
        }
    }
}
