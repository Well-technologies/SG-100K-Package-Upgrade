package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.*


class FFQRequestRxBus private constructor() {

    private val relay: PublishRelay<FFQRequest>

    init {
        relay = PublishRelay.create()
    }

    fun post(ffqRequest: FFQRequest) {
        relay.accept(ffqRequest)
    }

    fun toObservable(): Observable<FFQRequest> {
        return relay
    }

    companion object {

        private var instance: FFQRequestRxBus? = null

        @Synchronized
        fun getInstance(): FFQRequestRxBus {
            if (instance == null) {
                instance = FFQRequestRxBus()
            }
            return instance as FFQRequestRxBus
        }
    }
}