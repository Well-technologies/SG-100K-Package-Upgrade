package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable

@Entity(
    tableName = "ultrasound_request"
)
data class UltrasoundRequest(
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

    companion object CREATOR : Parcelable.Creator<UltrasoundRequest> {
        override fun createFromParcel(parcel: Parcel): UltrasoundRequest {
            return UltrasoundRequest(parcel)
        }

        override fun newArray(size: Int): Array<UltrasoundRequest?> {
            return arrayOfNulls(size)
        }
    }
}

data class UltraBody(
    @Expose @field:SerializedName("ultrasound") var ultrasound: UltraBodyData,
    @Expose @SerializedName(value = "contraindications") var contraindications: List<Map<String, String>>? = null
)
data class UltraBodyData(
    @Expose @SerializedName(value = "comment") var comment: String? = null,
    @Expose @SerializedName(value = "device_id") var device_id: String? = null,
    @Expose @SerializedName(value = "USSQ2") var images_taken: String? = null,
    @Expose @SerializedName(value = "USSQ1") var images_2d_taken: String? = null
):Serializable, Parcelable
{
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(comment)
        parcel.writeString(device_id)
        parcel.writeString(images_taken)
        parcel.writeString(images_2d_taken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UltrasoundRequest> {
        override fun createFromParcel(parcel: Parcel): UltrasoundRequest {
            return UltrasoundRequest(parcel)
        }

        override fun newArray(size: Int): Array<UltrasoundRequest?> {
            return arrayOfNulls(size)
        }
    }
}