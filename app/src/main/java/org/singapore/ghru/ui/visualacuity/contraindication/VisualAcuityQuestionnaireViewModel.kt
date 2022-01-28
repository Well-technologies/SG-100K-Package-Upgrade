package org.singapore.ghru.ui.visualacuity.contraindication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class VisualAcuityQuestionnaireViewModel
@Inject constructor() : ViewModel() {

    val haveEyeOccluded = MutableLiveData<Boolean>()

    fun setEyeOccluded(item: Boolean) {
        haveEyeOccluded.value = item
    }

}
