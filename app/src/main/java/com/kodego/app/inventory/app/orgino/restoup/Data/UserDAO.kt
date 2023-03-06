package com.kodego.app.inventory.app.orgino.restoup.Data

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.net.toUri
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
import okhttp3.internal.wait
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
    var ownedRestaurantsList = mutableStateOf(mutableListOf<String>())
    var restaurantMenuCategoryList = mutableStateOf(mutableListOf<String>())
    var restaurantMenuList = mutableStateOf(mutableListOf<MenuItem>())
    var restaurantTableDataList = mutableStateOf(mutableListOf<Table>())
    var restaurantOrderList = mutableStateOf(mutableListOf<Order>())
    lateinit var currentUser: User
    lateinit var serverDate: LocalDate

    fun clear() {
        ownedRestaurantsList.value.clear()
        restaurantMenuCategoryList.value.clear()
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
        dbReference.child("Restaurant").child(adminUID).child(restaurantName).child("MenuCategory").addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var _restaurantMenuCategoryList = mutableListOf<String>()
                for (data in snapshot.children) {
                    _restaurantMenuCategoryList.add(data.child("MenuCategoryItem").value.toString())
                }
                restaurantMenuCategoryList.value = _restaurantMenuCategoryList
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun loadRestaurantList (adminUID:String) {
        dbReference.child("Restaurant").child(adminUID).addValueEventListener(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var _ownedRestaurantsList = mutableListOf<String>()
                for (dataKey in snapshot.children) {
                    _ownedRestaurantsList.add(dataKey.key.toString())
                }
                ownedRestaurantsList.value = _ownedRestaurantsList
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
        val pushKey = dbReference.child("Restaurant").child(adminUID).child(tableData.restaurant).child("Table").push().key!!
        dbReference.child("Restaurant").child(adminUID).child(tableData.restaurant).child("Table").child(pushKey).setValue(tableData)
    }

    fun loadMenuItems(user:User) {
        dbReference.child("Restaurant").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).child("Menu").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var menulist = mutableListOf<MenuItem>()
                for (data in snapshot.children) {
                        var menuItem = MenuItem(
                            data.child("adminID").value.toString(),
                            data.child("category").value.toString(),
                            data.child("itemName").value.toString(),
                            data.child("itemPrice").value.toString().toDouble(),
                            data.child("id").value.toString(),
                            data.child("adminID").value.toString(),
                            try{
                                mutableListOf(data.child("itemImageUrls").children.first().value.toString().toUri())
                            } catch (e:Exception) {
                                mutableListOf()
                            }
                        )
                        menulist.add(menuItem)
                }
                restaurantMenuList.value = menulist
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun loadTableData(user: User) {
        dbReference.child("Restaurant").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).child("Table").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var _restaurantTableDataList = mutableListOf<Table>()
                for (tableData in snapshot.children) {
                    tableData?.let {
                        val table = Table(it.child("tableName").value.toString(), it.child("tableCapacity").value.toString().toInt(), it.child("restaurant").value.toString())
                        _restaurantTableDataList.add(table)
                    }
                }
                restaurantTableDataList.value = _restaurantTableDataList
            }

            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
    }

    fun addOrder(newOrder:Order, user: User) {
        val pushKey = dbReference.child("Order").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).push().key
        val convertedNewOrder = ConvertedOrder(
            pushKey!!,
            newOrder.table,
            newOrder.customerID,
            newOrder.orderStatus,
            newOrder.restaurant,
            newOrder.employeeID,
            newOrder.userType,
            newOrder.adminID
        )

        dbReference.child("Order").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).child(pushKey!!).setValue( convertedNewOrder )
        newOrder.orderItems.forEach {
            val orderPushkey = dbReference.child("Order").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).child(pushKey!!).child("orderItems").push().key
            val orderItem = ConvertedMenuItem(it.key.restaurant, it.key.category, it.key.itemName, it.key.itemPrice, it.key.id!!, it.key.adminID!!, it.value, orderPushkey!!)
            val orderItemImages = it.key.itemImages
            dbReference.child("Order").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).child(pushKey!!).child("orderItems").child(orderPushkey!!).setValue(orderItem).addOnSuccessListener {
                if (!orderItemImages.isNullOrEmpty()) {
                    for (uri in orderItemImages) {
                        dbReference.child("Order").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).child(pushKey!!).child("orderItems").child(orderPushkey!!).child("itemImageUrls").push().setValue(uri.toString())
                    }
                }
            }
        }
    }

    fun loadOrderData(user: User) {
        dbReference.child("Order").child(user.adminUID.toString()).child(user.assignedRestaurant.toString()).orderByKey().limitToLast(500).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var orderList = mutableListOf<Order>()
                for (orderData in snapshot.children) {
                    var _orderItems = mutableMapOf<MenuItem, Int>()
                    orderData.child("orderItems").children.forEach { orderItemSnapshot ->
                        _orderItems[MenuItem(
                            orderItemSnapshot.child("adminID").value.toString(),
                            orderItemSnapshot.child("category").value.toString(),
                            orderItemSnapshot.child("itemName").value.toString(),
                            orderItemSnapshot.child("itemPrice").value.toString()
                                .toDouble(),
                            orderItemSnapshot.child("id").value.toString(),
                            orderItemSnapshot.child("adminID").value.toString(),
                            try {
                                mutableListOf(orderItemSnapshot.child("itemImageUrls").children.first().value.toString()
                                    .toUri())
                            } catch (e:Exception) {
                                mutableListOf()
                            }
                            ,
                            orderItemSnapshot.child("orderItemID").value.toString()
                        )] = orderItemSnapshot.child("orderQuantity").value.toString()
                            .toInt()
                    }

                    var _order = Order(
                        orderData.child("orderID").value.toString(),
                        orderData.child("table").value.toString(),
                        orderData.child("customerID").value.toString(),
                        _orderItems,
                        when (orderData.child("orderStatus").value.toString()){
                            "SentToKitchen" -> OrderStatus.SentToKitchen
                            "Cooking" -> OrderStatus.Cooking
                            "ReadyToServe" -> OrderStatus.ReadyToServe
                            "Served" -> OrderStatus.Served
                            else -> throw OrderStatusException("OrderStatus not found on loadOrderData")
                        },
                        orderData.child("restaurant").value.toString(),
                        orderData.child("employeeID").value.toString(),
                        when (orderData.child("userType").value.toString()){
                            "ADMIN" -> UserTypes.ADMIN
                            "CASHIER" -> UserTypes.CASHIER
                            "WAITER" -> UserTypes.WAITER
                            "KITCHENSTAFF" -> UserTypes.KITCHENSTAFF
                            "CUSTOMER" -> UserTypes.CUSTOMER
                            else -> throw UserTypeException("Usertype Not Found on loadOrderData")
                        },
                        orderData.child("adminID").value.toString(),
                        orderData.child("orderNotes").value.toString()
                    )
                    orderList.add(_order)
                }
                restaurantOrderList.value = orderList
            }
            override fun onCancelled(error: DatabaseError) {
                //Do Nothing
            }
        })
    }
}