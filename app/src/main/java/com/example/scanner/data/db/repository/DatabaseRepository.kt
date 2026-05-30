package com.example.scanner.data.db.repository

import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.remote.FirebaseDataSource

class DatabaseRepository {

    private val firebaseDataSource = FirebaseDataSource()

    fun addItem(itemID:String,item: Item,b: (Result<String>) -> Unit){
        firebaseDataSource.updateNewItem(itemID,item,b)
    }
}