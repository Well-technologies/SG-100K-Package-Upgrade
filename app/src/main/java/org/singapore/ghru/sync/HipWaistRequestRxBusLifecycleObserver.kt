package org.singapore.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.db.GripStrengthRequestDao
import org.singapore.ghru.db.HipWaistRequestDao
import org.singapore.ghru.db.SpiromentryRequestDao
import org.singapore.ghru.vo.GripStrengthRequest
import org.singapore.ghru.vo.HipWaistRequest
import org.singapore.ghru.vo.SpirometryRequest
import timber.log.Timber

/**
 * Updates local database after remote comment sync requests
 */
class HipWaistRequestRxBusLifecycleObserver(var hipWaistRequestDao: HipWaistRequestDao) :
    LifecycleObserver {
    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            HipWaistRequestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "hipWaist SyncCommentLifecycleObserver ${result}")
                    handleSyncResponse(result)
                }, { error ->
                    error.printStackTrace()
                })
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Timber.d("onPause lifecycle event.")
        disposables.clear()
    }

    private fun handleSyncResponse(response: HipWaistRequest) {
        onSyncCommentSuccess(response)
    }

    private fun onSyncCommentSuccess(hipWaistRequest: HipWaistRequest) {
        //Timber.d("received sync comment success event for comment %s", household.bodyMeasurementRequest)
        hipWaistRequest.syncPending = false
        hipWaistRequestDao.update(hipWaistRequest?.screeningId)
        //Timber.d("received sync comment success event for householdId %s", household.bodyMeasurementRequest)


    }
}