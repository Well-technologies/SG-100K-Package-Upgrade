package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable


data class SampleCreateRequest(
    @Expose @SerializedName("meta")  var meta: Meta?,
    @Expose @SerializedName("comment")  var comment: String?
) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("barcode_readings")
    var barcode_readings: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("questions")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("is_partial_submission")
    var isPartialSubmission: Boolean = false


    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readString()
    ) {

        getBarcodeReadings(parcel)
        getContraindications(parcel)
        isPartialSubmission = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(meta, flags)
        parcel.writeString(comment)
        parcel.writeByte(if (isPartialSubmission) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SampleCreateRequest> {
        override fun createFromParcel(parcel: Parcel): SampleCreateRequest {
            return SampleCreateRequest(parcel)
        }

        override fun newArray(size: Int): Array<SampleCreateRequest?> {
            return arrayOfNulls(size)
        }

        private fun getBarcodeReadings(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun getContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }


}