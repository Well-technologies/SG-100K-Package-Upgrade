package org.singapore.ghru.sync

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.singapore.ghru.vo.request.ParticipantX

class SyncParticipantRequestRxBus private constructor() {
    private val relay: PublishRelay<SyncParticipantRequestResponse>

    init {
        relay = PublishRelay.create()
    }

    fun post(eventType: SyncResponseEventType, participantRequest: ParticipantX) {
        relay.accept(SyncParticipantRequestResponse(eventType, participantRequest))
    }

    fun toObservable(): Observable<SyncParticipantRequestResponse> {
        return relay
    }

    companion object {

        private var instance: SyncParticipantRequestRxBus? = null

        @Synchronized
        fun getInstance(): SyncParticipantRequestRxBus {
            if (instance == null) {
                instance = SyncParticipantRequestRxBus()
            }
            return instance as SyncParticipantRequestRxBus
        }
    }
}