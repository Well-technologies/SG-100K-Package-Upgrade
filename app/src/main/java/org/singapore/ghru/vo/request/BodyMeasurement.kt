package org.singapore.ghru.vo.request

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.singapore.ghru.BR
import org.singapore.ghru.vo.Meta
import java.io.Serializable


data class BodyMeasurementMetaResonce(
    @Expose @SerializedName("error") val meta: Boolean?,
    @Expose @SerializedName("data") val data: BodyMeasurementMetaResonceData?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readParcelable(BodyMeasurementMetaResonceData::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(meta)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMetaResonce> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMetaResonce {
            return BodyMeasurementMetaResonce(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMetaResonce?> {
            return arrayOfNulls(size)
        }
    }


}

data class BodyMeasurementMetaResonceData(
    @Expose @SerializedName("error") val meta: Boolean?,
    @Expose @SerializedName("station") val station: BodyMeasurementMetaResonceStation?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readParcelable(BodyMeasurementMetaResonceStation::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(meta)
        parcel.writeParcelable(station, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMetaResonceData> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMetaResonceData {
            return BodyMeasurementMetaResonceData(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMetaResonceData?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurementMetaResonceStation(
    @Expose @SerializedName("error") val meta: Boolean?,
    @Expose @SerializedName("data") val data: BodyMeasurementMeta?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readParcelable(BodyMeasurementMeta::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(meta)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMetaResonceStation> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMetaResonceStation {
            return BodyMeasurementMetaResonceStation(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMetaResonceStation?> {
            return arrayOfNulls(size)
        }
    }

}

@Entity(tableName = "body_measurement_meta")
data class BodyMeasurementMeta(
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") val meta: Meta?,
    @Embedded(prefix = "body")@Expose @SerializedName("body") val body: BodyMeasurement?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(BodyMeasurement::class.java.classLoader)
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
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
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(body, flags)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMeta> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMeta {
            return BodyMeasurementMeta(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMeta?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurementMetaNew(
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") val meta: Meta?,
    @Embedded(prefix = "body")@Expose @SerializedName("body") val body: BodyMeasurement?,
    @Expose @SerializedName("is_cancelled") var isCancelled: Int? = 0
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(BodyMeasurement::class.java.classLoader)
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
        isCancelled = parcel.readValue(Int::class.java.classLoader) as? Int
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
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(body, flags)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeValue(isCancelled)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMetaNew> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMetaNew {
            return BodyMeasurementMetaNew(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMetaNew?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurement(
    @Embedded(prefix = "height") @Expose @SerializedName("height") val height: BodyMeasurementDataNew?,
    @Embedded(prefix = "weight")@Expose @SerializedName("weight") val bodyComposition: BodyMeasurementDataNew?
) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BodyMeasurementDataNew::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementDataNew::class.java.classLoader)

    ) {
        readContraindications(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(height, flags)
        parcel.writeParcelable(bodyComposition, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurement> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurement {
            return BodyMeasurement(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurement?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}


data class BodyMeasurementData(
    @Expose @SerializedName("device_id") var deviceId: String?,
    @Expose @SerializedName("comment") var comment: String?,
    @Embedded(prefix = "data") @Expose @SerializedName("data") var data: BodyMeasurementValueData?,
    @Embedded(prefix = "skip")  @Expose @SerializedName("skip") var skip: CancelRequest?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(BodyMeasurementValueData::class.java.classLoader),
        parcel.readParcelable(CancelRequest::class.java.classLoader)
    ) {
    }

    constructor() : this(null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(comment)
        parcel.writeParcelable(data, flags)
        parcel.writeParcelable(skip, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementData> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementData {
            return BodyMeasurementData(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementData?> {
            return arrayOfNulls(size)
        }
    }


}

data class BodyMeasurementDataNew(
    @Expose @SerializedName("device_id") var deviceId: String?,
    @Expose @SerializedName("comment") var comment: String?,
    @Expose @SerializedName("unit") var unit: String?,
    @Expose @SerializedName("value") var value: Double?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    constructor() : this(null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(comment)
        parcel.writeString(unit)
        parcel.writeValue(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementDataNew> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementDataNew {
            return BodyMeasurementDataNew(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementDataNew?> {
            return arrayOfNulls(size)
        }
    }
}

data class BodyMeasurementValueData(
    @Embedded(prefix = "height") @Expose @SerializedName("height") val height: BodyMeasurementValueDto?,
    @Embedded(prefix = "fat_composition") @Expose @SerializedName("fat_composition") val fatComposition: BodyMeasurementValueDto?,
    @Embedded(prefix = "visceral") @Expose @SerializedName("visceral") val visceral: BodyMeasurementValueDto?,
    @Embedded(prefix = "muscle") @Expose @SerializedName("muscle") val muscle: BodyMeasurementValueDto?,
    @Embedded(prefix = "hip") @Expose @SerializedName("hip") val hip: BodyMeasurementValueDto?,
    @Embedded(prefix = "waist") @Expose @SerializedName("waist") val waist: BodyMeasurementValueDto?,
    @Embedded(prefix = "weight") @Expose @SerializedName("weight") val weight: BodyMeasurementValueDto?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementValueDto::class.java.classLoader)
    ) {
    }

    constructor(height: BodyMeasurementValueDto) : this(
        height = height, fatComposition = null, visceral = null,
        muscle = null, hip = null, waist = null, weight = null
    )

    constructor(hip: BodyMeasurementValueDto, waist: BodyMeasurementValueDto) : this(
        height = null, fatComposition = null, visceral = null,
        muscle = null, hip = hip, waist = waist, weight = null
    )

    constructor(
        visceral: BodyMeasurementValueDto?,
        muscle: BodyMeasurementValueDto?,
        fatComposition: BodyMeasurementValueDto?,
        weight: BodyMeasurementValueDto
    ) : this(
        height = null, fatComposition = fatComposition, visceral = visceral,
        muscle = muscle, hip = null, waist = null, weight = weight
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(height, flags)
        parcel.writeParcelable(fatComposition, flags)
        parcel.writeParcelable(visceral, flags)
        parcel.writeParcelable(muscle, flags)
        parcel.writeParcelable(hip, flags)
        parcel.writeParcelable(waist, flags)
        parcel.writeParcelable(weight, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementValueData> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementValueData {
            return BodyMeasurementValueData(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementValueData?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurementValueDto(
    @Expose @SerializedName("unit") val unit: String?,
    @Expose @SerializedName("value") val value: Double?

) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(unit)
        parcel.writeValue(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementValueDto> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementValueDto {
            return BodyMeasurementValueDto(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementValueDto?> {
            return arrayOfNulls(size)
        }
    }
}

class BodyMeasurementValue : BaseObservable(), Serializable {

    companion object {
        fun build(): BodyMeasurementValue {
            val bodyMeasurementValue = BodyMeasurementValue()
            bodyMeasurementValue.unit = String()
            bodyMeasurementValue.value = String()
            bodyMeasurementValue.comment = String()
            bodyMeasurementValue.deviceId = String()
            return bodyMeasurementValue
        }
    }


    var value: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.value)
        }
        @Bindable get() = field


    var unit: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.unit)
        }
        @Bindable get() = field


    var comment: String = String()
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }
        @Bindable get() = field


    var deviceId: String = String()
        set(value) {
            field = value
        }
        @Bindable get() = field


}

data class BodyMeasurementMeta1(
    @Expose @SerializedName("meta") val meta: Meta?,
    @Expose @SerializedName("body") val body: BodyMeasurement1?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(BodyMeasurement1::class.java.classLoader)
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
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
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(body, flags)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMeta1> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMeta1 {
            return BodyMeasurementMeta1(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMeta1?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurement1(
    @Expose @SerializedName("Height") val height: BodyMeasurementDataNew1?,
    @Expose @SerializedName("Weight") val bodyComposition: BodyMeasurementDataNew1?
) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BodyMeasurementDataNew1::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementDataNew1::class.java.classLoader)

    ) {
        readContraindications(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(height, flags)
        parcel.writeParcelable(bodyComposition, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurement1> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurement1 {
            return BodyMeasurement1(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurement1?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}

data class BodyMeasurementDataNew1(
    @Expose @SerializedName("device_id") var deviceId: String?,
    @Expose @SerializedName("comment") var comment: String?,
    @Expose @SerializedName("unit") var unit: String?,
    @Expose @SerializedName("value") var value: Double?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double
    ) {
    }

    constructor() : this(null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(comment)
        parcel.writeString(unit)
        parcel.writeValue(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementDataNew1> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementDataNew1 {
            return BodyMeasurementDataNew1(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementDataNew1?> {
            return arrayOfNulls(size)
        }
    }
}

@Entity(tableName = "body_measurement_without_readings")
data class BodyMeasurementMetaWithoutReadings(
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") val meta: Meta?,
    @Embedded(prefix = "body")@Expose @SerializedName("body") val body: BodyMeasurementWithoutReadings?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementWithoutReadings::class.java.classLoader)
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()!!
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
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(body, flags)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMetaWithoutReadings> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMetaWithoutReadings {
            return BodyMeasurementMetaWithoutReadings(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMetaWithoutReadings?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurementWithoutReadings(
    @Expose @SerializedName("device_id") var deviceId: String?,
    @Expose @SerializedName("comment") var comment: String?
) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("questions")
    var questions: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
        readContraindications(parcel)
        readQuestions(parcel)
    }

    constructor() : this(null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(comment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementWithoutReadings> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementWithoutReadings {
            return BodyMeasurementWithoutReadings(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementWithoutReadings?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun readQuestions(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }
}

//data class DxaHeightWeight(
//    @Embedded(prefix = "meta") @Expose @SerializedName("meta") val meta: Meta?,
//    @Embedded(prefix = "body")@Expose @SerializedName("body") val body: BodyMeasurement?,
//    @Expose @SerializedName("is_cancelled") var isCancelled: Int? = 0,
//    @Expose @SerializedName("status_code") var statusCode: Int? = 0
//) : Serializable, Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readParcelable(Meta::class.java.classLoader),
//        parcel.readParcelable(BodyMeasurement::class.java.classLoader)
//    ) {
//        id = parcel.readLong()
//        timestamp = parcel.readLong()
//        syncPending = parcel.readByte() != 0.toByte()
//        screeningId = parcel.readString()
//        isCancelled = parcel.readValue(Int::class.java.classLoader) as? Int
//        statusCode = parcel.readValue(Int::class.java.classLoader) as? Int
//    }
//
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0
//    @ColumnInfo(name = "timestamp")
//    var timestamp: Long = System.currentTimeMillis()
//
//    @ColumnInfo(name = "sync_pending")
//    var syncPending: Boolean = false
//
//    @ColumnInfo(name = "screening_id")
//    lateinit var screeningId: String
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeParcelable(meta, flags)
//        parcel.writeParcelable(body, flags)
//        parcel.writeLong(id)
//        parcel.writeLong(timestamp)
//        parcel.writeByte(if (syncPending) 1 else 0)
//        parcel.writeValue(isCancelled)
//        parcel.writeString(screeningId)
//        parcel.writeValue(statusCode)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<DxaHeightWeight> {
//        override fun createFromParcel(parcel: Parcel): DxaHeightWeight {
//            return DxaHeightWeight(parcel)
//        }
//
//        override fun newArray(size: Int): Array<DxaHeightWeight?> {
//            return arrayOfNulls(size)
//        }
//    }
//
//}

// new object for manual and auto readings

@Entity(tableName = "body_measurement_meta_manual_auto")
data class BodyMeasurementMetaManualAuto(
    @Embedded(prefix = "meta") @Expose @SerializedName("meta") val meta: Meta?,
    @Embedded(prefix = "body")@Expose @SerializedName("body") val body: BodyMeasurementManualAuto?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Meta::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementManualAuto::class.java.classLoader)
    ) {
        id = parcel.readLong()
        timestamp = parcel.readLong()
        syncPending = parcel.readByte() != 0.toByte()
        screeningId = parcel.readString()
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
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(body, flags)
        parcel.writeLong(id)
        parcel.writeLong(timestamp)
        parcel.writeByte(if (syncPending) 1 else 0)
        parcel.writeString(screeningId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementMetaManualAuto> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementMetaManualAuto {
            return BodyMeasurementMetaManualAuto(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementMetaManualAuto?> {
            return arrayOfNulls(size)
        }
    }

}

data class BodyMeasurementManualAuto(
    @Embedded(prefix = "height") @Expose @SerializedName("height") val height: BodyMeasurementDataManualAuto?,
    @Embedded(prefix = "weight")@Expose @SerializedName("weight") val bodyComposition: BodyMeasurementDataManualAuto?
) : Serializable, Parcelable {

    @Ignore
    @Expose
    @SerializedName("contraindications")
    var contraindications: List<Map<String, String>>? = null

    @Ignore
    @Expose
    @SerializedName("is_manual_entry")
    var isManualEntry: Boolean = false

    @Ignore
    @Expose
    @SerializedName("questions")
    var is_captured: List<Map<String, String>>? = null

    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BodyMeasurementDataManualAuto::class.java.classLoader),
        parcel.readParcelable(BodyMeasurementDataManualAuto::class.java.classLoader)

    ) {
        readContraindications(parcel)
        isManualEntry = parcel.readByte() != 0.toByte()
        getIsCaptured(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(height, flags)
        parcel.writeParcelable(bodyComposition, flags)
        parcel.writeByte(if (isManualEntry) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementManualAuto> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementManualAuto {
            return BodyMeasurementManualAuto(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementManualAuto?> {
            return arrayOfNulls(size)
        }

        private fun readContraindications(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }

        private fun getIsCaptured(parcel: Parcel): List<Map<String, String>> {
            val list = mutableListOf<Map<String, String>>()
            parcel.readList(list as List<*>, Map::class.java.classLoader)

            return list
        }
    }

}

data class BodyMeasurementDataManualAuto(
    @Expose @SerializedName("device_id") var deviceId: String?,
    @Expose @SerializedName("comment") var comment: String?,
    @Expose @SerializedName("unit") var unit: String?,
    @Expose @SerializedName("value") var value: String?
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    constructor() : this(null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceId)
        parcel.writeString(comment)
        parcel.writeString(unit)
        parcel.writeValue(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BodyMeasurementDataManualAuto> {
        override fun createFromParcel(parcel: Parcel): BodyMeasurementDataManualAuto {
            return BodyMeasurementDataManualAuto(parcel)
        }

        override fun newArray(size: Int): Array<BodyMeasurementDataManualAuto?> {
            return arrayOfNulls(size)
        }
    }
}

