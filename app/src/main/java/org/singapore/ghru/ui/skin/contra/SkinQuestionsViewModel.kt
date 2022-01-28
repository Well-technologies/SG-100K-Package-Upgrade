package org.singapore.ghru.ui.skin.contra

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.singapore.ghru.repository.AssertRepository
import org.singapore.ghru.repository.FundoscopyRepository
import org.singapore.ghru.repository.StationDevicesRepository
import javax.inject.Inject

class SkinQuestionsViewModel
@Inject constructor() : ViewModel() {

    val hadSkinLesson = MutableLiveData<String>()

    fun setHaveSkinLesson(item: String) {
        hadSkinLesson.value = item
    }

}
