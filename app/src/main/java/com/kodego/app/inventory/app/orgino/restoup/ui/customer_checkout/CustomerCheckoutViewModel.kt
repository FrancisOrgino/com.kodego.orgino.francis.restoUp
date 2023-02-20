package com.kodego.app.inventory.app.orgino.restoup.ui.customer_checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomerCheckoutViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Customer Checkout Fragment"
    }
    val text: LiveData<String> = _text
}