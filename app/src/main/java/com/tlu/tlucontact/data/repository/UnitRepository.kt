// UnitRepository.kt
package com.tlu.tlucontact.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tlu.tlucontact.data.model.OrganizationalUnit
import com.tlu.tlucontact.util.Constants
import kotlinx.coroutines.tasks.await

class UnitRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val unitsCollection = firestore.collection(Constants.COLLECTION_UNITS)

    // Lấy tất cả đơn vị
    suspend fun getAllUnits(): Result<List<OrganizationalUnit>> {
        return try {
            val snapshot = unitsCollection.orderBy("name").get().await()
            val units = snapshot.toObjects(OrganizationalUnit::class.java)
            Result.success(units)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy đơn vị theo ID
    suspend fun getUnitById(unitId: String): Result<OrganizationalUnit> {
        return try {
            val doc = unitsCollection.document(unitId).get().await()
            if (doc.exists()) {
                val organizationalUnit = doc.toObject(OrganizationalUnit::class.java)
                    ?: return Result.failure(Exception("Không tìm thấy thông tin đơn vị"))
                Result.success(organizationalUnit)
            } else {
                Result.failure(Exception("Không tìm thấy thông tin đơn vị"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Tìm kiếm đơn vị theo tên
    suspend fun searchUnitsByName(query: String): Result<List<OrganizationalUnit>> {
        return try {
            val snapshot = unitsCollection
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val units = snapshot.toObjects(OrganizationalUnit::class.java)
            Result.success(units)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lọc đơn vị theo loại
    suspend fun getUnitsByType(type: String): Result<List<OrganizationalUnit>> {
        return try {
            val snapshot = unitsCollection
                .whereEqualTo("type", type)
                .orderBy("name")
                .get()
                .await()

            val units = snapshot.toObjects(OrganizationalUnit::class.java)
            Result.success(units)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Lấy các đơn vị con
    suspend fun getChildUnits(parentUnitId: String): Result<List<OrganizationalUnit>> {
        return try {
            val parentRef = unitsCollection.document(parentUnitId)
            val snapshot = unitsCollection
                .whereEqualTo("parentUnit", parentRef)
                .orderBy("name")
                .get()
                .await()

            val units = snapshot.toObjects(OrganizationalUnit::class.java)
            Result.success(units)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}