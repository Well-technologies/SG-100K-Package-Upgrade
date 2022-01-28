package org.singapore.ghru.event

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.HOGTTDto

class HOGTTRxBus private constructor() {
    private val relay: PublishRelay<HOGTTDto>

    init {
        relay = PublishRelay.create()
    }

    fun post(hOGTT: HOGTTDto) {
        relay.accept(hOGTT)
    }

    fun toObservable(): Observable<HOGTTDto> {
        return relay
    }

    companion object {

        private var instance: HOGTTRxBus? = null

        @Synchronized
        fun getInstance(): HOGTTRxBus {
            if (instance == null) {
                instance = HOGTTRxBus()
            }
            return instance as HOGTTRxBus
        }
    }
}