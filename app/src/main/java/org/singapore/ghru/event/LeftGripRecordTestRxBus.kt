package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.GripStrengthData
import org.singapore.ghru.vo.GripStrengthRecord

class LeftGripRecordTestRxBus private constructor() {

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

        private var instance: LeftGripRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): LeftGripRecordTestRxBus {
            if (instance == null) {
                instance = LeftGripRecordTestRxBus()
            }
            return instance as LeftGripRecordTestRxBus
        }
    }
}