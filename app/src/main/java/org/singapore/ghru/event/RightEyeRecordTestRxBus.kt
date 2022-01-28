package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.VisualAcuityData

class RightEyeRecordTestRxBus private constructor() {

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

        private var instance: RightEyeRecordTestRxBus? = null

        @Synchronized
        fun getInstance(): RightEyeRecordTestRxBus {
            if (instance == null) {
                instance = RightEyeRecordTestRxBus()
            }
            return instance as RightEyeRecordTestRxBus
        }
    }
}