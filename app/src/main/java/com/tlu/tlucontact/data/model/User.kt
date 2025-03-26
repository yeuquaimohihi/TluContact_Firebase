// User.kt
package com.tlu.tlucontact.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val photoURL: String = "",
    val type: String = "",  // "staff", "student", or "guest"
    val verified: Boolean = false
) {
    fun isStaff(): Boolean {
        return type.equals("staff", ignoreCase = true)
    }

    fun isStudent(): Boolean {
        return type.equals("student", ignoreCase = true)
    }

    fun isGuest(): Boolean {
        return type.equals("guest", ignoreCase = true) || type.isEmpty()
    }
}