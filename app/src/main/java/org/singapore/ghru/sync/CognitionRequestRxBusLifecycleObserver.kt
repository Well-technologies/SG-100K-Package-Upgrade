package org.singapore.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.db.*
import org.singapore.ghru.vo.*
import timber.log.Timber

/**
 * Updates local database after remote comment sync requests
 */
class CognitionRequestRxBusLifecycleObserver(var cognitionRequestDao: CognitionRequestDao) :
    LifecycleObserver {
    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            CognitionRequestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "Cognition SyncCommentLifecycleObserver ${result}")
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

    private fun handleSyncResponse(response: CognitionRequest) {
        onSyncCommentSuccess(response)
    }

    private fun onSyncCommentSuccess(cognitionRequest: CognitionRequest) {
        //Timber.d("received sync comment success event for comment %s", household.bodyMeasurementRequest)
        cognitionRequest.syncPending = false
        cognitionRequestDao.update(cognitionRequest?.screeningId)
        //Timber.d("received sync comment success event for householdId %s", household.bodyMeasurementRequest)


    }
}