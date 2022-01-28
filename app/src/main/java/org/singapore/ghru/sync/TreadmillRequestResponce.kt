package org.singapore.ghru.sync

import org.singapore.ghru.vo.request.TreadmillRequest

class TreadmillRequestResponce(
    val eventType: SyncResponseEventType,
    val treadmillRequest: TreadmillRequest
)
{
}