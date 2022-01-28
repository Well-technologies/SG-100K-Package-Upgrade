package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.VisualAcuityRequest


class VisualAcuityRequestRxBus private constructor() {

    private val relay: PublishRelay<VisualAcuityRequest>

    init {
        relay = PublishRelay.create()
    }

    fun post(visualAcuityRequest: VisualAcuityRequest) {
        relay.accept(visualAcuityRequest)
    }

    fun toObservable(): Observable<VisualAcuityRequest> {
        return relay
    }

    companion object {

        private var instance: VisualAcuityRequestRxBus? = null

        @Synchronized
        fun getInstance(): VisualAcuityRequestRxBus {
            if (instance == null) {
                instance = VisualAcuityRequestRxBus()
            }
            return instance as VisualAcuityRequestRxBus
        }
    }
}