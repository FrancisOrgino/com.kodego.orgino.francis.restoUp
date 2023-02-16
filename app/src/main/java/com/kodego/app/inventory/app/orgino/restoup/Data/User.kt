package com.kodego.app.inventory.app.orgino.restoup.Data

data class User (
    var userName:String,
    var passWord:String,
    var userType:UserTypes
) {
    var uID:String = "CUSTOMER"
    constructor(userName:String,
                passWord:String,
                userType:UserTypes,
                providedUID:String) : this(userName, passWord, userType){this.uID=providedUID}
}

data class ConvertedUser (
    var id:String? = null,
    var uID:String? = null,
    var userName:String,
    var passWord:String,
    var userType:String
)

enum class UserTypes() {
    ADMIN,
    CASHIER,
    WAITER,
    KITCHENSTAFF,
    CUSTOMER
}