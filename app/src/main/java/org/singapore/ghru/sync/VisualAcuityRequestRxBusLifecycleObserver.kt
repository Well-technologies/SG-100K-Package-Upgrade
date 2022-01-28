package org.singapore.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.db.VisualAcuityRequestDao
import org.singapore.ghru.vo.VisualAcuityRequest
import timber.log.Timber

/**
 * Updates local database after remote comment sync requests
 */
class VisualAcuityRequestRxBusLifecycleObserver(var visualAcuityRequestDao: VisualAcuityRequestDao) :
    LifecycleObserver {
    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            VisualAcuityRequestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "visualAcuity SyncCommentLifecycleObserver ${result}")
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

    private fun handleSyncResponse(response: VisualAcuityRequest) {
        onSyncCommentSuccess(response)
    }

    private fun onSyncCommentSuccess(visualAcuityRequest: VisualAcuityRequest) {
        //Timber.d("received sync comment success event for comment %s", household.bodyMeasurementRequest)
        visualAcuityRequest.syncPending = false
        visualAcuityRequestDao.update(visualAcuityRequest?.screeningId)
        //Timber.d("received sync comment success event for householdId %s", household.bodyMeasurementRequest)


    }
}