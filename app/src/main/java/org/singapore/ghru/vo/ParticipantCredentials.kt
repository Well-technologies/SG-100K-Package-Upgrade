package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ParticipantCre(
    @Expose @SerializedName("username") var username: String?,
    @Expose @SerializedName("password") var password: String?
) :
    Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParticipantCre> {
        override fun createFromParcel(parcel: Parcel): ParticipantCre {
            return ParticipantCre(parcel)
        }

        override fun newArray(size: Int): Array<ParticipantCre?> {
            return arrayOfNulls(size)
        }
    }

}

//data class ParticipantData(
//    @Expose @field:SerializedName("user_name") var user_name: String?,
//    @Expose @field:SerializedName("password") var password: String?
//): Serializable, Parcelable {
//
//    constructor(parcel: Parcel) : this(
//        parcel.readString(),
//        parcel.readString()
//
//    ) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(user_name)
//        parcel.writeString(password)
//
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<ParticipantData> {
//        override fun createFromParcel(parcel: Parcel): ParticipantData {
//            return ParticipantData(parcel)
//        }
//
//        override fun newArray(size: Int): Array<ParticipantData?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//}