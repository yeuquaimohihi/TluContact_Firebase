// AuthRepository.kt
package com.tlu.tlucontact.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tlu.tlucontact.data.model.User
import com.tlu.tlucontact.util.Constants
import com.tlu.tlucontact.util.FirebaseUtils
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Đăng ký người dùng mới
    suspend fun register(email: String, password: String, displayName: String): Result<User> {
        return try {
            // Xác định role từ email
            val role = FirebaseUtils.determineRoleFromEmail(email)
            if (role.isEmpty()) {
                return Result.failure(Exception("Email không hợp lệ. Vui lòng sử dụng email trường cấp."))
            }

            // Tạo tài khoản trong Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(Exception("Đăng ký thất bại"))

            // Tạo user document trong Firestore
            val user = User(
                id = userId,
                email = email,
                role = role,
                displayName = displayName,
                photoURL = "",
                phoneNumber = ""
            )

            firestore.collection(Constants.COLLECTION_USERS).document(userId)
                .set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng nhập
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: return Result.failure(Exception("Đăng nhập thất bại"))

            // Lấy thông tin user từ Firestore
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId).get().await()

            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin người dùng"))
                Result.success(user)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin người dùng"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy thông tin user hiện tại
    suspend fun getCurrentUser(): Result<User> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Chưa đăng nhập"))

        return try {
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId).get().await()

            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin người dùng"))
                Result.success(user)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin người dùng"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Đăng xuất
    fun logout() {
        auth.signOut()
    }

    // Kiểm tra trạng thái đăng nhập
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Cập nhật thông tin người dùng
    suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.id)
                .update(
                    mapOf(
                        "displayName" to user.displayName,
                        "photoURL" to user.photoURL,
                        "phoneNumber" to user.phoneNumber
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}