package org.singapore.ghru.ui.bodymeasurements.bp.questionnaire

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BPQuestionnaireViewModel : ViewModel() {

    val haveArteriovenous = MutableLiveData<String>()
    val hadSurgery = MutableLiveData<String>()
    val lymphRemoved = MutableLiveData<String>()
    val haveTrauma = MutableLiveData<String>()

    fun setHaveArteriovenous(item: String) {
        haveArteriovenous.value = item
    }

    fun setHadSurgery(item: String) {
        hadSurgery.value = item
    }

    fun setLymphRemoved(item: String) {
        lymphRemoved.value = item
    }

    fun setHaveTrauma(item: String) {
        haveTrauma.value = item
    }

}
