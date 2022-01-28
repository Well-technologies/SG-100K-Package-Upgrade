package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.SpirometryRequest


class GripStrengthRequestRxBus private constructor() {

    private val relay: PublishRelay<GripStrengthRequest>

    init {
        relay = PublishRelay.create()
    }

    fun post(gripStrengthRequest: GripStrengthRequest) {
        relay.accept(gripStrengthRequest)
    }

    fun toObservable(): Observable<GripStrengthRequest> {
        return relay
    }

    companion object {

        private var instance: GripStrengthRequestRxBus? = null

        @Synchronized
        fun getInstance(): GripStrengthRequestRxBus {
            if (instance == null) {
                instance = GripStrengthRequestRxBus()
            }
            return instance as GripStrengthRequestRxBus
        }
    }
}