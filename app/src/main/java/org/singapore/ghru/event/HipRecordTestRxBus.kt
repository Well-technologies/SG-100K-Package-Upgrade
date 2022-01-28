package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.HipWaistData

class HipRecordTestRxBus private constructor() {

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

        private var instance: HipRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): HipRecordTestRxBus {
            if (instance == null) {
                instance = HipRecordTestRxBus()
            }
            return instance as HipRecordTestRxBus
        }
    }
}