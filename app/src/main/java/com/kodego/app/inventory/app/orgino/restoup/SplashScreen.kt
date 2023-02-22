package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.kodego.app.inventory.app.orgino.restoup.Data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    lateinit var registeredUsers:MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (auth.currentUser!=null) {
            lifecycleScope.launch {
                registeredUsers = db.loadUsers(applicationContext)
                delay(1000L)
                startActivityByUserType(auth.currentUser!!)
                finish()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(1500L)
                startActivity(Intent(this@SplashScreen, LoginOptionsActivity::class.java))
                finish()
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}