package com.tcreeds.play.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.*
import com.tcreeds.play.repository.PasswordResetRepository
import com.tcreeds.play.repository.UserRepository
import com.tcreeds.play.repository.UnverifiedUserRepository
import com.tcreeds.play.repository.entity.PasswordResetEntity
import com.tcreeds.play.repository.entity.UnverifiedUserEntity
import com.tcreeds.play.repository.entity.UserEntity
import com.tcreeds.play.rest.ResultMessage
import com.tcreeds.play.rest.resources.LoginResource
import com.tcreeds.play.rest.resources.PasswordResetResource
import com.tcreeds.play.rest.resources.UserResource
import com.tcreeds.play.rest.resources.VerificationResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(

        @Autowired
        val userRepository: UserRepository,

        @Autowired
        val unverifiedUserRepository: UnverifiedUserRepository,

        @Autowired
        val passwordResetRepository: PasswordResetRepository,

        @Autowired
        val amazonSES: AmazonSimpleEmailService,

        val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
){
    fun createUser(resource: LoginResource): ResultMessage {
        if (userRepository.findByEmail(resource.email) == null) {
            val unverifiedUser = unverifiedUserRepository.findByEmail(resource.email)
            if (unverifiedUser == null){
                val verificationId: String = UUID.randomUUID().toString()

                sendEmail(resource.email, "Play Account Verification", "https://play.tcreeds.io/verify/$verificationId")
                unverifiedUserRepository.save(UnverifiedUserEntity(
                        email = resource.email,
                        password = resource.password,
                        verificationId = verificationId))
                return ResultMessage.SENT_VERIFICATION_EMAIL
            }
            else {
                val verificationId: String = UUID.randomUUID().toString()
                sendEmail(resource.email, "Play Account Verification", "https://play.tcreeds.io/verify/$verificationId")
                unverifiedUserRepository.save(UnverifiedUserEntity(
                        userId = unverifiedUser.userId,
                        email = unverifiedUser.email,
                        password = unverifiedUser.password,
                        verificationId = verificationId))
                return ResultMessage.RESEND_VERIFICATION_EMAIL
            }

        }
        return ResultMessage.EMAIL_IN_USE
    }

    @Transactional
    fun verifyUser(resource: VerificationResource): ResultMessage {
        val user = unverifiedUserRepository.findByVerificationId(resource.verificationId)
        if (user != null){
            if (user.email == resource.email){
                val verifiedUser = UserEntity(
                        email = user.email,
                        password = user.password
                )
                userRepository.save(verifiedUser)
                unverifiedUserRepository.deleteById(user.userId)
                return ResultMessage.VERIFIED_USER
            }
            return ResultMessage.VERIFICATION_ID_MISMATCH
        }
        return ResultMessage.VERIFICATION_ID_NOT_FOUND
    }

    fun checkLogin(resource: LoginResource): ResultMessage {
        val userDataEntity: UserEntity? = userRepository.findByEmail(resource.email)
        if (userDataEntity != null && bCryptPasswordEncoder.matches(resource.password, userDataEntity.password))
            return ResultMessage.LOGIN_SUCCESS
        return ResultMessage.LOGIN_FAILED
    }

    fun sendResetPasswordEmail(resource: UserResource): ResultMessage {
        val userDataEntity: UserEntity? = userRepository.findByEmail(resource.email)
        if (userDataEntity != null){
            val existingEntity = passwordResetRepository.findByEmail(resource.email)
            if (existingEntity != null){
                passwordResetRepository.deleteById(existingEntity.userId)
            }

            val resetId: String = UUID.randomUUID().toString()
            sendEmail(resource.email, "Play Password Reset", "https://play.tcreeds.io/resetpassword/$resetId")

            val newResetEntity = PasswordResetEntity(
                    userId = userDataEntity.userId,
                    email = resource.email,
                    resetId = resetId
            )
            passwordResetRepository.save(newResetEntity)
            return ResultMessage.SENT_PASSWORD_RESET_EMAIL
        }
        return ResultMessage.PASSWORD_RESET_EMAIL_FAILED
    }

    fun resetPassword(resource: PasswordResetResource): ResultMessage {
        val resetEntity = passwordResetRepository.findByResetId(resource.resetId)
        if (resetEntity != null){
            val user = userRepository.findByEmail(resource.email)
            if (user != null) {
                val password = bCryptPasswordEncoder.encode(resource.password)
                user.password = password
                userRepository.save(user)
                return ResultMessage.PASSWORD_RESET_SUCCESSFUL
            }
        }
        return ResultMessage.INVALID_PASSWORD_RESET_ATTEMPT
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

    fun mockUser(name: String, password: String) {
        //repository.save(UserEntity(email = name, password = bCryptPasswordEncoder.encode(password), verified = true))
    }
}