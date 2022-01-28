package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.*


class CognitionRequestRxBus private constructor() {

    private val relay: PublishRelay<CognitionRequest>

    init {
        relay = PublishRelay.create()
    }

    fun post(cognitionRequest: CognitionRequest) {
        relay.accept(cognitionRequest)
    }

    fun toObservable(): Observable<CognitionRequest> {
        return relay
    }

    companion object {

        private var instance: CognitionRequestRxBus? = null

        @Synchronized
        fun getInstance(): CognitionRequestRxBus {
            if (instance == null) {
                instance = CognitionRequestRxBus()
            }
            return instance as CognitionRequestRxBus
        }
    }
}