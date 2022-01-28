package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import java.io.Serializable

class GripStrengthRecord() : Serializable, Parcelable {

    var deviceId: MutableLiveData<String> = MutableLiveData<String>().apply { }
    var comment: MutableLiveData<String> = MutableLiveData<String>().apply { }
    var unit: MutableLiveData<String> = MutableLiveData<String>().apply { }
    var value: MutableLiveData<List<String>> = MutableLiveData<List<String>>().apply { }

    constructor(parcel: Parcel) : this() {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GripRecord(deviceId=${deviceId.value}, comment=${comment.value}, unit=${unit.value}, value=${value.value})"
    }

    companion object CREATOR : Parcelable.Creator<GripStrengthRecord> {
        override fun createFromParcel(parcel: Parcel): GripStrengthRecord {
            return GripStrengthRecord(parcel)
        }

        override fun newArray(size: Int): Array<GripStrengthRecord?> {
            return arrayOfNulls(size)
        }
    }


}