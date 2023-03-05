package com.kodego.app.inventory.app.orgino.restoup.Data

data class Transaction (
    var transactionID:String,
    var order:Order,
    var totalAmount:Double,
    var finalAmount:Double,
    var restaurant: Restaurant,
    var employeeID:String,
    var userType:UserTypes,
    var adminID:String
)