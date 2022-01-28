package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/**
 * Created by shanuka on 10/26/17.
 */
@Entity(
    tableName = "fundoscopy_request"
)
data class FundoscopyRequest(
    @Expose @SerializedName(value = "comment")  @ColumnInfo(name = "comment") var comment: String? = null,
    @Expose @SerializedName(value = "device_id") @ColumnInfo(name = "device_id") var device_id: String? = null,
    @Expose @field:SerializedName("meta")  @Embedded(prefix = "meta") var meta: Meta?

) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("confirm_questions")
    var confirm_questions: List<Map<String, String>>? = null

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "sync_pending")
    var syncPending: Boolean = false

    @ColumnInfo(name = "screening_id")
    lateinit var screeningId: String

    @ColumnInfo(name = "fundoscopy_meta_id")
    var fundoscopyMetaId: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Meta::class.java.classLoader)

    ) {
        id = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
        fundoscopyMetaId= parcel.readLong()
        readContraindications(parcel)
        readConfirmQuestions(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(comment)
        parcel.writeString(device_id)
        parcel.writeParcelable(meta, flags)
        parcel.writeLong(id)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
        parcel.writeLong(fundoscopyMetaId)
        parcel.writeList(contraindications)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FundoscopyRequest> {
        override fun createFromParcel(parcel: Parcel): FundoscopyRequest {
            return FundoscopyRequest(parcel)
        }

        override fun newArray(size: Int): Array<FundoscopyRequest?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun readConfirmQuestions(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }


}
