// StudentRepository.kt
package com.tlu.tlucontact.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tlu.tlucontact.data.model.Student
import com.tlu.tlucontact.util.Constants
import kotlinx.coroutines.tasks.await

class StudentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val studentsCollection = firestore.collection(Constants.COLLECTION_STUDENTS)

    // Lấy tất cả sinh viên (chỉ CBGV mới được gọi)
    suspend fun getAllStudents(): Result<List<Student>> {
        return try {
            val snapshot = studentsCollection.orderBy("fullName").get().await()
            val students = snapshot.toObjects(Student::class.java)
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy sinh viên theo ID
    suspend fun getStudentById(studentId: String): Result<Student> {
        return try {
            val doc = studentsCollection.document(studentId).get().await()
            if (doc.exists()) {
                val student = doc.toObject(Student::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin sinh viên"))
                Result.success(student)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin sinh viên"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy sinh viên theo userId (từ Firebase Auth)
    suspend fun getStudentByUserId(userId: String): Result<Student> {
        return try {
            val snapshot = studentsCollection.whereEqualTo("userId", userId).get().await()
            if (!snapshot.isEmpty) {
                val student = snapshot.documents[0].toObject(Student::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin sinh viên"))
                Result.success(student)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin sinh viên"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tìm kiếm sinh viên theo tên
    suspend fun searchStudentsByName(query: String): Result<List<Student>> {
        return try {
            val snapshot = studentsCollection
                .orderBy("fullName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val students = snapshot.toObjects(Student::class.java)
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy sinh viên theo lớp
    suspend fun getStudentsByClass(className: String): Result<List<Student>> {
        return try {
            val snapshot = studentsCollection
                .whereEqualTo("className", className)
                .orderBy("fullName")
                .get()
                .await()

            val students = snapshot.toObjects(Student::class.java)
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cập nhật thông tin sinh viên
    suspend fun updateStudentProfile(student: Student): Result<Unit> {
        return try {
            studentsCollection.document(student.id)
                .update(
                    mapOf(
                        "fullName" to student.fullName,
                        "phone" to student.phone,
                        "address" to student.address,
                        "photoURL" to student.photoURL
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}