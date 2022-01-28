package org.singapore.ghru

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle

class AppLifecycleHandler(private val logoutDelegate: LogoutDelegate):
    Application.ActivityLifecycleCallbacks {


    override fun onActivityPaused(p0: Activity?) {
        logoutDelegate.startTimer(p0)
    }

    override fun onActivityResumed(p0: Activity?) {
        logoutDelegate.stopTimer()
    }

    override fun onActivityStarted(p0: Activity?) {

    }

    override fun onActivityDestroyed(p0: Activity?) {

    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {

    }

    override fun onActivityStopped(p0: Activity?) {

    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {

    }

}