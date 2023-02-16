package com.kodego.app.inventory.app.orgino.restoup.Data

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FirebaseHandler:Application() {
    override fun onCreate() {
        super.onCreate()
        //Enable Offline Data Persistence
        FirebaseDatabase.getInstance("https://resto-up-default-rtdb.asia-southeast1.firebasedatabase.app/").setPersistenceEnabled(true)

        //Keep Data Fresh
        FirebaseDatabase.getInstance("https://resto-up-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().keepSynced(true)
    }
}