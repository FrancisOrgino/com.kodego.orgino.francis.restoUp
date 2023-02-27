package com.kodego.app.inventory.app.orgino.restoup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.Query
import com.kodego.app.inventory.app.orgino.restoup.databinding.ActivityAdminAddTableBinding
import com.kodego.app.inventory.app.orgino.restoup.Model.TableModel

class AdminAddTable : AppCompatActivity() {
    lateinit var binding: ActivityAdminAddTableBinding
    var addTableDao = AddTableDao()
  //var everyTable: List<TableModel> = addTableDao.getAllData()
  // var adapter = TableAdapter(everyTable)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdminAddTableBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnCreateTable.setOnClickListener {

            addTableDao.add(AddTable(binding.etAddTable.text.toString()))

        }


      }

    }






