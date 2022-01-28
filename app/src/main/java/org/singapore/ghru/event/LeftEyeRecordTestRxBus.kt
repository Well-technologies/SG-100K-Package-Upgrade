package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.VisualAcuityData

class LeftEyeRecordTestRxBus private constructor() {

    private val relay: PublishRelay<VisualAcuityData>

    init {
        relay = PublishRelay.create()
    }

    fun post(record: VisualAcuityData) {
        relay.accept(record)
    }

    fun toObservable(): Observable<VisualAcuityData> {
        return relay
    }

    companion object {

        private var instance: LeftEyeRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): LeftEyeRecordTestRxBus {
            if (instance == null) {
                instance = LeftEyeRecordTestRxBus()
            }
            return instance as LeftEyeRecordTestRxBus
        }
    }
}