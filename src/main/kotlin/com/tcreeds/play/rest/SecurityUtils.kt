package com.tcreeds.play.rest

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.User
import java.util.*

object SecurityUtils {
    const val SECRET = "SecretKeyToGenJWTs"
    const val EXPIRATION_TIME: Long = 864000000 // 10 days
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
    const val SIGN_UP_URL = "/users/newuser"
    const val LOGIN_URL = "/users/login"
    const val VERIFY_URL = "/users/verifyemail"
    const val SEND_RESET_URL = "/users/generateresetcode"
    const val RESET_PASSWORD_URL = "/users/resetpassword"

    fun generateToken(username: String): String{
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET.toByteArray())
                .compact()
    }
}