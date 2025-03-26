// DirectoryOption.kt
package com.tlu.tlucontact.data.model

/**
 * Model class representing a directory option in the main screen
 */
data class DirectoryOption(
    val id: Int,
    val title: String,
    val description: String,
    val iconRes: Int
)