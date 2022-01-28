package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.request.BloodPressureMetaRequest
import org.singapore.ghru.vo.request.BodyMeasurementMeta
import org.singapore.ghru.vo.request.HeightWeightMeasurementMeta


class BodyMeasurementMetaRxBus private constructor() {

    private val relay: PublishRelay<BodyMeasurementMeta>

    init {
        relay = PublishRelay.create()
    }

    fun post(bodyMeasurementMeta: BodyMeasurementMeta) {
        relay.accept(bodyMeasurementMeta)
    }

    fun toObservable(): Observable<BodyMeasurementMeta> {
        return relay
    }

//    private val relay: PublishRelay<HeightWeightMeasurementMeta>
//
//    init {
//        relay = PublishRelay.create()
//    }
//
//    fun post(bodyMeasurementMeta: HeightWeightMeasurementMeta) {
//        relay.accept(bodyMeasurementMeta)
//    }
//
//    fun toObservable(): Observable<HeightWeightMeasurementMeta> {
//        return relay
//    }

    companion object {

        private var instance: BodyMeasurementMetaRxBus? = null

        @Synchronized
        fun getInstance(): BodyMeasurementMetaRxBus {
            if (instance == null) {
                instance = BodyMeasurementMetaRxBus()
            }
            return instance as BodyMeasurementMetaRxBus
        }
    }
}