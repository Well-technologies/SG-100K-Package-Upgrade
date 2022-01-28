package org.singapore.ghru.sync

import org.singapore.ghru.vo.request.UltrasoundRequest

class UltrasoundRequestResponce(
    val eventType: SyncResponseEventType,
    val ultrasoundRequest: UltrasoundRequest
)
{
}