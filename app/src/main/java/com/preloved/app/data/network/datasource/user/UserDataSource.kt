package com.preloved.app.data.network.datasource

import com.preloved.app.data.network.model.request.auth.LoginRequest
import com.preloved.app.data.network.model.request.auth.RegisterRequest
import com.preloved.app.data.network.model.request.auth.UpdateProfileRequest
import com.preloved.app.data.network.model.response.LoginResponse
import com.preloved.app.data.network.model.response.RegisterResponse
import com.preloved.app.data.network.model.response.UpdateProfileResponse
import com.preloved.app.data.network.model.response.UserResponse
import java.io.File

interface UserDataSource {
    suspend fun postLogin(loginRequest: LoginRequest): LoginResponse
    suspend fun postRegister(registerRequest: RegisterRequest): RegisterResponse

    suspend fun getProfileData(token: String): UserResponse

    suspend fun updateProfileData(
        token: String,
        email: String,
        nama: String,
        city: String,
        address: String,
        phone: String,
        profilePhoto: File? = null ) : UserResponse

    suspend fun getUserData(token:String): UserResponse

   }