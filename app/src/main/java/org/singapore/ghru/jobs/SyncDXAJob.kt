package org.singapore.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.singapore.ghru.sync.DXARequestRxBus
import org.singapore.ghru.sync.SyncResponseEventType
import org.singapore.ghru.vo.request.DXARequest
import org.singapore.ghru.vo.request.ParticipantRequest
import timber.log.Timber

class SyncDXAJob(
    private val participantRequest: ParticipantRequest?,
    private val dxaRequest : DXARequest
) : Job(
    Params(JobPriority.DXA)
        .setRequiresNetwork(true)
        .groupBy("dxa")
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
        RemoteHouseholdService().getInstance().addDXA(participantRequest!!,dxaRequest)
        DXARequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS,dxaRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        DXARequestRxBus.getInstance().post(SyncResponseEventType.FAILED,dxaRequest)
    }
    companion object {

        private val TAG = SyncDXAJob::class.java.getCanonicalName()
    }
}