package org.singapore.ghru.ui.gripstrangth.contraindication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class GripStrengthQuestionnaireViewModel
@Inject constructor() : ViewModel() {

    val haveInjury = MutableLiveData<String>()
    val haveSevere = MutableLiveData<String>()
    val haveDominant = MutableLiveData<String>()

    fun setHaveInjury(item: String) {
        haveInjury.value = item
    }

    fun setHaveSevere(item: String) {
        haveSevere.value = item
    }

    fun setDominant(item: String) {
        haveDominant.value = item
    }

}
