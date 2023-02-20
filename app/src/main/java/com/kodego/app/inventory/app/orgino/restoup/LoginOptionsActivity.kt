package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kodego.app.inventory.app.orgino.restoup.Data.*
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityLoginOptionsBinding
import kotlinx.coroutines.launch

var auth: FirebaseAuth = FirebaseAuth.getInstance()
var db = UserDAO()
class LoginOptionsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginOptionsBinding
    lateinit var registeredUsers:MutableList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loggedUser = auth.currentUser

        lifecycleScope.launch {
            registeredUsers = db.loadUsers(applicationContext)
            if (loggedUser!=null) {
                startActivityByUserType(loggedUser)
            }
        }

        binding.btnSignIn.setOnClickListener {
            //"test@email.com", "password"
            val providedEmail = binding.editTextTextEmailAddress.text!!.toString()
            val providedPassword = binding.editTextTextPassword.text!!.toString()
            try {
                login(providedEmail, providedPassword)
            } catch (e:java.lang.Exception) {
                Toast.makeText(applicationContext, "An error occurred while trying to log in. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun login(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    startActivityByUserType(user)
                }
            } else {
                Toast.makeText(applicationContext, "error on login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startActivityByUserType(user:FirebaseUser) {
        for (registeredUser in registeredUsers) {
            if (user!!.uid == registeredUser.uID && registeredUser.userType.toString() == "ADMIN") {
                startActivity(Intent(this, MainInterface::class.java))
                finish()
            }
        }
    }
}