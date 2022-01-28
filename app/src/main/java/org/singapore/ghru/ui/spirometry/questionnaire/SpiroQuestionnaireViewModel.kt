package org.singapore.ghru.ui.spirometry.questionnaire

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpiroQuestionnaireViewModel : ViewModel() {

    val haveRetina = MutableLiveData<Boolean>()
    val haveEyeSurgery = MutableLiveData<Boolean>()
    val haveOtherSurgery = MutableLiveData<Boolean>()
    val haveMyocardialInfarction = MutableLiveData<Boolean>()
    val haveSufferedStroke = MutableLiveData<Boolean>()
    val haveChestInfection = MutableLiveData<Boolean>()
    val haveTuberculosis = MutableLiveData<Boolean>()
    val havePneumothorax = MutableLiveData<Boolean>()

    fun setHaveRetina(item: Boolean) {
        haveRetina.value = item
    }

    fun setHaveEyeSurgery(item: Boolean) {
        haveEyeSurgery.value = item
    }

    fun setHaveOtherSurgery(item: Boolean) {
        haveOtherSurgery.value = item
    }

    fun setHaveMycardial(item: Boolean) {
        haveMyocardialInfarction.value = item
    }

    fun setHaveStroke(item: Boolean) {
        haveSufferedStroke.value = item
    }

    fun setHaveChestInfection(item: Boolean) {
        haveChestInfection.value = item
    }

    fun setHaveTuberculosis(item: Boolean) {
        haveTuberculosis.value = item
    }

    fun setHavePneumothorax(item: Boolean) {
        havePneumothorax.value = item
    }

}
