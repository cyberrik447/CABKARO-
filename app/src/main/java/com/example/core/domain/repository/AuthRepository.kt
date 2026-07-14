package com.example.core.domain.repository

import com.example.core.domain.Resource
import com.example.core.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface handling Firebase Auth with mobile number + OTP verification.
 * Strict Business Rule: OTP verification is required before account creation.
 */
interface AuthRepository {
    
    /**
     * Sends an OTP verification request to the specified mobile number.
     */
    fun requestOtp(mobileNumber: String): Flow<Resource<Unit>>

    /**
     * Verifies the OTP. If valid, signs in the user.
     * Login uses mobile number and OTP.
     */
    fun verifyOtpAndLogin(mobileNumber: String, otp: String): Flow<Resource<User>>

    /**
     * Creates a profile after OTP verification is complete.
     */
    fun createAccount(user: User): Flow<Resource<User>>

    /**
     * Retrieves the current logged-in user, or null if none.
     */
    fun getCurrentUser(): Flow<Resource<User?>>

    /**
     * Signs the user out.
     */
    suspend fun signOut()
}
