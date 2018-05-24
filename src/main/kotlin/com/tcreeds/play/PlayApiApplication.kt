package com.tcreeds.play

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableConfigurationProperties
@EnableWebSecurity
open class PlayApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(PlayApiApplication::class.java, *args)
}
