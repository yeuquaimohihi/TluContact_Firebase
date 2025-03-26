// PreferenceManager.kt
package com.tlu.tlucontact.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )

    // User authentication
    fun saveUserData(userId: String, userName: String, userEmail: String, userType: String, token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_EMAIL, userEmail)
        editor.putString(KEY_USER_TYPE, userType)
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun saveUserName(userName: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_NAME, userName)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getString(KEY_USER_ID, null) != null
    }

    fun getUserId(): String {
        return sharedPreferences.getString(KEY_USER_ID, "") ?: ""
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserType(): String {
        return sharedPreferences.getString(KEY_USER_TYPE, "") ?: ""
    }

    // NEW METHOD: Check if user is staff
    fun isUserStaff(): Boolean {
        val userType = getUserType()
        return userType.equals(USER_TYPE_STAFF, ignoreCase = true)
    }

    // NEW METHOD: Check if user is student
    fun isUserStudent(): Boolean {
        val userType = getUserType()
        return userType.equals(USER_TYPE_STUDENT, ignoreCase = true)
    }

    fun getToken(): String {
        return sharedPreferences.getString(KEY_TOKEN, "") ?: ""
    }

    fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "TLUContactPrefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_TYPE = "user_type"
        private const val KEY_TOKEN = "token"

        // User types
        const val USER_TYPE_STAFF = "staff"
        const val USER_TYPE_STUDENT = "student"
        const val USER_TYPE_GUEST = "guest"
    }
}