package org.singapore.ghru.sync

import org.singapore.ghru.vo.request.DXARequest

class DXARequestResponce(
    val eventType: SyncResponseEventType,
    val dxaRequest: DXARequest
)
{
}