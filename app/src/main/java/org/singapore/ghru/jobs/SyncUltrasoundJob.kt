package org.singapore.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.singapore.ghru.sync.SyncResponseEventType
import org.singapore.ghru.sync.UltrasoundRequestRxBus
import org.singapore.ghru.vo.request.ParticipantRequest
import org.singapore.ghru.vo.request.UltrasoundRequest
import timber.log.Timber

class SyncUltrasoundJob(
    private val participantRequest: ParticipantRequest?,
    private val ultrasoundRequest : UltrasoundRequest
) : Job(
    Params(JobPriority.ULTRASOUND)
        .setRequiresNetwork(true)
        .groupBy("ultrasound")
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
        RemoteHouseholdService().getInstance().addUltrasound(participantRequest!!,ultrasoundRequest)
        UltrasoundRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS,ultrasoundRequest)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        UltrasoundRequestRxBus.getInstance().post(SyncResponseEventType.FAILED,ultrasoundRequest)
    }
    companion object {

        private val TAG = SyncUltrasoundJob::class.java.getCanonicalName()
    }
}