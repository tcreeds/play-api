package com.tcreeds.play.service.email

interface EmailClient {

    fun sendEmail(destination: String, subject: String, body: String)
}