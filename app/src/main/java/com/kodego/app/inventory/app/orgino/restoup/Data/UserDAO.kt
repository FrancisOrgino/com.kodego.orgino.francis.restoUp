package com.kodego.app.inventory.app.orgino.restoup.Data

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kodego.app.inventory.app.orgino.restoup.auth
import kotlinx.coroutines.tasks.await


class UserDAO {
    var dbReference = Firebase.database("https://resto-up-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    fun addUser(uID:String, user: ConvertedUser) {
        user.uID = uID
        dbReference.child("User").child(uID).setValue(user)
    }

    fun queryUserType(): Query {
        return dbReference.child("User").orderByKey()
    }

    suspend fun loadUsers(context:Context):MutableList<User> {
        var registeredUser = mutableListOf<User>()
        val dbSnapshot = queryUserType().get().await()
        for (snapshot in dbSnapshot.children) {
            try {
                registeredUser.add(
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
                )
            } catch (e:Exception) {
                Toast.makeText(context, "load users error", Toast.LENGTH_LONG).show()
            }
        }
        return  registeredUser
    }
    fun addRestaurant(name:String, address:String) {
        dbReference.child("Restaurant").child(auth.uid!!).setValue(Restaurant(name, address))
    }

    fun addEmployeeAccount(email:String, password:String, user:ConvertedUser) {
        val originalUser = auth.currentUser
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { taskResult ->
                val providedUID = taskResult.result.user!!.uid
                user.uID = providedUID
                addUser(providedUID, user)
                auth.updateCurrentUser(originalUser!!)
            }
            .addOnFailureListener { e:Exception ->
                val errorMessage = e.message
                val tag = "addEmployeeAccount"
                Log.d(tag, errorMessage.toString())
            }
    }
}