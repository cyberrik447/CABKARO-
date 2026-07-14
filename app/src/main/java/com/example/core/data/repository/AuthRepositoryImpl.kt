package com.example.core.data.repository

import com.example.core.database.dao.UserDao
import com.example.core.database.entity.UserEntity
import com.example.core.domain.Resource
import com.example.core.domain.repository.AuthRepository
import com.example.core.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao
) : AuthRepository {

    override fun requestOtp(mobileNumber: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            // Firebase OTP flow would go here (e.g. PhoneAuthProvider)
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun verifyOtpAndLogin(mobileNumber: String, otp: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            // Here we would perform Firebase verification.
            // For now, we simulate a successful login resulting in a User model.
            val mockUid = "user_" + mobileNumber.hashCode().toString()
            val user = User(
                uid = mockUid,
                name = "",
                mobileNumber = mobileNumber,
                isOtpVerified = true,
                isRegistered = false
            )
            // Save to local Room cache
            userDao.insertUser(
                UserEntity(
                    uid = user.uid,
                    name = user.name,
                    mobileNumber = user.mobileNumber,
                    isOtpVerified = user.isOtpVerified,
                    isRegistered = user.isRegistered
                )
            )
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun createAccount(user: User): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            // Write completed profile to local DB and update Firestore.
            val updatedUser = user.copy(isRegistered = true)
            userDao.insertUser(
                UserEntity(
                    uid = updatedUser.uid,
                    name = updatedUser.name,
                    mobileNumber = updatedUser.mobileNumber,
                    isOtpVerified = updatedUser.isOtpVerified,
                    isRegistered = updatedUser.isRegistered
                )
            )
            emit(Resource.Success(updatedUser))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override fun getCurrentUser(): Flow<Resource<User?>> = flow {
        emit(Resource.Loading)
        try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val cachedUser = userDao.getUserById(firebaseUser.uid)
                if (cachedUser != null) {
                    emit(Resource.Success(User(
                        uid = cachedUser.uid,
                        name = cachedUser.name,
                        mobileNumber = cachedUser.mobileNumber,
                        isOtpVerified = cachedUser.isOtpVerified,
                        isRegistered = cachedUser.isRegistered
                    )))
                } else {
                    emit(Resource.Success(null))
                }
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
