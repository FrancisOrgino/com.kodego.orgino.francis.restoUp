package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.kodego.app.inventory.app.orgino.restoup.Model.CashierInterface
import com.kodego.app.inventory.app.orgino.restoup.Model.WaiterInterface
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityLoginOptionsBinding

class LoginOptionsActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdminLoginInterface.setOnClickListener {
            val intentAdminLogin = Intent(this,AdminLogin::class.java)
            startActivity(intentAdminLogin)
        }

        binding.btnLogin.setOnClickListener {
            var mainUserName: String = binding.etUserLoginOptions.text.toString()
            var mainPassword: String = binding.etUserPassword.text.toString()


            loginCredentials(mainUserName, mainPassword)
        }

    }

    fun loginCredentials(mainUsername: String, mainPassword: String) {

        var correctUserName: String = "user"
        var correctPassword: String = "user123"

        if ((correctUserName == mainUsername) && (correctPassword == mainPassword)) {

            val intentLogin = Intent(this, CashierInterface::class.java)
            startActivity(intentLogin)
            finish()
            Toast.makeText(applicationContext, "LOGGED IN", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(applicationContext, "INVALID USER", Toast.LENGTH_SHORT).show()

        }

    }


}

