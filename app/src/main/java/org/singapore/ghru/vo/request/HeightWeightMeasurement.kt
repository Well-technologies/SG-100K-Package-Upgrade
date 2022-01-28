package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.BR
import org.singapore.ghru.vo.Meta
import java.io.Serializable


data class HeightMeasurementMetaResonce(
    @Expose @SerializedName("error") val meta: Boolean?,
    @Expose @SerializedName("data") val data: HeightWeightMeasurementMetaResonceData?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readParcelable(HeightWeightMeasurementMetaResonceData::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(meta)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightMeasurementMetaResonce> {
        override fun createFromParcel(parcel: Parcel): HeightMeasurementMetaResonce {
            return HeightMeasurementMetaResonce(parcel)
        }

        override fun newArray(size: Int): Array<HeightMeasurementMetaResonce?> {
            return arrayOfNulls(size)
        }
    }


}

data class HeightWeightMeasurementMetaResonceData(
    @Expose @SerializedName("error") val meta: Boolean?,
    @Expose @SerializedName("station") val station: HeightWeightMeasurementMetaResonceStation?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readParcelable(HeightWeightMeasurementMetaResonceStation::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(meta)
        parcel.writeParcelable(station, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightWeightMeasurementMetaResonceData> {
        override fun createFromParcel(parcel: Parcel): HeightWeightMeasurementMetaResonceData {
            return HeightWeightMeasurementMetaResonceData(parcel)
        }

        override fun newArray(size: Int): Array<HeightWeightMeasurementMetaResonceData?> {
            return arrayOfNulls(size)
        }
    }

}

data class HeightWeightMeasurementMetaResonceStation(
    @Expose @SerializedName("error") val meta: Boolean?,
    @Expose @SerializedName("data") val data: HeightWeightMeasurementMeta?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readParcelable(HeightWeightMeasurementMeta::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(meta)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightWeightMeasurementMetaResonceStation> {
        override fun createFromParcel(parcel: Parcel): HeightWeightMeasurementMetaResonceStation {
            return HeightWeightMeasurementMetaResonceStation(parcel)
        }

        override fun newArray(size: Int): Array<HeightWeightMeasurementMetaResonceStation?> {
            return arrayOfNulls(size)
        }
    }

}
@Entity(tableName = "height_weight_measurement_meta")
data class HeightWeightMeasurementMeta(
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") val meta: Meta?,
    @Embedded(prefix = "body")@Expose @SerializedName("body") val body: HeightWeightMeasurement?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(HeightWeightMeasurement::class.java.classLoader)
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(body, flags)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightWeightMeasurementMeta> {
        override fun createFromParcel(parcel: Parcel): HeightWeightMeasurementMeta {
            return HeightWeightMeasurementMeta(parcel)
        }

        override fun newArray(size: Int): Array<HeightWeightMeasurementMeta?> {
            return arrayOfNulls(size)
        }
    }

}

data class HeightWeightMeasurement(
    @Expose @SerializedName("height") val height: HeightWeightMeasurementData?,
    @Expose @SerializedName("weight") val weight: HeightWeightMeasurementData?,
    @Expose @SerializedName("contraindications") var contraindications: List<Map<String, String>>? = null
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(HeightWeightMeasurementData::class.java.classLoader),
        parcel.readParcelable(HeightWeightMeasurementData::class.java.classLoader)
//        parcel.readMap(List<Map<String, String>>())
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(height, flags)
        parcel.writeParcelable(weight, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightWeightMeasurement> {
        override fun createFromParcel(parcel: Parcel): HeightWeightMeasurement {
            return HeightWeightMeasurement(parcel)
        }

        override fun newArray(size: Int): Array<HeightWeightMeasurement?> {
            return arrayOfNulls(size)
        }
    }

}


data class HeightWeightMeasurementData(
    @Expose @SerializedName("device_id") var deviceId: String?,
    @Expose @SerializedName("comment") var comment: String?,
    @Expose @SerializedName("unit") var unit: String?,
    @Expose @SerializedName("value") var value: Double?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    constructor() : this(null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(comment)
        parcel.writeString(comment)
        parcel.writeValue(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HeightWeightMeasurementData> {
        override fun createFromParcel(parcel: Parcel): HeightWeightMeasurementData {
            return HeightWeightMeasurementData(parcel)
        }

        override fun newArray(size: Int): Array<HeightWeightMeasurementData?> {
            return arrayOfNulls(size)
        }
    }
}