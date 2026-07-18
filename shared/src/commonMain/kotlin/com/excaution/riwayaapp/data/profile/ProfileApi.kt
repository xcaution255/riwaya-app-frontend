package com.excaution.riwayaapp.data.profile

import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.network.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody

class ProfileApi(private val client: HttpClient) {

    suspend fun getProfile(): ApiResult<UserProfileResponse> =
        safeApiCall {
            client.get("/users/profile").body()
        }

    suspend fun updateProfile(username: String, bio: String?): ApiResult<UpdateProfileResponse> =
        safeApiCall {
            client.patch("/users/update-profile") {
                setBody(UpdateProfileRequest(username, bio))
            }.body()
        }
    }
