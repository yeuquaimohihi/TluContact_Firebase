// Unit.kt
package com.tlu.tlucontact.data.model

import com.google.firebase.firestore.DocumentReference

data class OrganizationalUnit(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val address: String = "",
    val logoURL: String = "",
    val phone: String = "",
    val email: String = "",
    val fax: String = "",
    val parentUnit: DocumentReference? = null,
    val type: String = ""
)