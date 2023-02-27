package com.kodego.app.inventory.app.orgino.restoup.Data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.database.SnapshotParser
import com.google.android.material.tabs.TabLayout.Tab
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kodego.app.inventory.app.orgino.restoup.auth
import com.kodego.app.inventory.app.orgino.restoup.db
import kotlinx.coroutines.tasks.await
import java.time.LocalDate


class UserDAO {

    var dbReference = Firebase.database("https://resto-up-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    var storageReference = Firebase.storage("gs://resto-up.appspot.com").reference
    var ownedRestaurantsList = mutableSetOf<String>()
    var restaurantMenuCategoryList = mutableSetOf<String>()
    lateinit var currentUser: User

    fun clear() {
        ownedRestaurantsList.clear()
        restaurantMenuCategoryList.clear()
    }
    fun addUser(uID:String, user: ConvertedUser) {
        user.uID = uID
        dbReference.child("User").child(uID).setValue(user)
    }

    suspend fun loadUserData(uID: String):User {
        val snapshot = dbReference.child("User").child(uID).get().await()

        return try {
                User(
                    snapshot.child("userName").value.toString(),
                    snapshot.child("passWord").value.toString(),
                    when (snapshot.child("userType").value.toString()) {
                        "ADMIN" -> UserTypes.ADMIN
                        else -> throw UserTypeException("Non-Admin")
                    },
                    snapshot.child("uID").value.toString(),
                    snapshot.child("adminUID").value.toString()
                )

        } catch (e:UserTypeException) {
            User(
                snapshot.child("firstName").value.toString(),
                snapshot.child("middleName").value.toString(),
                snapshot.child("lastName").value.toString(),
                try {
                    snapshot.child("birthDate").value.toString().let { LocalDate.parse(it) }
                } catch (e:Exception) { null },
                snapshot.child("email").value.toString(),
                snapshot.child("userName").value.toString(),
                snapshot.child("passWord").value.toString(),
                when (snapshot.child("userType").value.toString()) {
                    "CASHIER" -> UserTypes.CASHIER
                    "WAITER" -> UserTypes.WAITER
                    "KITCHENSTAFF" -> UserTypes.KITCHENSTAFF
                    "CUSTOMER" -> UserTypes.CUSTOMER
                    else -> throw Exception("Load Users Exception")
                },
                snapshot.child("uID").value.toString(),
                snapshot.child("adminUID").value.toString(),
                snapshot.child("assignedRestaurant").value.toString()
            )
        }

    }
    fun addRestaurant(name:String, address:String) {
        dbReference.child("Restaurant").child(auth.uid!!).child(name).setValue(Restaurant(name, address))
    }

    fun addEmployeeAccount(email:String, password:String, user:ConvertedUser) {
        val originalUser = auth.currentUser
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { taskResult ->
                val providedUID = taskResult.user!!.uid
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

    fun loadMenuCategoryList (restaurantName:String, adminUID:String) {
        dbReference.child("Restaurant").child(adminUID).child(restaurantName).child("MenuCategory").addChildEventListener(object:ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.child("MenuCategoryItem").value?.let { restaurantMenuCategoryList.add(it.toString()) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.child("MenuCategoryItem").value?.let { restaurantMenuCategoryList.remove(it.toString()) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun loadRestaurantList (adminUID:String) {
        dbReference.child("Restaurant").child(adminUID).addChildEventListener(object:ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.key?.let { ownedRestaurantsList.add(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.key?.let { ownedRestaurantsList.remove(it) }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun addMenuCategory(menuCategory:String, restaurantName:String, adminUID:String) {
        val newMenuCategory = mapOf("MenuCategoryItem" to menuCategory)
        dbReference.child("Restaurant").child(adminUID).child(restaurantName).child("MenuCategory").push().setValue(newMenuCategory)
    }

    fun addMenuItem(menuItem: MenuItem, adminUID: String) {
        val pushKey = dbReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").push().key!!
        val newMenuItem = ConvertedMenuItem(menuItem.restaurant, menuItem.category, menuItem.itemName, menuItem.itemPrice, pushKey)
        dbReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").child(pushKey).setValue(newMenuItem).addOnSuccessListener {
            menuItem.itemImages?.let {
                for ((counter, uri) in it.withIndex()) {
                    db.storageReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").child(pushKey).child("$counter").putFile(uri)
                }
            }
        }.addOnFailureListener { e:Exception ->
            val errorMessage = e.message
            val tag = "addMenuItem"
            Log.d(tag, errorMessage.toString())
        }
    }

    fun addTable(tableData:Table, adminUID: String) {
        val pushKey = dbReference.child("Restaurant").child(adminUID).child(tableData.restaurantName).child("Table").push().key!!
        dbReference.child("Restaurant").child(adminUID).child(tableData.restaurantName).child("Table").child(pushKey).setValue(tableData)
    }
}