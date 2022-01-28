package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "cognition_request")
data class CognitionRequest(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: CognitionData?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CognitionData::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<CognitionRequest> {
        override fun createFromParcel(parcel: Parcel): CognitionRequest {
            return CognitionRequest(parcel)
        }

        override fun newArray(size: Int): Array<CognitionRequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class CognitionData(
    @Expose @field:SerializedName("cognition_completed") var questionnaire_completed: Boolean = false,
    @Expose @field:SerializedName("assistance_required") var assistance_required: Boolean = false
): Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()

    ) {
        readContraindications(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (questionnaire_completed) 1 else 0)
        parcel.writeByte(if (assistance_required) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CognitionData> {
        override fun createFromParcel(parcel: Parcel): CognitionData {
            return CognitionData(parcel)
        }

        override fun newArray(size: Int): Array<CognitionData?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}