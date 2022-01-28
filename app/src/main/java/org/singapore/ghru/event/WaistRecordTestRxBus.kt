package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.GripStrengthData
import org.singapore.ghru.vo.GripStrengthRecord
import org.singapore.ghru.vo.HipWaistData

class WaistRecordTestRxBus private constructor() {

    private val relay: PublishRelay<HipWaistData>

    init {
        relay = PublishRelay.create()
    }

    fun post(record: HipWaistData) {
        relay.accept(record)
    }

    fun toObservable(): Observable<HipWaistData> {
        return relay
    }

    companion object {

        private var instance: WaistRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): WaistRecordTestRxBus {
            if (instance == null) {
                instance = WaistRecordTestRxBus()
            }
            return instance as WaistRecordTestRxBus
        }
    }
}