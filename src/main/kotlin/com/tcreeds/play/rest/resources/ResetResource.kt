package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class PasswordResetResource (

        @JsonProperty
        val resetId: String,

        @JsonProperty
        val email: String,

        @JsonProperty
        val password: String
)