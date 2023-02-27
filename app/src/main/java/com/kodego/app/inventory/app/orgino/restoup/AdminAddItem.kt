package com.kodego.app.inventory.app.orgino.restoup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kodego.app.inventory.app.orgino.restoup.Model.ItemList
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityAdminAddItemBinding


class AdminAddItem : AppCompatActivity() {

    lateinit var binding: ActivityAdminAddItemBinding
    var adminAddItemDao = AdminAddItemDao()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateItem.setOnClickListener {

            adminAddItemDao.add(AddItem(binding.etCategory.text.toString(),binding.etItemName.text.toString(),binding.etItemPrice.text.toString().toInt()))

            Toast.makeText(applicationContext,"ADD ITEM SUCCESSFUL",Toast.LENGTH_SHORT).show()

        }
    }
}