package com.example.scanner.util

import com.google.firebase.database.DataSnapshot
//đổi snapshot sang lớp
fun <T> wrapSnapshotToClass(className: Class<T>,snapshot: DataSnapshot): T?{
    return snapshot.getValue(className)
}

fun <T> wrapSnapshotToArrayList(className : Class<T>,snapshot: DataSnapshot): MutableList<T>{
    val arrayList: MutableList<T> = arrayListOf()
    for (child in snapshot.children){
        child.getValue(className)?.let { arrayList.add(it) }
    }
    return arrayList


}