package org.singapore.ghru.ui.treadmill.contraindications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TreadmillContraViewModel : ViewModel() {

    val bloodPressure = MutableLiveData<Boolean>()
    val medicalConditions = MutableLiveData<Boolean>()
    val chestPain = MutableLiveData<Boolean>()
    val diabetes = MutableLiveData<Boolean>()
    val oxygen = MutableLiveData<Boolean>()

    fun setBloodPressure(item: Boolean) {
        bloodPressure.value = item
    }

    fun setMedicalConditions(item: Boolean) {
        medicalConditions.value = item
    }

    fun setChestPain(item: Boolean) {
        chestPain.value = item
    }

    fun setDiabetes(item: Boolean) {
        diabetes.value = item
    }

    fun setOxygen(item: Boolean) {
        oxygen.value = item
    }

}
