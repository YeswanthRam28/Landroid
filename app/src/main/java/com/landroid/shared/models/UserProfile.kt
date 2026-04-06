// app/src/main/java/com/landroid/shared/models/UserProfile.kt
package com.landroid.shared.models

data class UserProfile(
    val uid: String,
    val name: String,
    val phone: String,
    val role: UserRole
)

enum class UserRole { CONSULTANT, LANDOWNER }
