package com.kodego.app.inventory.app.orgino.restoup.Data

data class Order (
    var orderID:String,
    var table:String,
    var customerID:String,
    var orderItems:MutableMap<MenuItem, Int>,
    var orderStatus:OrderStatus,
    var restaurant:String,
    var employeeID:String,
    var userType:UserTypes,
    var adminID:String,
    var orderNotes:String
) {
    //app entry
    constructor(
        _table:String,
        _customerID:String,
        _orderItems:MutableMap<MenuItem, Int>,
        _restaurant:String,
        _employeeID:String,
        _userType:UserTypes,
        _adminID:String,
        _orderNotes:String
    ) : this(
        "",
        _table,
        _customerID,
        _orderItems,
        OrderStatus.SentToKitchen,
        _restaurant,
        _employeeID,
        _userType,
        _adminID,
        _orderNotes
    )
}

//db push helper
data class ConvertedOrder(
    var orderID:String,
    var table:String,
    var customerID:String,
    var orderStatus:OrderStatus,
    var restaurant:String,
    var employeeID:String,
    var userType:UserTypes,
    var adminID:String
){

}

enum class OrderStatus() {
    SentToKitchen,
    Cooking,
    ReadyToServe,
    Served
}
