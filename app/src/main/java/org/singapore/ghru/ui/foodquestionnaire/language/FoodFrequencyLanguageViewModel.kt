package org.singapore.ghru.ui.foodquestionnaire.language

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class FoodFrequencyLanguageViewModel
@Inject constructor() : ViewModel() {

    val haveLanguage = MutableLiveData<Boolean>()

    fun setHaveLanguage(item: Boolean) {
        haveLanguage.value = item
    }

}
