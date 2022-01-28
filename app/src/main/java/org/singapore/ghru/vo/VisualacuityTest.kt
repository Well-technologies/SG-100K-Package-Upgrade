package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "visual_acuity_request")
data class VisualAcuityRequest(
    @Embedded(prefix = "body") @Expose @SerializedName("body") var body: VisualAcuityTests?,
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") var meta: Meta?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(VisualAcuityTests::class.java.classLoader),
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

    companion object CREATOR : Parcelable.Creator<VisualAcuityRequest> {
        override fun createFromParcel(parcel: Parcel): VisualAcuityRequest {
            return VisualAcuityRequest(parcel)
        }

        override fun newArray(size: Int): Array<VisualAcuityRequest?> {
            return arrayOfNulls(size)
        }
    }

}

data class VisualAcuityTests(
    @Embedded(prefix = "right_eye") @Expose @SerializedName("right_eye") var right_eye: VisualAcuityDataNew?,
    @Embedded(prefix = "left_eye") @Expose @SerializedName("left_eye") var left_eye: VisualAcuityDataNew?,
    @Expose @field:SerializedName("comment") var comment: String?,
    @Expose @field:SerializedName("VASQ1") var visual_aid: String?
): Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("is_partial_submission")
    var isPartialSubmission: Boolean = false

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(VisualAcuityData::class.java.classLoader),
        parcel.readParcelable(VisualAcuityData::class.java.classLoader),
        parcel.readString(),
//        parcel.readByte() != 0.toByte(),
        parcel.readString()

    ) {
        readContraindications(parcel)
        isPartialSubmission = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(right_eye, flags)
        parcel.writeParcelable(left_eye, flags)
        parcel.writeString(comment)
//        parcel.writeByte(if (images_exported) 1 else 0)
        parcel.writeString(visual_aid)
        parcel.writeByte(if (isPartialSubmission) 1 else 0)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VisualAcuityTests> {
        override fun createFromParcel(parcel: Parcel): VisualAcuityTests {
            return VisualAcuityTests(parcel)
        }

        override fun newArray(size: Int): Array<VisualAcuityTests?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}

data class VisualAcuityData(
    @Expose @field:SerializedName("letter_score") var letter_score: String?,
    @Expose @field:SerializedName("acuity_score") var acuity_score: String?,
    @Expose @field:SerializedName("logmar_score") var logmar_score: String?,
    @Expose @field:SerializedName("comment") var comment: String?,
    @Expose @field:SerializedName("device_id") var device_id: String?
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(letter_score)
        parcel.writeString(acuity_score)
        parcel.writeString(logmar_score)
        parcel.writeString(comment)
        parcel.writeString(device_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VisualAcuityData> {
        override fun createFromParcel(parcel: Parcel): VisualAcuityData {
            return VisualAcuityData(parcel)
        }

        override fun newArray(size: Int): Array<VisualAcuityData?> {
            return arrayOfNulls(size)
        }
    }
}

data class VisualAcuityDataNew(
    @Expose @field:SerializedName("number_of_rows") var number_of_rows: String?,
    @Expose @field:SerializedName("number_of_letters") var number_of_letters: String?,
    @Expose @field:SerializedName("logmar_score") var logmar_value: String?
): Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(number_of_rows)
        parcel.writeString(number_of_letters)
        parcel.writeString(logmar_value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VisualAcuityDataNew> {
        override fun createFromParcel(parcel: Parcel): VisualAcuityDataNew {
            return VisualAcuityDataNew(parcel)
        }

        override fun newArray(size: Int): Array<VisualAcuityDataNew?> {
            return arrayOfNulls(size)
        }
    }
}