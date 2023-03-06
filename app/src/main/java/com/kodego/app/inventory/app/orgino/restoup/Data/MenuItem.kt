package com.kodego.app.inventory.app.orgino.restoup.Data

import android.net.Uri

data class MenuItem(
    var restaurant:String,
    var category:String,
    var itemName:String,
    var itemPrice:Double,
) {
    var itemImages:MutableList<Uri>? = null
    var id:String? = null
    var adminID:String? = null
    var orderItemID = ""

    constructor(
        restaurant:String,
        category:String,
        itemName:String,
        itemPrice:Double,
        itemImages:MutableList<Uri>
    ):this(
        restaurant,
        category,
        itemName,
        itemPrice,
    ) {this.itemImages = itemImages}

    constructor(
        restaurant:String,
        category:String,
        itemName:String,
        itemPrice:Double,
        id:String,
        adminID:String,
        _itemImage:MutableList<Uri>?
    ):this(
        restaurant,
        category,
        itemName,
        itemPrice,
    ) {
        this.id = id
        this.adminID = adminID
        this.itemImages = _itemImage
    }
    constructor(
        restaurant:String,
        category:String,
        itemName:String,
        itemPrice:Double,
        id:String,
        adminID:String,
        _itemImage:MutableList<Uri>?,
        _orderItemID: String
    ):this(
        restaurant,
        category,
        itemName,
        itemPrice,
    ) {
        this.id = id
        this.adminID = adminID
        this.itemImages = _itemImage
        this.orderItemID = _orderItemID
    }
}

data class ConvertedMenuItem(
    var restaurant:String,
    var category:String,
    var itemName:String,
    var itemPrice:Double,
    var id:String,
    var adminID:String
) {
    var orderQuantity: Int = 0
    var orderItemID = ""

    constructor(
        _restaurant:String,
        _category:String,
        _itemName:String,
        _itemPrice:Double,
        _id:String,
        _adminID:String,
        _orderQuantity: Int,
        _orderItemID:String
    ): this (
        _restaurant,
        _category,
        _itemName,
        _itemPrice,
        _id,
        _adminID,
    ) {
        this.orderItemID = _orderItemID
        this.orderQuantity = _orderQuantity}
}