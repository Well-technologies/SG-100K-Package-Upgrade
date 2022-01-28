package org.singapore.ghru.vo

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
class ResourceDataWithMeta<T>(
    @field:SerializedName(value = "data")
    @Expose val data: T?,
    @Expose @field:SerializedName(value = "message")
    val message: String,
    @Expose @field:SerializedName(value = "error")
    val error: Boolean,
    @Expose @SerializedName("station_status")
    @ColumnInfo(name = "station_status")
    val stationStatus: Boolean,
    @Expose @SerializedName("status_code")
    @ColumnInfo(name = "status_code")
    val statusCode: Int,
    @Expose @SerializedName("is_cancelled")
    @ColumnInfo(name = "is_cancelled")
    val is_cancelled: Int,
    @Expose @SerializedName("meta") val meta: ParticipantListMeta?


)
