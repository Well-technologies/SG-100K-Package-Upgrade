package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.singapore.ghru.vo.Meta
import org.singapore.ghru.vo.TreadmillBP
import java.io.Serializable

@Entity(
    tableName = "treadmill_request"
)
data class TreadmillRequest(
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

@Parcelize
data class TreadmillBody(
    @Expose @SerializedName(value = "comment") var comment: String? = null,
    @Expose @SerializedName(value = "device_id") var device_id: String? = null,
    @Expose @SerializedName("before_test") var before_test: List<Map<String, String>>? = null,
    @Expose @SerializedName("after_test") var after_test: List<Map<String, String>>? = null,
    @Expose @SerializedName("blood_pressure") var blood_pressure: List<TreadmillBPData>? = null,
    @Expose @SerializedName(value = "contraindications") var contraindications: List<Map<String, String>>? = null
) : Parcelable

@Parcelize
data class TreadmillBPData(
    @Expose @SerializedName(value = "systolic") var systolic: String? = null,
    @Expose @SerializedName(value = "diastolic") var diastolic: String? = null,
    @Expose @SerializedName(value = "pulse") var pulse: String? = null,
    @Expose @SerializedName(value = "stage") var stage: String? = null
) : Parcelable