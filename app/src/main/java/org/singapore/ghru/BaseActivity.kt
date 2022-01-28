package org.singapore.ghru

import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onUserInteraction() {
        super.onUserInteraction()

        var app = getApp()
        if (app != null) {
            if(app.timerStarted) {
                app.stopTimer()
            }
            else {
                app.startTimer(this)
            }
        }
    }

    open fun getApp(): NghruApp? {
        return this.application as NghruApp
    }

}