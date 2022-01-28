package org.singapore.ghru.jobs

import com.birbit.android.jobqueue.Job
import com.birbit.android.jobqueue.Params
import com.birbit.android.jobqueue.RetryConstraint
import org.singapore.ghru.sync.SyncHouseholdRequestRxBus
import org.singapore.ghru.sync.SyncResponseEventType
import org.singapore.ghru.vo.request.HouseholdRequest
import timber.log.Timber

class SyncHouseholdRequestJob(private val household: HouseholdRequest) : Job(
    Params(JobPriority.HOUSEHOLD)
        .setRequiresNetwork(true)
        .groupBy("Household")
        .persist()
) {

    override fun onAdded() {
        Timber.d("Executing onAdded() for comment $household")
    }

    override fun shouldReRunOnThrowable(throwable: Throwable, runCount: Int, maxRunCount: Int): RetryConstraint {
        if (throwable is RemoteException) {

            val statusCode = throwable.response.code()
            if (statusCode >= 422 && statusCode < 500) {
                return RetryConstraint.CANCEL
            }
        }
        // if we are here, most likely the connection was lost during job execution
        return RetryConstraint.createExponentialBackoff(runCount, 1000);
    }

    override fun onRun() {
        Timber.d("Executing onRun() for household $household")
        // RemoteHouseholdService().getInstance().addHouseholdRequest(token, household)
        household.syncPending = false
        RemoteHouseholdService().getInstance().provideDb(this.getApplicationContext()).householdRequestDao()
            .update(household)
        SyncHouseholdRequestRxBus.getInstance().post(SyncResponseEventType.SUCCESS, household)
    }

    override fun onCancel(cancelReason: Int, throwable: Throwable?) {
        Timber.d("canceling job. reason: %d, throwable: %s", cancelReason, throwable)
        //Crashlytics.logException(throwable)
        //Crashlytics.log("household " + household.toString())

        // sync to remote failed
        SyncHouseholdRequestRxBus.getInstance().post(SyncResponseEventType.FAILED, household)
    }
}