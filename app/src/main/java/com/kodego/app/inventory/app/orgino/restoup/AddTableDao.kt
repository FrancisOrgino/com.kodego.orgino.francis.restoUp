package com.kodego.app.inventory.app.orgino.restoup

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddTableDao {


        var dbReference : DatabaseReference = Firebase.database.reference

        fun add(addItem : AddTable){
            dbReference.push().setValue(addItem )
        }
        fun get(query: Any?): Query {
            return dbReference.orderByKey()
        }
        fun remove(key:String){
            dbReference.child(key).removeValue()
        }
        fun update(key:String,map: Map<String,Int>){
            dbReference.child(key).updateChildren(map)
        }
}