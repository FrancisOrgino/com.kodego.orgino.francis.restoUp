package com.kodego.app.inventory.app.orgino.restoup

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kodego.app.inventory.app.orgino.restoup.Data.*
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityLoginOptionsBinding
import com.kodego.app.inventory.app.orgino.restoup.databinding.SignUpDialogBinding
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
            showSignUpDialog()
        }
    }

    private fun login(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                lifecycleScope.launch {
                    Toast.makeText(applicationContext, "Success!", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    startActivityByUserType(user!!)
                }
            } else {
                Toast.makeText(applicationContext, "error on login", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun startActivityByUserType(user:FirebaseUser) {
        val registeredUser = db.loadUserData(user.uid)
        if (user.uid == registeredUser.uid && registeredUser.userType.toString() == "ADMIN") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        } else if (user.uid == registeredUser.uid && registeredUser.userType.toString() == "CASHIER") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        } else if (user.uid == registeredUser.uid && registeredUser.userType.toString() == "WAITER") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        } else if (user.uid == registeredUser.uid && registeredUser.userType.toString() == "KITCHENSTAFF") {
            val intent = Intent(this, MainInterface::class.java)
//                intent.putExtra("Logged User", registeredUser)
            db.currentUser = registeredUser
            startActivity(intent)
            finish()
        }
    }


    private fun showSignUpDialog() {
        val dialog = Dialog(this)
        val binding:SignUpDialogBinding = SignUpDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        binding.btnSignUpCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnSignUpConfirm.setOnClickListener {
            val email = binding.etSignUpEmail.text!!.toString()
            val password = binding.etSignUpPassword!!.text.toString()
            lifecycleScope.launch {
                try {
                    signUp(ConvertedUser(email, password, UserTypes.ADMIN))
                    dialog.dismiss()
                } catch (e:Exception) {
                    Toast.makeText(applicationContext, "sign-up error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun signUp(newAdminUser:ConvertedUser) {
        auth.createUserWithEmailAndPassword(newAdminUser.userName, newAdminUser.passWord).addOnSuccessListener {
            authResult ->
            authResult.user?.let {
                val providedUID = it.uid
                newAdminUser.adminUID = providedUID
                newAdminUser.uid = providedUID
                newAdminUser.email = newAdminUser.userName

                db.addUser(providedUID, newAdminUser)
            }
            login(newAdminUser.email!!, newAdminUser.passWord)
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Signup error occurred. Check your internet connectivity or try again later.", Toast.LENGTH_LONG).show()
        }
    }
}