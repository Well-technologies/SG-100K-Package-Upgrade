package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "blood_pressure_request")
data class BloodPresureRequest(

    @Expose @SerializedName("comment") @ColumnInfo(name = "comment") var comment: String,
    @Expose @SerializedName("device_id") @ColumnInfo(name = "device_id") val device_id: String

) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @Ignore
    @Expose
    @SerializedName("blood_pressure")
    var bloodPresureRequestList: List<BloodPresureItemRequest>? = null

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    @ColumnInfo(name = "meta_id")
    var metaId: Long = 0

    @Ignore
    @Expose
    @SerializedName("BPSQ1")
    lateinit var arm: String

    @Ignore
    @Expose
    @SerializedName("BPSQ2")
    lateinit var cuffSize: String

    @Ignore
    @Expose
    @SerializedName("BPSQ3")
    lateinit var smoking: String

    @Ignore
    @Expose
    @SerializedName("BPSQ4")
    lateinit var caffeineConsumption: String

    @Ignore
    @Expose
    @SerializedName("partially_able")
    lateinit var partially_able: String

    @Ignore
    @Expose
    @SerializedName("partially_reason")
    lateinit var partially_reason: String

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
        id = parcel.readLong()
        bloodPresureRequestList = parcel.createTypedArrayList(BloodPresureItemRequest)
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId  = parcel.readString()
        metaId = parcel.readLong()
        arm  = parcel.readString()
        cuffSize  = parcel.readString()
        smoking  = parcel.readString()
        caffeineConsumption  = parcel.readString()
        partially_able  = parcel.readString()
        partially_reason  = parcel.readString()
        readContraindications(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(comment)
        parcel.writeString(device_id)
        parcel.writeLong(id)
        parcel.writeTypedList(bloodPresureRequestList)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
        parcel.writeLong(metaId)
        parcel.writeString(arm)
        parcel.writeString(cuffSize)
        parcel.writeString(smoking)
        parcel.writeString(caffeineConsumption)
        parcel.writeString(partially_able)
        parcel.writeString(partially_reason)
        parcel.writeList(contraindications)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodPresureRequest> {
        override fun createFromParcel(parcel: Parcel): BloodPresureRequest {
            return BloodPresureRequest(parcel)
        }

        override fun newArray(size: Int): Array<BloodPresureRequest?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }
}