package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.UnverifiedUserEntity
import com.tcreeds.play.repository.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UnverifiedUserRepository : CrudRepository<UnverifiedUserEntity, Long> {
    fun findByVerificationId(verificationId: String): UnverifiedUserEntity?
    fun findByEmail(email: String): UnverifiedUserEntity?
}