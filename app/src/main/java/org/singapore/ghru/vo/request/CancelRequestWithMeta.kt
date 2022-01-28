package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable

@Entity(tableName = "cancel_request_with_meta")
data class CancelRequestWithMeta(
    @Expose @SerializedName("station_type") @ColumnInfo(name = "station_type") var stationType: String?,
    @Expose @SerializedName("comment")@ColumnInfo(name = "comment") var comment: String?,
    @Expose @SerializedName("reason")@ColumnInfo(name = "reason") var reason: String?

) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var timestamp: Long = System.currentTimeMillis()

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    @ColumnInfo(name = "created_date_time")
    lateinit var createdDateTime: String

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("questions")
    var questions: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("meta")
    var meta: Meta? = null


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
        createdDateTime = parcel.readString()
        readContraindications(parcel)
        readQuestions(parcel)

    }

    @Ignore
    constructor() : this(reason = null, stationType = null, comment = null)

    @Ignore
    constructor(stationType: String) : this(reason = null, stationType = stationType, comment = null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stationType)
        parcel.writeString(comment)
        parcel.writeString(reason)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
        parcel.writeString(createdDateTime)
        parcel.writeList(contraindications)
        parcel.writeList(questions)
        parcel.writeParcelable(meta, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CancelRequestWithMeta> {
        override fun createFromParcel(parcel: Parcel): CancelRequestWithMeta {
            return CancelRequestWithMeta(parcel)
        }

        override fun newArray(size: Int): Array<CancelRequestWithMeta?> {
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