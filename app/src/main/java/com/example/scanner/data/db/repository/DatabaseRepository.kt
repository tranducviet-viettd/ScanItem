package com.example.scanner.data.db.repository

import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.remote.FirebaseDataSource
import com.example.scanner.util.wrapSnapshotToClass

class DatabaseRepository {

    private val firebaseDataSource = FirebaseDataSource()

    fun addItem(itemID:String,item: Item,b: (Result<String>) -> Unit){
        firebaseDataSource.updateNewItem(itemID,item,b)
    }

    fun loadItem(itemID:String,b: (Result<Item>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadItemTask(itemID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Item::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
}