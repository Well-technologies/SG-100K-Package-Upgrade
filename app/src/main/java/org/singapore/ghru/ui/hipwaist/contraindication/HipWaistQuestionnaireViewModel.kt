package org.singapore.ghru.ui.hipwaist.contraindication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class HipWaistQuestionnaireViewModel
@Inject constructor() : ViewModel() {

    val haveColostomy = MutableLiveData<Boolean>()
    val haveUnaided = MutableLiveData<Boolean>()

    fun setHaveColostomy(item: Boolean) {
        haveColostomy.value = item
    }

    fun setHaveUnaided(item: Boolean) {
        haveUnaided.value = item
    }

}
