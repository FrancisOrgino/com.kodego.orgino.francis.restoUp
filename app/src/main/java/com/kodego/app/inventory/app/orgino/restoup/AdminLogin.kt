//package com.kodego.app.inventory.app.orgino.restoup
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityAdminLoginBinding
//
//class AdminLogin : AppCompatActivity() {
//
//    lateinit var binding: ActivityAdminLoginBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnAdminLogin.setOnClickListener {
//            var adminUserName: String = binding.etAdminUserName.text.toString()
//            var adminPassword: String = binding.etAdminPassword.text.toString()
//
//            adminCredentials(adminUserName,adminPassword)
//        }
//    }
//
//        fun adminCredentials(adminUserName:String,adminPassword: String){
//
//            val correctAdminUserName: String = "admin"
//            val correctAdminPassword: String ="admin123"
//
//            if((correctAdminUserName == adminUserName) && (correctAdminPassword == adminPassword)){
//
//                val intentCashierLogin = Intent (this,AdminInterface::class.java)
//                startActivity(intentCashierLogin)
//                finish()
//                Toast.makeText(applicationContext,"ADMIN LOGGED IN", Toast.LENGTH_SHORT).show()
//
//            }else{
//                Toast.makeText(applicationContext,"INVALID USER", Toast.LENGTH_SHORT).show()
//
//            }
//
//        }
//
//
//}