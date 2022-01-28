package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "blood_pressure_item_request")
data class BloodPresureItemRequest(

    @Expose @SerializedName("systolic") @ColumnInfo(name = "systolic") val systolic: Int,
    @Expose @SerializedName("diastolic") @ColumnInfo(name = "diastolic") val diastolic: Int,
    @Expose @SerializedName("pulse") @ColumnInfo(name = "pulse") val pulse: Int,
//    @Expose @SerializedName("arm") @ColumnInfo(name = "arm") val arm: String,
//    @Expose @SerializedName("cuff_size") @ColumnInfo(name = "cuff_size") val cuff_size: String,
    @Expose @SerializedName("timestamp") @ColumnInfo(name = "timestamp") val timestamp: String

) : Serializable, Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "blood_presure_request_id")
    var bloodPresureRequestId: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
//        parcel.readString()!!,
//        parcel.readString()!!,
        parcel.readString()!!
    ) {
        id = parcel.readLong()

        bloodPresureRequestId = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(systolic)
        parcel.writeInt(diastolic)
        parcel.writeInt(pulse)
//        parcel.writeString(arm)
//        parcel.writeString(cuff_size)
        parcel.writeString(timestamp)

        parcel.writeLong(id)

        parcel.writeLong(bloodPresureRequestId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodPresureItemRequest> {
        override fun createFromParcel(parcel: Parcel): BloodPresureItemRequest {
            return BloodPresureItemRequest(parcel)
        }

        override fun newArray(size: Int): Array<BloodPresureItemRequest?> {
            return arrayOfNulls(size)
        }
    }


}