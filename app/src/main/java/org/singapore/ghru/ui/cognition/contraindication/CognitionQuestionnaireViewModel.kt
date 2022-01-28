package org.singapore.ghru.ui.cognition.contraindication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class CognitionQuestionnaireViewModel
@Inject constructor() : ViewModel() {

    val haveAbleToClick = MutableLiveData<Boolean>()

    fun setHaveAbleToClick(item: Boolean) {
        haveAbleToClick.value = item
    }

}
