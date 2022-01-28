package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.TreadmillBP


class TreadmillBPRecordRxBus private constructor() {

    private val relay: PublishRelay<TreadmillBP>
    private val relayReset: PublishRelay<Int>

    init {
        relayReset = PublishRelay.create()
    }

    init {
        relay = PublishRelay.create()
    }

    fun post(record: TreadmillBP) {
        relay.accept(record)
    }

    fun post(record: Int) {
        relayReset.accept(record)
    }

    fun toObservable(): Observable<TreadmillBP> {
        return relay
    }

    fun toObservableReset(): Observable<Int> {
        return relayReset
    }

    companion object {

        private var instance: TreadmillBPRecordRxBus? = null

        @Synchronized
        fun getInstance(): TreadmillBPRecordRxBus {
            if (instance == null) {
                instance = TreadmillBPRecordRxBus()
            }
            return instance as TreadmillBPRecordRxBus
        }
    }
}