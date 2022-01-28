package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import java.io.Serializable

@Entity(tableName = "hip_waist_request")
data class HipWaistRequest(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: HipWaistTests?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(HipWaistTests::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<HipWaistRequest> {
        override fun createFromParcel(parcel: Parcel): HipWaistRequest {
            return HipWaistRequest(parcel)
        }

        override fun newArray(size: Int): Array<HipWaistRequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class HipWaistTests(
    @Embedded(prefix = "hip") @Expose @SerializedName("hip") var hip: HipWaistData?,
    @Embedded(prefix = "waist") @Expose @SerializedName("waist") var waist: HipWaistData?,
    @Expose @field:SerializedName("comment") var comment: String?,
    @Expose @field:SerializedName("device_id") var device_id: String?
): Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(HipWaistData::class.java.classLoader),
        parcel.readParcelable(HipWaistData::class.java.classLoader),
        parcel.readString(),
        parcel.readString()

    ) {
        readContraindications(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(hip, flags)
        parcel.writeParcelable(waist, flags)
        parcel.writeString(comment)
        parcel.writeString(device_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HipWaistTests> {
        override fun createFromParcel(parcel: Parcel): HipWaistTests {
            return HipWaistTests(parcel)
        }

        override fun newArray(size: Int): Array<HipWaistTests?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}

data class HipWaistData(
    @Expose @field:SerializedName("unit") var unit: String?
): Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("value")
    var value: ArrayList<Double>? = null

    constructor(parcel: Parcel) : this(
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HipWaistData> {
        override fun createFromParcel(parcel: Parcel): HipWaistData {
            return HipWaistData(parcel)
        }

        override fun newArray(size: Int): Array<HipWaistData?> {
            return arrayOfNulls(size)
        }
    }
}