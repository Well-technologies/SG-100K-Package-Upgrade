package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable

@Entity(tableName = "checkout_request")
data class CheckoutRequest(
    @Expose @field:SerializedName("comment") var comment: String?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("vouchers")
    var vouchers: ArrayList<String>? = null

    @Ignore
    @Expose
    @SerializedName("studies")
    var studies: StudyList? = null

    @Ignore
    @Expose
    @SerializedName("study_amount")
    var study_amount: String? = null

    constructor(parcel: Parcel) : this(
        parcel.readString(),
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
    }
}

data class StudyList(
    @Expose @field:SerializedName("study_hellios") var study_hellios: String?
//    @Expose @field:SerializedName("study_abc") var study_abc: String?,
//    @Expose @field:SerializedName("study_xyz") var study_xyz: String?,
//    @Expose @field:SerializedName("study_123") var study_123: String?
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(study_hellios)
//        parcel.writeString(study_abc)
//        parcel.writeString(study_xyz)
//        parcel.writeString(study_123)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudyList> {
        override fun createFromParcel(parcel: Parcel): StudyList {
            return StudyList(parcel)
        }

        override fun newArray(size: Int): Array<StudyList?> {
            return arrayOfNulls(size)
        }
    }
}