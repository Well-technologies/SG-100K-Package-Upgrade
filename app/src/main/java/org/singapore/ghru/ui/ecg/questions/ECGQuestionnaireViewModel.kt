package org.singapore.ghru.ui.ecg.questions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ECGQuestionnaireViewModel : ViewModel() {

    val haveArteriovenous = MutableLiveData<Boolean>()
    val hadSurgery = MutableLiveData<String>()
    val lymphRemoved = MutableLiveData<Boolean>()
    val haveTrauma = MutableLiveData<String>()
    val haveNeckInjury = MutableLiveData<Boolean>()
    val amputated = MutableLiveData<Boolean>()

    fun setHaveArteriovenous(item: Boolean) {
        haveArteriovenous.value = item
    }

    fun setHadSurgery(item: String) {
        hadSurgery.value = item
    }

    fun setLymphRemoved(item: Boolean) {
        lymphRemoved.value = item
    }

    fun setHaveTrauma(item: String) {
        haveTrauma.value = item
    }

    fun setHaveNeckInjury(item: Boolean) {
        haveNeckInjury.value = item
    }

    fun setAmputated(item: Boolean) {
        amputated.value = item
    }

}
