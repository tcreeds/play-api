package com.tcreeds.play.service.email

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!local")
class EmailClientOnline(

        @Autowired
        val ses: AmazonSimpleEmailService

) : EmailClient {

    override fun sendEmail(destination: String, subject: String, body: String) {
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
        ses.sendEmail(request)
    }
}

