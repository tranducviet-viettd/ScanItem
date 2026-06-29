package com.example.scanner.data.db.entity

import com.google.firebase.database.PropertyName

data class User(
    @get:PropertyName("info") @set:PropertyName("info") var info:UserInfo = UserInfo(),
    )


data class UserInfo(
    @get:PropertyName("id") @set:PropertyName("id") var id:String="",
    @get:PropertyName("email") @set:PropertyName("email") var email:String="",
    @get:PropertyName("displayName") @set:PropertyName("displayName") var displayName:String="",
    @get:PropertyName("online") @set:PropertyName("online") var online: Boolean = false,
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl") var profileImageUrl : String = ""

)
