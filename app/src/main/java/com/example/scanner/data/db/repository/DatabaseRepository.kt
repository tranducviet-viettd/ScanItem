package com.example.scanner.data.db.repository

import android.util.Log
import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.data.db.entity.User
import com.example.scanner.data.db.entity.UserInfo
import com.example.scanner.data.db.remote.FirebaseDataSource
import com.example.scanner.util.wrapSnapshotToArrayList
import com.example.scanner.util.wrapSnapshotToClass

class DatabaseRepository {

    private val firebaseDataSource = FirebaseDataSource()

    fun addItem(itemID:String,item: Item,b: (Result<String>) -> Unit){
        firebaseDataSource.updateNewItem(itemID,item,b)
    }

    fun updateNewUser(user: User,b: (Result<Unit>) -> Unit ) {
        Log.d("doi","43")
        firebaseDataSource.updateNewUser(user).addOnSuccessListener {
            b.invoke(Result.Success())
        }
    }
    fun loadItem(itemID:String,b: (Result<Item>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadItemTask(itemID).addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToClass(Item::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadItems(b: (Result<MutableList<Item>>) -> Unit){
        b.invoke(Result.Loading)
        firebaseDataSource.loadItems().addOnSuccessListener {
            b.invoke(Result.Success(wrapSnapshotToArrayList(Item::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }

    fun loadUserInfo(uid:String, b :(Result<UserInfo>) -> Unit){
        Log.d("doi","30")
//        b.invoke(Result.Loading)
        firebaseDataSource.loadUserInfo(uid).addOnSuccessListener {
            Log.d("doi","32")
            b.invoke(Result.Success(wrapSnapshotToClass(UserInfo::class.java,it)))
        }.addOnFailureListener { b.invoke(Result.Error(it.message)) }
    }
    fun isCustomer(uid: String, b: (Boolean) -> Unit) {
        Log.d("doi","23")
        firebaseDataSource.loadCustomer(uid)
            .addOnSuccessListener { snapshot ->
                Log.d("doi","28")
                // Nếu snapshot tồn tại → uid có trong /customers/ → Khách
                b(snapshot.exists())
            }
            .addOnFailureListener {
                b(false) // Lỗi thì coi như không phải khách (an toàn hơn)
            }
    }

}