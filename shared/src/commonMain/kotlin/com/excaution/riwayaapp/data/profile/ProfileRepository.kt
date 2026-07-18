package com.excaution.riwayaapp.data.profile

import com.excaution.riwayaapp.core.network.ApiResult


class ProfileRepository(
    private val api: ProfileApi
) {
    suspend fun getProfile(): ApiResult<UserProfileResponse> =
        api.getProfile()

    suspend fun updateProfile(username: String, bio: String?): ApiResult<UpdateProfileResponse> =
        api.updateProfile(username, bio)

}