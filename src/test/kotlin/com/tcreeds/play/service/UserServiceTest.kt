package com.tcreeds.play.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.tcreeds.play.repository.PasswordResetRepository
import com.tcreeds.play.repository.UnverifiedUserRepository
import com.tcreeds.play.repository.UserRepository
import com.tcreeds.play.repository.entity.UnverifiedUserEntity
import com.tcreeds.play.repository.entity.UserEntity
import com.tcreeds.play.rest.ResultMessage
import com.tcreeds.play.rest.resources.LoginResource
import org.junit.Before
import org.junit.Test
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class UserServiceTest {

    @InjectMockKs
    private lateinit var service: UserService

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var unverifiedUserRepository: UnverifiedUserRepository

    @MockK
    private lateinit var passwordResetRepository: PasswordResetRepository

    @MockK
    private lateinit var encoder: BCryptPasswordEncoder

    @MockK
    private lateinit var email: AmazonSimpleEmailService

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every{ encoder.encode(allAny()) } returns "test"

        every { email.sendEmail(allAny()) } returns null
    }

    @Test
    fun `should create a new user`() {

        every{ userRepository.findByEmail(allAny()) } returns null
        every{ unverifiedUserRepository.findByEmail(allAny()) } returns null
        every{ unverifiedUserRepository.save<UnverifiedUserEntity>(allAny())} returns UnverifiedUserEntity(email="", verificationId = "", password = "")

        val result = service.createUser(LoginResource(email = "test", password = "test"))

        Assert.assertEquals(ResultMessage.SENT_VERIFICATION_EMAIL, result)
        verify(exactly = 1) { unverifiedUserRepository.save<UnverifiedUserEntity>(allAny()) }
    }

    @Test
    fun `should error when email in use`() {

        every{ userRepository.findByEmail(allAny()) } returns UserEntity()

        val result = service.createUser(LoginResource(email = "test", password = "test"))

        Assert.assertEquals(ResultMessage.EMAIL_IN_USE, result)
        verify(exactly = 0){ email.sendEmail(allAny()) }
        verify(exactly = 0){ unverifiedUserRepository.save<UnverifiedUserEntity>(allAny()) }
    }

    @Test
    fun `should resend when user exists but is unverified`() {

        every{ userRepository.findByEmail(allAny()) } returns null
        every{ unverifiedUserRepository.findByEmail(allAny()) } returns UnverifiedUserEntity(email="", verificationId = "", password = "")
        every{ unverifiedUserRepository.save<UnverifiedUserEntity>(allAny())} returns UnverifiedUserEntity(email="", verificationId = "", password = "")

        val result = service.createUser(LoginResource(email = "test", password = "test"))

        Assert.assertEquals(ResultMessage.RESEND_VERIFICATION_EMAIL, result)
        verify(exactly = 1){ unverifiedUserRepository.save<UnverifiedUserEntity>(allAny()) }
    }
}