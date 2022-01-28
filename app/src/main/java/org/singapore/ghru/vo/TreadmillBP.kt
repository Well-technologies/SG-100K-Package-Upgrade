package org.singapore.ghru.vo

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData


class TreadmillBP(var id: Int) : Parcelable {

    var systolic: MutableLiveData<String> = MutableLiveData<String>().apply { }
    var diastolic: MutableLiveData<String> = MutableLiveData<String>().apply { }
    var pulse: MutableLiveData<String> = MutableLiveData<String>().apply { }
    var stage: MutableLiveData<String> = MutableLiveData<String>().apply { }

    constructor(parcel: Parcel) : this(parcel.readInt()) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TreadmillBP> {
        override fun createFromParcel(parcel: Parcel): TreadmillBP {
            return TreadmillBP(parcel)
        }

        override fun newArray(size: Int): Array<TreadmillBP?> {
            return arrayOfNulls(size)
        }
    }

}


enum class Stage(val stage: String) {
    RESTING("Resting (0%)"),
    FOUR("4kph (0%)"),
    FIVE("5kph (0%)"),
    SIXZERO("6kph (0%)"),
    SIXTHREE("6kph (3%)"),
    SIXSIX("6kph (6%)"),
    AFTER("After Test")
}



