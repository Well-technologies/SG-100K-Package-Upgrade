package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.request.UltrasoundRequest

class UltrasoundRequestRxBus private constructor() {

    private val relay: PublishRelay<UltrasoundRequestResponce>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, ultrasoundRequest: UltrasoundRequest) {
        relay.accept(UltrasoundRequestResponce(eventType, ultrasoundRequest))
    }

    fun toObservable(): Observable<UltrasoundRequestResponce> {
        return relay
    }

    companion object {

        private var instance: UltrasoundRequestRxBus? = null

        @Synchronized
        fun getInstance(): UltrasoundRequestRxBus {
            if (instance == null) {
                instance = UltrasoundRequestRxBus()
            }
            return instance as UltrasoundRequestRxBus
        }
    }
}