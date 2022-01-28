package org.singapore.ghru.ui.samplecollection.questions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SampleQuestionsViewModel
@Inject constructor(): ViewModel() {

    val hadDermatitisEyes = MutableLiveData<String>()
    val hadDermatitisNeck = MutableLiveData<String>()
    val hadDermatitisElbow = MutableLiveData<String>()
    val hadDermatitisBehindKnees = MutableLiveData<String>()
    val hadDermatitisFrontKnees = MutableLiveData<String>()

    fun setHadDermatitisEyes(item: String) {
        hadDermatitisEyes.value = item
    }

    fun setHadDermatitisNeck(item: String) {
        hadDermatitisNeck.value = item
    }

    fun setHadDermatitisElbow(item: String) {
        hadDermatitisElbow.value = item
    }

    fun setHadDermatitisBehindKnees(item: String) {
        hadDermatitisBehindKnees.value = item
    }

    fun setHadDermatitisFrontKnees(item: String) {
        hadDermatitisFrontKnees.value = item
    }

}
