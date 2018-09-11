package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.PasswordResetEntity
import com.tcreeds.play.repository.entity.UnverifiedUserEntity
import com.tcreeds.play.repository.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetRepository : CrudRepository<PasswordResetEntity, Long> {
    fun findByResetId(verificationId: String): PasswordResetEntity?
    fun findByEmail(email: String): PasswordResetEntity?
}