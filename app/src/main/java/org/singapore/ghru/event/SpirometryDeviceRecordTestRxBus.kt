package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import com.nuvoair.sdk.launcher.NuvoairLauncherMeasurement
import io.reactivex.Observable

class SpirometryDeviceRecordTestRxBus private constructor() {

    private val relay: PublishRelay<NuvoairLauncherMeasurement>

    init {
        relay = PublishRelay.create()
    }

    fun post(record: NuvoairLauncherMeasurement) {
        relay.accept(record)
    }

    fun toObservable(): Observable<NuvoairLauncherMeasurement> {
        return relay
    }

    companion object {

        private var instance: SpirometryDeviceRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): SpirometryDeviceRecordTestRxBus {
            if (instance == null) {
                instance = SpirometryDeviceRecordTestRxBus()
            }
            return instance as SpirometryDeviceRecordTestRxBus
        }
    }
}