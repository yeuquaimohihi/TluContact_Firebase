// FirebaseUtils.kt
package com.tlu.tlucontact.util

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseUtils {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Kiểm tra role dựa trên email
    fun determineRoleFromEmail(email: String): String {
        return when {
            email.endsWith(Constants.EMAIL_PATTERN_STAFF) -> "CBGV"
            email.endsWith(Constants.EMAIL_PATTERN_STUDENT) -> "SV"
            else -> ""
        }
    }

    // Upload ảnh lên Firebase Storage
    suspend fun uploadImage(imageUri: Uri, folder: String): String {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("$folder/$fileName")

        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    // Kiểm tra người dùng đã có hồ sơ trong Firestore chưa
    suspend fun checkUserProfileExists(userId: String): Boolean {
        val userDoc = firestore.collection(Constants.COLLECTION_USERS).document(userId).get().await()
        return userDoc.exists()
    }
}