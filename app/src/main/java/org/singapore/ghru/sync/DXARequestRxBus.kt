package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.request.DXARequest

class DXARequestRxBus private constructor() {

    private val relay: PublishRelay<DXARequestResponce>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, dxaRequest: DXARequest) {
        relay.accept(DXARequestResponce(eventType, dxaRequest))
    }

    fun toObservable(): Observable<DXARequestResponce> {
        return relay
    }

    companion object {

        private var instance: DXARequestRxBus? = null

        @Synchronized
        fun getInstance(): DXARequestRxBus {
            if (instance == null) {
                instance = DXARequestRxBus()
            }
            return instance as DXARequestRxBus
        }
    }
}