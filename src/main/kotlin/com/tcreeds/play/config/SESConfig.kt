package com.tcreeds.play.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService



@Configuration
open class SESConfig{

    @Bean
    open fun amazonSES(): AmazonSimpleEmailService {
        return AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build()
    }
}