package com.tcreeds.play.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import com.tcreeds.play.repository.UserRepository
import com.tcreeds.play.repository.entity.UserEntity
import com.tcreeds.play.rest.resources.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(

        @Autowired
        val repository: UserRepository,

        @Autowired
        val amazonSES: AmazonSimpleEmailService,

        val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
){
    fun createUser(resource: UserResource): Boolean {
        if (repository.findByEmail(resource.email) == null) {
            val verificationId: String = UUID.randomUUID().toString()
            repository.save(UserEntity(email = resource.email, verificationId = verificationId))
            sendEmail(resource.email, "Play Account Verification", "https://play.tcreeds.io/verify/$verificationId")
            return true
        }
        return false
    }

    fun verifyUser(resource: UserResource): Boolean {
        val user: UserEntity? = repository.findByVerificationId(resource.verificationId)
        if (user != null && !UserEntity.isVerifiedUser(user)){
            user.password = bCryptPasswordEncoder.encode(resource.password)
            user.verificationId = ""
            user.verified = true
            repository.save(user)
            return true
        }
        return false
    }

    fun checkLogin(resource: UserResource): Boolean {
        val userDataEntity: UserEntity? = repository.findByEmail(resource.email)
        if (userDataEntity != null && UserEntity.isVerifiedUser(userDataEntity) && bCryptPasswordEncoder.matches(resource.password, userDataEntity.password))
            return true
        return false
    }

    fun sendResetPasswordEmail(resource: UserResource): Boolean {
        val userDataEntity: UserEntity? = repository.findByEmail(resource.email)
        if (userDataEntity != null && UserEntity.isVerifiedUser(userDataEntity)){
            val verificationId: String = UUID.randomUUID().toString()
            userDataEntity.verificationId = verificationId
            repository.save(userDataEntity)
            sendEmail(resource.email, "Play Password Reset", "https://play.tcreeds.io/resetpassword/$verificationId")
        }
        return true
    }

    fun resetPassword(resource: UserResource): Boolean{
        val user: UserEntity? = repository.findByVerificationId(resource.verificationId)
        if (user != null){
            user.password = bCryptPasswordEncoder.encode(resource.password)
            user.verificationId = ""
            repository.save(user)
            return true
        }
        return false
    }

    fun sendEmail(destination: String, subject: String, body: String) {
        val request: SendEmailRequest = SendEmailRequest()
                .withDestination(Destination(listOf(destination)))
                .withSource("no-reply@tcreeds.io")
                .withMessage(Message()
                        .withBody(Body()
                                .withHtml(Content()
                                        .withCharset("UTF-8").withData(body))
                                .withText(Content()
                                        .withCharset("UTF-8").withData(body)))
                        .withSubject(Content()
                                .withCharset("UTF-8").withData(subject)))
        amazonSES.sendEmail(request)
    }
}