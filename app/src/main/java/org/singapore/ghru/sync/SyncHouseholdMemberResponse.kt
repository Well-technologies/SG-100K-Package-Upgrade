package org.singapore.ghru.sync

import org.singapore.ghru.vo.request.Member

class SyncHouseholdMemberResponse(val eventType: SyncResponseEventType, val member: Member)