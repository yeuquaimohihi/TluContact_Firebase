// Staff.kt
package com.tlu.tlucontact.data.model

import com.google.firebase.firestore.DocumentReference

data class Staff(
    val id: String = "",
    val staffId: String = "",
    val fullName: String = "",
    val position: String = "",
    val phone: String = "",
    val email: String = "",
    val photoURL: String = "",
    val unit: DocumentReference? = null,
    val userId: String = ""
)