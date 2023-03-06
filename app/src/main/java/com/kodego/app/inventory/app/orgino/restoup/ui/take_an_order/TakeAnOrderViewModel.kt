package com.kodego.app.inventory.app.orgino.restoup.ui.take_an_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TakeAnOrderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Take An Order Fragment"
    }
    val text: LiveData<String> = _text
}