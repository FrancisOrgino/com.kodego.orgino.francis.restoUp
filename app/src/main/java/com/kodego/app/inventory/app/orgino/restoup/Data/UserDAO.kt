package com.kodego.app.inventory.app.orgino.restoup.Data

import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class UserDAO {
    var dbReference = Firebase.database("https://resto-up-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    fun addUser(uID:String, user: User) {
        user.uID = uID
        dbReference.child("User").child(uID).setValue(user)
    }

    fun queryUserType(): Query {
        return dbReference.child("User").orderByKey()
    }
}