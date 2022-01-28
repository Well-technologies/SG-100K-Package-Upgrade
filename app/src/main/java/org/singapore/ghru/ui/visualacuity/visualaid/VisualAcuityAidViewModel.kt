package org.singapore.ghru.ui.visualacuity.visualaid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class VisualAcuityAidViewModel
@Inject constructor() : ViewModel() {

    val haveVisualAid = MutableLiveData<Boolean>()

    fun setVisualAid(item: Boolean) {
        haveVisualAid.value = item
    }

//    val haveImageExported = MutableLiveData<Boolean>()
//
//    fun setImageExported(item: Boolean) {
//        haveImageExported.value = item
//    }

}
