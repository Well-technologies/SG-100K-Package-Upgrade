package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import java.io.Serializable

@Entity(tableName = "grip_strength_request")
data class GripStrengthRequest(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: GripStrengthTests?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(GripStrengthTests::class.java.classLoader),
        parcel.readParcelable(Meta::class.java.classLoader)
    ) {
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
        parcel.writeParcelable(body, flags)
        parcel.writeParcelable(meta, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GripStrengthRequest> {
        override fun createFromParcel(parcel: Parcel): GripStrengthRequest {
            return GripStrengthRequest(parcel)
        }

        override fun newArray(size: Int): Array<GripStrengthRequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class GripStrengthTests(
    @Embedded(prefix = "reading1") @Expose @SerializedName("reading1") var reading1: GripStrengthData?,
    @Embedded(prefix = "reading2") @Expose @SerializedName("reading2") var reading2: GripStrengthData?,
    @Embedded(prefix = "reading3") @Expose @SerializedName("reading3") var reading3: GripStrengthData?,
    @Expose @field:SerializedName("comment") var comment: String?,
    @Expose @field:SerializedName("device_id") var device_id: String?
): Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("station_questions")
    var stationQuestions: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(GripStrengthData::class.java.classLoader),
        parcel.readParcelable(GripStrengthData::class.java.classLoader),
        parcel.readParcelable(GripStrengthData::class.java.classLoader),
        parcel.readString(),
        parcel.readString()

    ) {
        readContraindications(parcel)
        readQuestions(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(reading1, flags)
        parcel.writeParcelable(reading2, flags)
        parcel.writeParcelable(reading3, flags)
        parcel.writeString(comment)
        parcel.writeString(device_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GripStrengthTests> {
        override fun createFromParcel(parcel: Parcel): GripStrengthTests {
            return GripStrengthTests(parcel)
        }

        override fun newArray(size: Int): Array<GripStrengthTests?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun readQuestions(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}

data class GripStrengthData(

    @Embedded(prefix = "left_grip") @Expose @SerializedName("left_grip") var left_grip: GripStrengthReading?,
    @Embedded(prefix = "right_grip") @Expose @SerializedName("right_grip") var right_grip: GripStrengthReading?
): Serializable, Parcelable {

        constructor(parcel: Parcel) : this(

        parcel.readParcelable(GripStrengthReading::class.java.classLoader),
        parcel.readParcelable(GripStrengthReading::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(left_grip, flags)
        parcel.writeParcelable(right_grip, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GripStrengthData> {
        override fun createFromParcel(parcel: Parcel): GripStrengthData {
            return GripStrengthData(parcel)
        }

        override fun newArray(size: Int): Array<GripStrengthData?> {
            return arrayOfNulls(size)
        }
    }
}

data class GripStrengthReading(
    @Expose @field:SerializedName("value") var value: String?,
    @Expose @field:SerializedName("unit") var unit: String?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GripStrengthReading> {
        override fun createFromParcel(parcel: Parcel): GripStrengthReading {
            return GripStrengthReading(parcel)
        }

        override fun newArray(size: Int): Array<GripStrengthReading?> {
            return arrayOfNulls(size)
        }
    }
}