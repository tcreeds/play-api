package com.tcreeds.play.service

import com.tcreeds.play.repository.UserDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import java.util.Collections.emptyList
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(
        @Autowired
        private val repository: UserDataRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = repository.findByEmail(email)
        return User(user?.email, user?.password, emptyList())
    }
}