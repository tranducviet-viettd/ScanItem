package com.example.scanner.data.db.remote

import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.example.scanner.data.db.Result
import com.example.scanner.data.db.entity.Item
import com.example.scanner.util.wrapSnapshotToArrayList
import com.example.scanner.util.wrapSnapshotToClass
import com.google.firebase.database.ChildEventListener

class FirebaseDataSource {

    companion object{

        val dbInstance = FirebaseDatabase.getInstance()
    }

    private fun refToPath(path : String): DatabaseReference {
        return dbInstance.reference.child(path)
    }

    //tạo người nghe dữ liệu và nếu dữ liệu thay đôỉ gắn dữ liệu vào bộ điều khiêển task
    private fun attackValueListenerToTaskComplete(src : TaskCompletionSource<DataSnapshot>): ValueEventListener{
        return (
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        src.setResult(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        src.setException(Exception(error.message))
                    }
                }
                )

    }
    // tạo người nghe dữ liệu và nếu dữ liệu thay đôỉ gọi hàm gọi lại b và truyền dữ liệu kiểu list
    private fun <T> attackValueListenerToBlockList(resultClassName:Class<T>,b : (Result<MutableList<T>>) -> Unit) : ValueEventListener {
        return(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        b.invoke(Result.Success(wrapSnapshotToArrayList(resultClassName,snapshot)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        b.invoke(Result.Error(error.message))
                    }
                }
                )
    }
    // tạo người nghe dữ liệu và nếu dữ liệu thay đôỉ gọi hàm tương ứng và truyền dữ liệu kiểu list

    private fun <T> attackValueListenerToBlock(resultClassName: Class<T>,  onAdded: (Result<T>) -> Unit, onChanged: (Result<T>) -> Unit, onRemove:(Result<T>) -> Unit) : ChildEventListener {
        return( object : ChildEventListener{
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
               onAdded.invoke(Result.Success(wrapSnapshotToClass(resultClassName,snapshot)))
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
                )
    }
    // update mặt hàng mới lên firebase
    fun updateNewItem(itemID:String, item: Item, b: (Result<String>) -> Unit) {
        refToPath("items/${itemID}").setValue(item)
            .addOnSuccessListener {
                b.invoke(Result.Success("Thêm mặt hàng mới THÀNH CÔNG"))
            }
            .addOnFailureListener { e ->
                b.invoke(Result.Error(e.message ?: "Thêm mặt hàng mới THẤT BẠI"))
            }
    }



}