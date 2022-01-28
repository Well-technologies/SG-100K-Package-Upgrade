package org.singapore.ghru.ui.fundoscopy.questions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FundoscopyQuestionsViewModel : ViewModel() {

    val hadSurgery = MutableLiveData<String>()
    val haveEyePain = MutableLiveData<String>()
    val hadEyeRedness = MutableLiveData<String>()
//    val haveBlurring = MutableLiveData<String>()
//    val haveDouble = MutableLiveData<String>()

    fun setHadSurgery(item: String) {
        hadSurgery.value = item
    }

    fun setHaveEyaPain(item: String) {
        haveEyePain.value = item
    }

    fun setHaveEyeRedness(item: String) {
        hadEyeRedness.value = item
    }

//    fun setHaveBlurring(item: String) {
//        haveBlurring.value = item
//    }
//
//    fun setHaveDouble(item: String) {
//        haveDouble.value = item
//    }

}
