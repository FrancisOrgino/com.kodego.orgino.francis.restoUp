package com.kodego.app.inventory.app.orgino.restoup.Data

import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kodego.app.inventory.app.orgino.restoup.auth
import com.kodego.app.inventory.app.orgino.restoup.db
import kotlinx.coroutines.tasks.await
import java.net.URL
import java.time.LocalDate


class UserDAO {

    init {
        try {
            auth.currentUser?.let { loadRestaurantList(it.uid) }
        } catch (e:Exception) {
            //
        }
    }
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
                    snapshot.child("uid").value.toString(),
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
                snapshot.child("uid").value.toString(),
                snapshot.child("adminUID").value.toString(),
                snapshot.child("assignedRestaurant").value.toString()
            )
        }

    }
    fun addRestaurant(name:String, address:String) {
        dbReference.child("Restaurant").child(auth.uid!!).child(name).setValue(Restaurant(name, address))
    }

    fun addEmployeeAccount(email:String, password:String, user:ConvertedUser) {
        //add new employee account without logging out current admin user
        //does this by initializing '(an emulated??) secondary app' that then gets deleted after it creates a new employee account
        val authworker = FirebaseApp.initializeApp(FirebaseApp.getInstance().applicationContext,FirebaseApp.getInstance().options, "auth-worker")
        val _authcreator = Firebase.auth(authworker)
        _authcreator.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { taskResult ->
                val providedUID = taskResult.user!!.uid
                user.uid = providedUID
                addUser(providedUID, user)
                _authcreator.signOut()
                authworker.delete()
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
        dbReference.child("Restaurant").child(adminUID).addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataKey in snapshot.children) {
                    ownedRestaurantsList.add(dataKey.key.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Do Nothing
            }
        })
    }

    fun addMenuCategory(menuCategory:String, restaurantName:String, adminUID:String) {
        val newMenuCategory = mapOf("MenuCategoryItem" to menuCategory)
        dbReference.child("Restaurant").child(adminUID).child(restaurantName).child("MenuCategory").push().setValue(newMenuCategory)
    }

    fun addMenuItem(menuItem: MenuItem, adminUID: String) {
        val pushKey = dbReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").push().key!!
        val newMenuItem = ConvertedMenuItem(menuItem.restaurant, menuItem.category, menuItem.itemName, menuItem.itemPrice, pushKey, adminUID)
        dbReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").child(pushKey).setValue(newMenuItem).addOnSuccessListener {
            menuItem.itemImages?.let {
                for ((counter, uri) in it.withIndex()) {
                    db.storageReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").child(pushKey).child("$counter").putFile(uri).continueWith { db.storageReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").child(pushKey).child("$counter").downloadUrl.continueWith { task -> dbReference.child("Restaurant").child(adminUID).child(menuItem.restaurant).child("Menu").child(pushKey).child("itemImageUrls").push().setValue(task.result.toString()) } }
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