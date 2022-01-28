package org.singapore.ghru.ui.foodquestionnaire.contraindication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class FoodFrequencyQuestionnaireViewModel
@Inject constructor() : ViewModel() {

    val haveCommunicate = MutableLiveData<Boolean>()

    fun setHaveCommunicate(item: Boolean) {
        haveCommunicate.value = item
    }

}
