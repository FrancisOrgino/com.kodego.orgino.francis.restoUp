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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var auth: FirebaseAuth = FirebaseAuth.getInstance()
var db = UserDAO()

class LoginOptionsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loggedUser = auth.currentUser

        lifecycleScope.launch {
            if (loggedUser!=null) {
                db.loadRestaurantList(auth.currentUser!!.uid)
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

        binding.btnSignUp.setOnClickListener {

        }
    }

    private fun login(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Success!", Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                lifecycleScope.launch {
                    startActivityByUserType(user!!)
                }
            } else {
                Toast.makeText(applicationContext, "error on login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun startActivityByUserType(user:FirebaseUser) {
        val registeredUser = db.loadUserData(user.uid)
        if (user.uid == registeredUser.uID && registeredUser.userType.toString() == "ADMIN") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        } else if (user.uid == registeredUser.uID && registeredUser.userType.toString() == "CASHIER") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        } else if (user.uid == registeredUser.uID && registeredUser.userType.toString() == "WAITER") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        } else if (user.uid == registeredUser.uID && registeredUser.userType.toString() == "KITCHENSTAFF") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        }
    }

    private fun signUp() {

    }
}