package com.kodego.app.inventory.app.orgino.restoup.Data

import android.net.Uri

data class MenuItem(
    var restaurant:String,
    var category:String,
    var itemName:String,
    var itemPrice:Double,
) {
    var itemImages:List<Uri>? = null
    var id:String? = null

    constructor(
        restaurant:String,
        category:String,
        itemName:String,
        itemPrice:Double,
        itemImages:List<Uri>
    ):this(
        restaurant,
        category,
        itemName,
        itemPrice,
    ) {this.itemImages = itemImages}
}

data class ConvertedMenuItem(
    var restaurant:String,
    var category:String,
    var itemName:String,
    var itemPrice:Double,
    var id:String
)