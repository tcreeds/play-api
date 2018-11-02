package com.tcreeds.play.service.email

import org.springframework.stereotype.Service

@Service
interface EmailClient {

    fun sendEmail(destination: String, subject: String, body: String)
}