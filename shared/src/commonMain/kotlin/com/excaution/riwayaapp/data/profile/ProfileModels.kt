package com.excaution.riwayaapp.data.profile

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable data class UserProfileResponse(
    val id: Uuid,
    val userName: String,
    val email: String,
    val bio: String?,
    val profileImage: String?,
    val createdAt: Instant
)

@Serializable data class UpdateProfileRequest(
    val username: String,
    val bio: String?
)

@Serializable data class UpdateProfileResponse(val message: String)