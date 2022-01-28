package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable

@Entity(tableName = "vicorder_request")
data class VicorderRequest(
    @Expose @field:SerializedName("comment") var comment: String?,
    @Expose @field:SerializedName("device_id") var device_id: String?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("stationQuestions")
    var stationQuestions: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Meta::class.java.classLoader)
    ) {
        readContraindications(parcel)
        readStationQuestions(parcel)
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

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutRequest> {
        override fun createFromParcel(parcel: Parcel): CheckoutRequest {
            return CheckoutRequest(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutRequest?> {
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
    }
}