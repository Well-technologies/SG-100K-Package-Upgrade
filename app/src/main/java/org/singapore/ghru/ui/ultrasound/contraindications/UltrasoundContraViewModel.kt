package org.singapore.ghru.ui.ultrasound.contraindications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UltrasoundContraViewModel : ViewModel() {

    val hadSurgery = MutableLiveData<Boolean>()

    fun setHadSurgery(item: Boolean) {
        hadSurgery.value = item
    }
}
