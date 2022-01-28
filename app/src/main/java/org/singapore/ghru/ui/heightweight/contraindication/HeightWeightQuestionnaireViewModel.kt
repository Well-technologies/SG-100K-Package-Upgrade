package org.singapore.ghru.ui.heightweight.contraindication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class HeightWeightQuestionnaireViewModel
@Inject constructor() : ViewModel() {

    val haveUnaided = MutableLiveData<Boolean>()

    fun setHaveUnaided(item: Boolean) {
        haveUnaided.value = item
    }

}
