package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.kodego.app.inventory.app.orgino.restoup.Data.User
import com.kodego.app.inventory.app.orgino.restoup.Data.UserAdapter
import com.kodego.app.inventory.app.orgino.restoup.Data.UserDAO
import com.kodego.app.inventory.app.orgino.restoup.Data.UserTypes
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityLoginOptionsBinding

class LoginOptionsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginOptionsBinding
    private lateinit var adapter: UserAdapter
    lateinit var auth: FirebaseAuth
    private val db = UserDAO()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //auth process helpers
        val snapshotParser ={ snapshot:DataSnapshot ->
            User(
                snapshot.child("userName").value.toString(),
                snapshot.child("passWord").value.toString(),
                when (snapshot.child("userType").value.toString()) {
                    "ADMIN" -> UserTypes.ADMIN
                    "CASHIER" -> UserTypes.CASHIER
                    "WAITER" -> UserTypes.WAITER
                    "KITCHENSTAFF" -> UserTypes.KITCHENSTAFF
                    "CUSTOMER" -> UserTypes.CUSTOMER
                    else -> UserTypes.CUSTOMER
                },
                snapshot.child("uID").value.toString()
            )
        }
        val options:FirebaseRecyclerOptions<User> = FirebaseRecyclerOptions.Builder<User>().setQuery(db.queryUserType(), snapshotParser).build()
        adapter = UserAdapter(options)
        binding.rvHelper.layoutManager = LinearLayoutManager(applicationContext)
        binding.rvHelper.adapter = adapter

        login("test@email.com", "password")

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    private fun login(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    startActivityByUserType(user)
                }
            } else {
                Toast.makeText(applicationContext, "no user found", Toast.LENGTH_LONG).show()
            }
        }
        auth
    }

    private fun startActivityByUserType(user:FirebaseUser) {
        for (registeredUser in adapter.userList) {
            if (user!!.uid == registeredUser.uID && registeredUser.userType.toString() == "ADMIN") {
                Toast.makeText(applicationContext,"starting admin activity", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, AdminInterface::class.java))
                finish()
            }
        }
    }
}