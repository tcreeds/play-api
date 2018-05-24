package com.tcreeds.play.service

import com.tcreeds.play.repository.UserDataRepository
import com.tcreeds.play.repository.entity.UserDataEntity
import com.tcreeds.play.rest.resources.UserResource
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(

        @Autowired
        val repository: UserDataRepository,

        val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
){
    fun createUser(resource: UserResource): Boolean {
        if (repository.findByEmail(resource.email) == null) {
            repository.save(UserDataEntity(resource.email, UUID.randomUUID().toString()))
            return true
        }
        return false
    }

    fun verifyUser(resource: UserResource): Boolean {
        val user: UserDataEntity? = repository.findByVerificationId(resource.verificationId)
        if (user != null && UserDataEntity.isVerifiedUser(user)){
            user.password = bCryptPasswordEncoder.encode(resource.password)
            repository.save(user)
            return true
        }
        return false
    }

    fun checkLogin(resource: UserResource): Boolean {
        val userDataEntity: UserDataEntity? = repository.findByEmail(resource.email)
        if (userDataEntity != null && UserDataEntity.isVerifiedUser(userDataEntity) && bCryptPasswordEncoder.matches(resource.password, userDataEntity.password))
            return true
        return false
    }
}