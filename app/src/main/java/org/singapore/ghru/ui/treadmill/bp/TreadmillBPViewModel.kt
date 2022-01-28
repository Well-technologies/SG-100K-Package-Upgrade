package org.singapore.ghru.ui.treadmill.bp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.singapore.ghru.vo.Arm
import org.singapore.ghru.vo.BloodPressure
import org.singapore.ghru.vo.BodyMeasurement
import org.singapore.ghru.vo.TreadmillBP
import javax.inject.Inject


class TreadmillBPViewModel
@Inject constructor() : ViewModel() {
    var isValidSystolicBp: Boolean = false
    var isValidDiastolicBp: Boolean = false
    var isValidPuls: Boolean = false
}
