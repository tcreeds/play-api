package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class VerificationResource(

        @JsonProperty
        val email: String,

        @JsonProperty
        val verificationId: String
)