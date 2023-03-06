package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.kodego.app.inventory.app.orgino.restoup.Data.User
//<<<<<<< HEAD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//class SplashScreen : AppCompatActivity() {

//=======
//import androidx.core.view.WindowCompat
//import androidx.lifecycle.lifecycleScope
//import com.google.firebase.auth.FirebaseUser
//import com.kodego.app.inventory.app.orgino.restoup.Data.User
//import kotlinx.coroutines.*

class SplashScreen : AppCompatActivity() {
    lateinit var registeredUsers:MutableList<User>
//>>>>>>> 63f7135d7677646c1c16b0ac29c9e52320ea19af
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//<<<<<<< HEAD
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(2000L)
//            startActivity(Intent(this@SplashScreen, LoginOptionsActivity::class.java))
//            finish()
//        }
//=======
        if (auth.currentUser!=null) {
            lifecycleScope.launch {
                db.loadRestaurantList(auth.currentUser!!.uid)
                startActivityByUserType(auth.currentUser!!)
                finish()
            }
        } else {
            lifecycleScope.launch {
                startActivity(Intent(this@SplashScreen, LoginOptionsActivity::class.java))
                finish()
            }
        }
    }
    private suspend fun startActivityByUserType(user: FirebaseUser) {
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        WindowCompat.setDecorFitsSystemWindows(window, false)
//>>>>>>> 63f7135d7677646c1c16b0ac29c9e52320ea19af
    }
}