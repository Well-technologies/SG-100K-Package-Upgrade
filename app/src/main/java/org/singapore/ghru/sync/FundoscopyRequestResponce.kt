package org.singapore.ghru.sync

import org.singapore.ghru.vo.FundoscopyRequest

class FundoscopyRequestResponce(
    val eventType: SyncResponseEventType,
    val fundoscopyRequest: FundoscopyRequest
)
{
}