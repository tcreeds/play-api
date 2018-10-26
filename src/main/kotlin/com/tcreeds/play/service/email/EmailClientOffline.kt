package com.tcreeds.play.service.email

import org.springframework.context.annotation.Profile

@Profile("local")
class EmailClientOffline : EmailClient{

    override fun sendEmail(destination: String, subject: String, body: String) {
        println("email destination: $destination")
        println("email subject: $subject")
        println("email body: $body")
    }
}