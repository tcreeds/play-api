package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResource(

        @JsonProperty
        val email: String,

        @JsonProperty
        val password: String
)