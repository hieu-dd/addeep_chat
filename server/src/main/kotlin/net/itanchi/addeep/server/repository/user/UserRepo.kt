package net.itanchi.addeep.server.repository.user

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface UserRepo : CoroutineCrudRepository<UserModel, Long> {
    @Query("SELECT * FROM users WHERE CONCAT(\"+\", country_code, phone_number) = :phone OR email = :email")
    suspend fun findByPhoneOrEmail(phone: String, email: String): UserModel?

    @Query("""
        SELECT * 
        FROM users 
        WHERE (id = :userId AND :phone = '' AND :addeepId = '')  
        OR CONCAT("+", country_code, phone_number) = :phone 
        OR (addeep_id = :addeepId AND allow_to_search_by_addeep_id = TRUE)  
    """)
    suspend fun findByIdOrPhoneOrAddeepId(userId: Long, phone: String, addeepId: String): UserModel?

    suspend fun findAllByPhoneNumberInOrEmailIn(phones: List<String>, emails: List<String>): List<UserModel>

    suspend fun findByFirebaseUid(firebaseUid: String): UserModel?

//    @Query(
//        """
//        UPDATE users
//        SET gender = COALESCE(:gender, gender),
//        dob = COALESCE(:dob, dob),
//        avatar = COALESCE(:avatar, avatar)
//        WHERE id = :userId
//    """
//    )
//    suspend fun updateUser(userId: Long, gender: Gender?, dob: LocalDate?, avatar: String?): UserModel
}