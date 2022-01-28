package org.singapore.ghru.ui.dxa.noheight

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class NoHeightDialogViewModel
@Inject constructor() : ViewModel() {

    var codecheckMsg: MutableLiveData<String> = MutableLiveData<String>().apply { }
}