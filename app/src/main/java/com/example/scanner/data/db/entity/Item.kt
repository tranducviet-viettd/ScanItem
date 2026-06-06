package com.example.scanner.data.db.entity


import com.google.firebase.database.PropertyName


data class Item(
    @get:PropertyName("id")@set:PropertyName("id") var id:String = "",
    @get:PropertyName("name")@set:PropertyName("name") var name: String = "",
    @get:PropertyName("price")@set:PropertyName("price") var price: String = "",
    @get:PropertyName("code")@set:PropertyName("code") var code: String = "",
    @get:PropertyName("imageOfItem")@set:PropertyName("imageOfItem") var imageOfItem :String ="",
    @get:PropertyName("quantity")@set:PropertyName("quantity") var quantity : Quantity = Quantity()
)

data class Quantity(
    @get:PropertyName("piece")@set:PropertyName("piece") var piece : Int = 0,
    @get:PropertyName("pack")@set:PropertyName("pack") var pack : Int = 0,
    @get:PropertyName("box")@set:PropertyName("box") var box : Int = 0,

    )