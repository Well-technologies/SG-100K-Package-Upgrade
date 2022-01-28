package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable

@Entity(tableName = "skin_request")
data class SkinRequest(
    @Expose @field:SerializedName("comment") var comment: String?,
    @Expose @field:SerializedName("device_id") var device_id: String?,
    @Expose @field:SerializedName("device_id_two") var device_id_two: String?,
    @Expose @field:SerializedName("device_id_three") var device_id_three: String?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("reading1_tewl")
    var tewl_readings: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("reading2_hydration")
    var hydration_readings: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("reading3_ph")
    var ph_readings: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("stationQuestions")
    var stationQuestions: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("is_partial_submission")
    var isPartialSubmission: Boolean = false

    @Ignore
    @Expose
    @SerializedName("is_manual_entry")
    var isManualEntry: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Meta::class.java.classLoader)

    ) {
        readContraindications(parcel)
        readStationQuestions(parcel)
        getTewlReadings(parcel)
        getHydrationReadings(parcel)
        getPhReadings(parcel)
        isPartialSubmission = parcel.readByte() != 0.toByte()
        isManualEntry = parcel.readByte() != 0.toByte()
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
        parcel.writeString(comment)
        parcel.writeParcelable(meta, flags)
        parcel.writeByte(if (isPartialSubmission) 1 else 0)
        parcel.writeByte(if (isManualEntry) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SkinRequest> {
        override fun createFromParcel(parcel: Parcel): SkinRequest {
            return SkinRequest(parcel)
        }

        override fun newArray(size: Int): Array<SkinRequest?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun readStationQuestions(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun getTewlReadings(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun getHydrationReadings(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun getPhReadings(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }
}