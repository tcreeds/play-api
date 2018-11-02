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
import com.tcreeds.play.rest.resources.*
import com.tcreeds.play.service.email.EmailClient
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
        val emailClient: EmailClient,

        val bCryptPasswordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()
){
    fun createUser(resource: CreateUserResource): ResultMessage {
        val user = userRepository.findByEmail(resource.email)
        if (user == null) {
            val unverifiedUser = unverifiedUserRepository.findByEmail(resource.email)
            val verificationId: String = UUID.randomUUID().toString()
            println(verificationId)
            emailClient.sendEmail(resource.email, "Play Account Verification", "https://play.tcreeds.io/verify/?email=${resource.email}&verificationId=$verificationId")
            if (unverifiedUser == null){
                unverifiedUserRepository.save(UnverifiedUserEntity(
                        email = resource.email,
                        displayName = resource.displayName,
                        password = bCryptPasswordEncoder.encode(resource.password),
                        verificationId = verificationId))
                return ResultMessage.SENT_VERIFICATION_EMAIL
            }
            else {
                unverifiedUserRepository.save(UnverifiedUserEntity(
                        userId = unverifiedUser.userId,
                        email = unverifiedUser.email,
                        displayName = unverifiedUser.displayName,
                        password = bCryptPasswordEncoder.encode(unverifiedUser.password),
                        verificationId = verificationId))
                return ResultMessage.RESEND_VERIFICATION_EMAIL
            }

        }
        return ResultMessage.EMAIL_IN_USE
    }

    fun getUser(email: String): UserResource? {
        val user = userRepository.findByEmail(email)
        return if (user != null) UserResource(
                id = user.userId,
                displayName = user.displayName ?: ""
        ) else null
    }

    @Transactional
    fun verifyUser(resource: VerificationResource): ResultMessage {
        val user = unverifiedUserRepository.findByVerificationId(resource.verificationId)
        if (user != null){
            if (user.email == resource.email){
                val verifiedUser = UserEntity(
                        email = user.email,
                        displayName = user.displayName,
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

    fun checkLogin(resource: LoginResource): UserResource? {
        val userDataEntity: UserEntity? = userRepository.findByEmail(resource.email)
        if (userDataEntity != null && bCryptPasswordEncoder.matches(resource.password, userDataEntity.password))
            return UserResource(
                    id = userDataEntity.userId,
                    email = userDataEntity.email,
                    displayName = userDataEntity.displayName
            )
        return null
    }

    fun getProfile(userId: Long): ProfileResource? {
        val userDataEntity: UserEntity? = userRepository.findByUserId(userId)
        if (userDataEntity != null){
            return ProfileResource(
                    username = userDataEntity.displayName ?: "",
                    bio = userDataEntity.bio?: "",
                    communities = userDataEntity.communities.map {
                        val community = it.community
                        CommunityResource(
                                id = community.communityId,
                                name = community.name,
                                description = community.description
                        )
                    }
            )
        }
        return null
    }

    fun updateProfile(resource: ProfileResource, email: String): ResultMessage {
        val userDataEntity: UserEntity? = userRepository.findByEmail(email)
        if (userDataEntity != null){
            userDataEntity.displayName = resource.username
            userDataEntity.bio = resource.bio
            userRepository.save(userDataEntity)
            return ResultMessage.UPDATED_PROFILE
        }
        return ResultMessage.USER_NOT_FOUND
    }

    fun sendResetPasswordEmail(resource: UserResource): ResultMessage {
        val userDataEntity: UserEntity? = userRepository.findByEmail(resource.email)
        if (userDataEntity != null){
            val existingEntity = passwordResetRepository.findByEmail(resource.email)
            if (existingEntity != null){
                passwordResetRepository.deleteById(existingEntity.userId)
            }

            val resetId: String = UUID.randomUUID().toString()
            emailClient.sendEmail(resource.email, "Play Password Reset", "https://play.tcreeds.io/resetpassword/$resetId")

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

    fun deleteUser(resource: UserResource): ResultMessage {
        val user = userRepository.findByEmail(resource.email)
        if (user != null){
            userRepository.delete(user)
            return ResultMessage.DELETED_USER
        }
        return ResultMessage.USER_NOT_FOUND
    }

    fun mockUser(name: String, password: String) {
        //repository.save(UserEntity(email = name, password = bCryptPasswordEncoder.encode(password), verified = true))
    }
}