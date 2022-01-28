package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.BR
import org.singapore.ghru.vo.Meta
import java.io.Serializable


@Entity(tableName = "hl7_readings")
data class HL7Readings(
    @Embedded(prefix = "values")@Expose @SerializedName("values") val values: HL7Data?,
    @Expose @SerializedName("participant_id") var participant_id: String?,
    @Expose @SerializedName("id") var id: String?

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(HL7Data::class.java.classLoader),
        parcel.readString(),
        parcel.readString()
    ) {
        reading_id = parcel.readLong()
        timestamp = parcel.readLong()
    }

    @PrimaryKey(autoGenerate = true)
    var reading_id: Long = 0
    @ColumnInfo(name = "timestamp")
    var timestamp: Long = System.currentTimeMillis()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(values, flags)
        parcel.writeString(participant_id)
        parcel.writeString(id)
        parcel.writeLong(reading_id)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HL7Readings> {
        override fun createFromParcel(parcel: Parcel): HL7Readings {
            return HL7Readings(parcel)
        }

        override fun newArray(size: Int): Array<HL7Readings?> {
            return arrayOfNulls(size)
        }
    }

}

data class HL7Data(
    @Embedded(prefix = "height") @Expose @SerializedName("height") val height: HL7Values?,
    @Embedded(prefix = "weight")@Expose @SerializedName("weight") val weight: HL7Values?,
    @Expose @SerializedName("observation_time") var observation_time: String?
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(HL7Values::class.java.classLoader),
        parcel.readParcelable(HL7Values::class.java.classLoader),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(height, flags)
        parcel.writeParcelable(weight, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HL7Data> {
        override fun createFromParcel(parcel: Parcel): HL7Data {
            return HL7Data(parcel)
        }

        override fun newArray(size: Int): Array<HL7Data?> {
            return arrayOfNulls(size)
        }
    }

}

data class HL7Values(
    @Expose @SerializedName("unit") var unit: String?,
    @Expose @SerializedName("value") var value: Double?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    constructor() : this(null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(unit)
        parcel.writeValue(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HL7Values> {
        override fun createFromParcel(parcel: Parcel): HL7Values {
            return HL7Values(parcel)
        }

        override fun newArray(size: Int): Array<HL7Values?> {
            return arrayOfNulls(size)
        }
    }
}