package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.HipWaistRequest
import org.singapore.ghru.vo.SpirometryRequest


class HipWaistRequestRxBus private constructor() {

    private val relay: PublishRelay<HipWaistRequest>

    init {
        relay = PublishRelay.create()
    }

    fun post(hipWaistRequest: HipWaistRequest) {
        relay.accept(hipWaistRequest)
    }

    fun toObservable(): Observable<HipWaistRequest> {
        return relay
    }

    companion object {

        private var instance: HipWaistRequestRxBus? = null

        @Synchronized
        fun getInstance(): HipWaistRequestRxBus {
            if (instance == null) {
                instance = HipWaistRequestRxBus()
            }
            return instance as HipWaistRequestRxBus
        }
    }
}