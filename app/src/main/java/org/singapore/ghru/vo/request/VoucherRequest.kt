package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.vo.Meta
import java.io.Serializable


data class VoucherRequest(
    @Expose var voucher_id: String

) : Serializable, Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(voucher_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VoucherRequest> {
        override fun createFromParcel(parcel: Parcel): VoucherRequest {
            return VoucherRequest(parcel)
        }

        override fun newArray(size: Int): Array<VoucherRequest?> {
            return arrayOfNulls(size)
        }
    }


}




