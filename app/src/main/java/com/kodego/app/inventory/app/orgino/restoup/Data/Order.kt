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

enum class OrderStatus(val label:String) {
    SentToKitchen("Sent to Kitchen"),
    Cooking("Cooking"),
    ReadyToServe("Ready To Serve"),
    Served("Served")
}

fun cloneOrder(anOrder : Order):Order{
    return Order(
        anOrder.orderID,
        anOrder.table,
        anOrder.customerID,
        anOrder.orderItems,
        anOrder.orderStatus,
        anOrder.restaurant,
        anOrder.employeeID,
        anOrder.userType,
        anOrder.adminID,
        anOrder.orderNotes
    )
}