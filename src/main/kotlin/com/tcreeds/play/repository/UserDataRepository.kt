package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.UserDataEntity
import org.socialsignin.spring.data.dynamodb.repository.EnableScan
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@EnableScan
interface UserDataRepository : CrudRepository<UserDataEntity, Long> {
    fun findByEmail(email: String): UserDataEntity?
    fun findByVerificationId(verificationId: String): UserDataEntity?
}