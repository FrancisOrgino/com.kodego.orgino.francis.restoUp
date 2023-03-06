package com.kodego.app.inventory.app.orgino.restoup.ui.order_queue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderQueueViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Order Queue Fragment"
    }
    val text: LiveData<String> = _text
}