package org.singapore.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.db.TreadmillRequestDao
import timber.log.Timber

class TreadmillRequestLifecycleObserver (var treadmillRequestDao: TreadmillRequestDao) : LifecycleObserver {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            TreadmillRequestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "household SyncCommentLifecycleObserver ${result.treadmillRequest}")
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

    private fun handleSyncResponse(response: TreadmillRequestResponce) {
        if (response.eventType === SyncResponseEventType.SUCCESS) {
            onSyncCommentSuccess(response)
        } else {
            onSyncCommentFailed(response)
        }
    }

    private fun onSyncCommentSuccess(treadmillRequestResponce: TreadmillRequestResponce) {
        treadmillRequestResponce?.treadmillRequest.syncPending = false
        val udatedId=treadmillRequestDao.update(treadmillRequestResponce?.treadmillRequest.screeningId)
    }

    private fun onSyncCommentFailed(treadmillRequestResponce: TreadmillRequestResponce) {
        Timber.d("received sync comment failed event for comment %s", treadmillRequestResponce)
    }

}