// Constants.kt
package com.tlu.tlucontact.util

object Constants {
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_UNITS = "units"
    const val COLLECTION_STAFF = "staff"
    const val COLLECTION_STUDENTS = "students"

    // Preferences
    const val PREF_NAME = "TLUContactPrefs"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_ROLE = "userRole"
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_EMAIL = "userEmail"

    // Email patterns
    const val EMAIL_PATTERN_STAFF = "@tlu.edu.vn"
    const val EMAIL_PATTERN_STUDENT = "@e.tlu.edu.vn"

    // Error messages
    const val ERROR_EMAIL_REQUIRED = "Email không được để trống"
    const val ERROR_INVALID_EMAIL = "Email không hợp lệ"
    const val ERROR_PASSWORD_REQUIRED = "Mật khẩu không được để trống"
    const val ERROR_PASSWORD_LENGTH = "Mật khẩu phải có ít nhất 6 ký tự"
    const val ERROR_PASSWORD_MATCH = "Mật khẩu không trùng khớp"
    const val ERROR_NAME_REQUIRED = "Họ và tên không được để trống"

    // Intent keys
    const val INTENT_UNIT_ID = "unitId"
    const val INTENT_STAFF_ID = "staffId"
    const val INTENT_STUDENT_ID = "studentId"
}