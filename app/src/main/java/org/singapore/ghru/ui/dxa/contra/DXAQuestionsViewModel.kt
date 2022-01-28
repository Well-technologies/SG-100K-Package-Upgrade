package org.singapore.ghru.ui.dxa.contra

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class DXAQuestionsViewModel
@Inject constructor() : ViewModel() {

    val hadXray = MutableLiveData<Boolean>()

    fun setHadXray(item: Boolean) {
        hadXray.value = item
    }
}
