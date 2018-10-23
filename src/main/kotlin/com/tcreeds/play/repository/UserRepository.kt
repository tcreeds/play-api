package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findByUserId(userId: Long): UserEntity?
}