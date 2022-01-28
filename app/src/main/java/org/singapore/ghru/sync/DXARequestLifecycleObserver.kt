package org.singapore.ghru.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import org.singapore.ghru.db.DXARequestDao
import org.singapore.ghru.db.FundoscopyRequestDao
import timber.log.Timber

class DXARequestLifecycleObserver (var dxaRequestDao: DXARequestDao) : LifecycleObserver {

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Timber.d("onResume lifecycle event.")
        disposables.add(
            DXARequestRxBus.getInstance().toObservable()
                .subscribe({ result ->
                    Log.d("Result", "household SyncCommentLifecycleObserver ${result.dxaRequest}")
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

    private fun handleSyncResponse(response: DXARequestResponce) {
        if (response.eventType === SyncResponseEventType.SUCCESS) {
            onSyncCommentSuccess(response)
        } else {
            onSyncCommentFailed(response)
        }
    }

    private fun onSyncCommentSuccess(dxaRequestResponce: DXARequestResponce) {
        dxaRequestResponce?.dxaRequest.syncPending = false
        val udatedId=dxaRequestDao.update(dxaRequestResponce?.dxaRequest.screeningId)
    }

    private fun onSyncCommentFailed(dxaRequestResponce: DXARequestResponce) {
        Timber.d("received sync comment failed event for comment %s", dxaRequestResponce)
    }

}