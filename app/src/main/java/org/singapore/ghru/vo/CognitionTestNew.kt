package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "cog_request_new")
data class CognitionRequestNew(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: CognitionDataNew?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?,
    @Expose @field:SerializedName("status") var status: String?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(CognitionDataNew::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<CognitionRequestNew> {
        override fun createFromParcel(parcel: Parcel): CognitionRequestNew {
            return CognitionRequestNew(parcel)
        }

        override fun newArray(size: Int): Array<CognitionRequestNew?> {
            return arrayOfNulls(size)
        }
    }

}

data class CognitionDataNew(
    @Expose @field:SerializedName("COSQ1") var cognition_completed: Boolean = false,
    @Expose @field:SerializedName("COSQ2") var assistance_required: Boolean = false
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()

    ) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (cognition_completed) 1 else 0)
        parcel.writeByte(if (assistance_required) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CognitionDataNew> {
        override fun createFromParcel(parcel: Parcel): CognitionDataNew {
            return CognitionDataNew(parcel)
        }

        override fun newArray(size: Int): Array<CognitionDataNew?> {
            return arrayOfNulls(size)
        }
    }

}