// StaffRepository.kt
package com.tlu.tlucontact.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tlu.tlucontact.data.model.Staff
import com.tlu.tlucontact.util.Constants
import kotlinx.coroutines.tasks.await

class StaffRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val staffCollection = firestore.collection(Constants.COLLECTION_STAFF)

    // Lấy tất cả CBGV
    suspend fun getAllStaff(): Result<List<Staff>> {
        return try {
            val snapshot = staffCollection.orderBy("fullName").get().await()
            val staffList = snapshot.toObjects(Staff::class.java)
            Result.success(staffList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy CBGV theo ID
    suspend fun getStaffById(staffId: String): Result<Staff> {
        return try {
            val doc = staffCollection.document(staffId).get().await()
            if (doc.exists()) {
                val staff = doc.toObject(Staff::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin cán bộ"))
                Result.success(staff)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin cán bộ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy CBGV theo userId (từ Firebase Auth)
    suspend fun getStaffByUserId(userId: String): Result<Staff> {
        return try {
            val snapshot = staffCollection.whereEqualTo("userId", userId).get().await()
            if (!snapshot.isEmpty) {
                val staff = snapshot.documents[0].toObject(Staff::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin cán bộ"))
                Result.success(staff)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin cán bộ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tìm kiếm CBGV theo tên
    suspend fun searchStaffByName(query: String): Result<List<Staff>> {
        return try {
            val snapshot = staffCollection
                .orderBy("fullName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val staffList = snapshot.toObjects(Staff::class.java)
            Result.success(staffList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy CBGV theo đơn vị
    suspend fun getStaffByUnit(unitId: String): Result<List<Staff>> {
        return try {
            val unitRef = firestore.collection(Constants.COLLECTION_UNITS).document(unitId)
            val snapshot = staffCollection
                .whereEqualTo("unit", unitRef)
                .orderBy("fullName")
                .get()
                .await()

            val staffList = snapshot.toObjects(Staff::class.java)
            Result.success(staffList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cập nhật thông tin CBGV
    suspend fun updateStaffProfile(staff: Staff): Result<Unit> {
        return try {
            staffCollection.document(staff.id)
                .update(
                    mapOf(
                        "fullName" to staff.fullName,
                        "phone" to staff.phone,
                        "photoURL" to staff.photoURL
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}