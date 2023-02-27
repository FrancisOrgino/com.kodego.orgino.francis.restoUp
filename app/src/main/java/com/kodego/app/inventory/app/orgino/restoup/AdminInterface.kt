package com.kodego.app.inventory.app.orgino.restoup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityAdminInterfaceBinding
import com.google.firebase.database.ValueEventListener
import com.kodego.app.inventory.app.orgino.restoup.Model.ItemList

class AdminInterface : AppCompatActivity() {

    lateinit var binding: ActivityAdminInterfaceBinding
    var adminAddItemDao = AdminAddItemDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdminAddItem.setOnClickListener {
            val intentAdminAddItem = Intent(this, AdminAddItem::class.java)
            startActivity(intentAdminAddItem)
        }

        binding.btnCreateNewUser.setOnClickListener {
            val intentCreateNewUser = Intent(this, AdminCreateUser::class.java)
            startActivity(intentCreateNewUser)
        }

        binding.btnViewItems.setOnClickListener {
            viewItems()
        }
    }

    fun viewItems() {

        adminAddItemDao.get().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var itemList: ArrayList<ItemList> = ArrayList<ItemList>()

                var dataFromDbItems = snapshot.children

                for (data in dataFromDbItems) {

                    var itemId = data.key.toString()
                    var itemCategory = data.child("category").value.toString()
                    var itemName = data.child("itemName").value.toString()
                    var itemPrice = data.child("itemPrice").value.toString().toInt()

                    var listOfItems = ItemList(itemId, itemCategory, itemName, itemPrice)
                    itemList.add(listOfItems)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

}
