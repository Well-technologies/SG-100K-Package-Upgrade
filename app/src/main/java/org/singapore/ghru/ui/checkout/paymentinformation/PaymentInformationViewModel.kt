package org.singapore.ghru.ui.checkout.paymentinformation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class PaymentInformationViewModel
@Inject constructor() : ViewModel() {

    val haveHellios = MutableLiveData<Boolean>()

    fun setHaveHellios(item: Boolean) {
        haveHellios.value = item
    }

    val haveAbc = MutableLiveData<Boolean>()

    fun setHaveAbc(item: Boolean) {
        haveAbc.value = item
    }

    val haveXyz = MutableLiveData<Boolean>()

    fun setHaveXyz(item: Boolean) {
        haveXyz.value = item
    }

    val have123 = MutableLiveData<Boolean>()

    fun setHave123(item: Boolean) {
        have123.value = item
    }

}
