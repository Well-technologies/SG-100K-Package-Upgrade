package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.GripStrengthData
import org.singapore.ghru.vo.GripStrengthRecord

class Reading3RecordTestRxBus private constructor() {

    private val relay: PublishRelay<GripStrengthData>

    init {
        relay = PublishRelay.create()
    }

    fun post(record: GripStrengthData) {
        relay.accept(record)
    }

    fun toObservable(): Observable<GripStrengthData> {
        return relay
    }

    companion object {

        private var instance: Reading3RecordTestRxBus? = null

        @Synchronized
        fun getInstance(): Reading3RecordTestRxBus {
            if (instance == null) {
                instance = Reading3RecordTestRxBus()
            }
            return instance as Reading3RecordTestRxBus
        }
    }
}