package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.GripStrengthData
import org.singapore.ghru.vo.GripStrengthRecord

class RightGripRecordTestRxBus private constructor() {

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

        private var instance: RightGripRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): RightGripRecordTestRxBus {
            if (instance == null) {
                instance = RightGripRecordTestRxBus()
            }
            return instance as RightGripRecordTestRxBus
        }
    }
}