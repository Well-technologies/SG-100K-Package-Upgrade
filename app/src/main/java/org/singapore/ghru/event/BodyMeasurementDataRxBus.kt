package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.request.BodyMeasurementData
import org.singapore.ghru.vo.request.BodyMeasurementDataNew
import org.singapore.ghru.vo.request.HeightWeightMeasurementData

class BodyMeasurementDataRxBus private constructor() {
    private val relay: PublishRelay<BodyMeasurementDataResponse>

    init {
        relay = PublishRelay.create()
    }

    fun post(bodyMeasurementDataResponse: BodyMeasurementDataResponse) {
        relay.accept(bodyMeasurementDataResponse)
    }

    fun toObservable(): Observable<BodyMeasurementDataResponse> {
        return relay
    }

    companion object {

        private var instance: BodyMeasurementDataRxBus? = null

        @Synchronized
        fun getInstance(): BodyMeasurementDataRxBus {
            if (instance == null) {
                instance = BodyMeasurementDataRxBus()
            }
            return instance as BodyMeasurementDataRxBus
        }
    }
}

class BodyMeasurementDataResponse(
    val eventType: BodyMeasurementDataEventType,
    val bodyMeasurementData: BodyMeasurementDataNew
//    val bodyMeasurementData: HeightWeightMeasurementData
)

enum class BodyMeasurementDataEventType {
    HEIGHT,
    BODY_COMOSITION,
    HIP_WAIST
}
