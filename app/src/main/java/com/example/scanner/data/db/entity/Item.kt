package com.example.scanner.data.db.entity

import com.google.firebase.database.PropertyName
data class Item(
    @get:PropertyName("id")@set:PropertyName("id") var id:String?= "",
    @get:PropertyName("name")@set:PropertyName("name") var name: String? = "",
    @get:PropertyName("price")@set:PropertyName("price") var price: String?= "",
    @get:PropertyName("code")@set:PropertyName("code") var code: String? = "",
    @get:PropertyName("imageOfItem")@set:PropertyName("imageOfItem") var imageOfItem :String? ="",

)