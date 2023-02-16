package com.kodego.app.inventory.app.orgino.restoup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class AdminInterface : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_interface)

        Toast.makeText(applicationContext, "AdminInterfaceActivityStarted", Toast.LENGTH_LONG).show()
    }
}