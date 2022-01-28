package org.singapore.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.singapore.ghru.sync.SyncResponseEventType
import org.singapore.ghru.sync.TreadmillRequestRxBus
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.TreadmillRequest
import timber.log.Timber

class SyncTreadmillJob(
    private val participantRequest: ParticipantRequest?,
    private val treadmillRequest : TreadmillRequest
) : Job(
    Params(JobPriority.TREADMILL)
        .setRequiresNetwork(true)
        .groupBy("treadmill")
        .persist()
) {


    override fun onAdded() {
        Timber.d("Executing onAdded() for comment $participantRequest")
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        if (throwable is RemoteException) {

            val statusCode = throwable.response.code()
            if (statusCode in 422..499) {
                return RetryConstraint.CANCEL
            }
        }
        // if we are here, most likely the connection was lost during job execution
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }

    override fun onRun() {
        Timber.d("Executing onRun() for household $participantRequest")
        RemoteHouseholdService().getInstance().addTreadmill(participantRequest!!,treadmillRequest)
        TreadmillRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS,treadmillRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        TreadmillRequestRxBus.getInstance().post(SyncResponseEventType.FAILED,treadmillRequest)
    }
    companion object {

        private val TAG = SyncTreadmillJob::class.java.getCanonicalName()
    }
}