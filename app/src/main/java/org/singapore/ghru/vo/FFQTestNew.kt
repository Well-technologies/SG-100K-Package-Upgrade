package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "ffq_request_new")
data class FFQRequestNew(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: FfqDataNew?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?,
    @Expose @field:SerializedName("status") var status: String?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(FfqDataNew::class.java.classLoader),
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readString()
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
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FFQRequestNew> {
        override fun createFromParcel(parcel: Parcel): FFQRequestNew {
            return FFQRequestNew(parcel)
        }

        override fun newArray(size: Int): Array<FFQRequestNew?> {
            return arrayOfNulls(size)
        }
    }

}

data class FfqDataNew(
    @Expose @field:SerializedName("FFSQ1") var language: String?,
    @Expose @field:SerializedName("FFSQ2") var questionnaire_completed: Boolean = false,
    @Expose @field:SerializedName("FFSQ3") var assistance_required: Boolean = false
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()

    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(language)
        parcel.writeByte(if (questionnaire_completed) 1 else 0)
        parcel.writeByte(if (assistance_required) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FfqDataNew> {
        override fun createFromParcel(parcel: Parcel): FfqDataNew {
            return FfqDataNew(parcel)
        }

        override fun newArray(size: Int): Array<FfqDataNew?> {
            return arrayOfNulls(size)
        }
    }

}