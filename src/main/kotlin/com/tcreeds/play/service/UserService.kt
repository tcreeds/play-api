package com.tcreeds.play.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import com.tcreeds.play.repository.UserDataRepository
import com.tcreeds.play.repository.entity.UserDataEntity
import com.tcreeds.play.rest.resources.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(

        @Autowired
        val repository: UserDataRepository,

        @Autowired
        val amazonSES: AmazonSimpleEmailService,

        val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
){
    fun createUser(resource: UserResource): Boolean {
        if (repository.findByEmail(resource.email) == null) {
            val verificationId: String = UUID.randomUUID().toString()
            val request: SendEmailRequest = SendEmailRequest()
                    .withDestination(Destination(listOf(resource.email)))
                    .withSource("no-reply@tcreeds.io")
                    .withMessage(Message()
                            .withBody(Body()
                                    .withHtml(Content()
                                            .withCharset("UTF-8").withData(verificationId))
                                    .withText(Content()
                                            .withCharset("UTF-8").withData(verificationId)))
                            .withSubject(Content()
                                    .withCharset("UTF-8").withData("Play Account Verification")))
            amazonSES.sendEmail(request)
            repository.save(UserDataEntity(resource.email, verificationId))

            return true
        }
        return false
    }

    fun verifyUser(resource: UserResource): Boolean {
        val user: UserDataEntity? = repository.findByVerificationId(resource.verificationId)
        if (user != null && !UserDataEntity.isVerifiedUser(user)){
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