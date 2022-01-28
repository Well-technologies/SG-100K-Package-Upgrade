package org.singapore.ghru.ui.vicorder.contra

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.AssertRepository
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.repository.StationDevicesRepository
import javax.inject.Inject

class VicorderQuestionsViewModel
@Inject constructor() : ViewModel() {

    val hadFistula = MutableLiveData<String>()
    val haveMastectomy = MutableLiveData<String>()
    val hadLymph = MutableLiveData<String>()
    val haveTrauma = MutableLiveData<String>()

    fun setHadFistula(item: String) {
        hadFistula.value = item
    }

    fun setHaveMastectomy(item: String) {
        haveMastectomy.value = item
    }

    fun setHaveLymph(item: String) {
        hadLymph.value = item
    }

    fun setHaveTrauma(item: String) {
        haveTrauma.value = item
    }

}
