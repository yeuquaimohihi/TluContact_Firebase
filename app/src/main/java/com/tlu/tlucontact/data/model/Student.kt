// Student.kt
package com.tlu.tlucontact.data.model

import com.google.firebase.firestore.DocumentReference

data class Student(
    val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val studentId: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val photoURL: String = "",
    val classInfo: DocumentReference? = null  // Reference to class document in Firestore
)