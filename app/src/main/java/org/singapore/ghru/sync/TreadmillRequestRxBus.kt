package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.request.TreadmillRequest

class TreadmillRequestRxBus private constructor() {

    private val relay: PublishRelay<TreadmillRequestResponce>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, treadmillRequest: TreadmillRequest) {
        relay.accept(TreadmillRequestResponce(eventType, treadmillRequest))
    }

    fun toObservable(): Observable<TreadmillRequestResponce> {
        return relay
    }

    companion object {

        private var instance: TreadmillRequestRxBus? = null

        @Synchronized
        fun getInstance(): TreadmillRequestRxBus {
            if (instance == null) {
                instance = TreadmillRequestRxBus()
            }
            return instance as TreadmillRequestRxBus
        }
    }
}