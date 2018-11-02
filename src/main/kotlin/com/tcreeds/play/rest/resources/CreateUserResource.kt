package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUserResource(

        @JsonProperty
        val email: String = "",

        @JsonProperty
        val displayName: String = "",

        @JsonProperty
        val password: String = ""
)