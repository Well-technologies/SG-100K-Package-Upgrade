package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "ffq_request_new1")
data class FFQRequestNew1(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: FfqDataNew1?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(FfqDataNew1::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<FFQRequestNew1> {
        override fun createFromParcel(parcel: Parcel): FFQRequestNew1 {
            return FFQRequestNew1(parcel)
        }

        override fun newArray(size: Int): Array<FFQRequestNew1?> {
            return arrayOfNulls(size)
        }
    }

}

data class FfqDataNew1(
    @Expose @field:SerializedName("status") var status: String?
): Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readString()

    ) {
        readContraindications(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)

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

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}