package com.example.messenger.model.dataModel

import java.io.Serializable

data class User(
    val id: String?,
    val email: String?,
    val fullName: String?
): Serializable{

}