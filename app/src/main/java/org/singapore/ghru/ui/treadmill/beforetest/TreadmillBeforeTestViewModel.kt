package org.singapore.ghru.ui.treadmill.beforetest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class TreadmillBeforeTestViewModel
@Inject constructor() : ViewModel() {

    val armPlacement = MutableLiveData<String>()

    fun setArmPlacement(item: String) {
        armPlacement.value = item
    }
}
