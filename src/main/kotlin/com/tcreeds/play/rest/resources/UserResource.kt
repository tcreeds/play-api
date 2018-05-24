package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class UserResource(

        @JsonProperty
        val email: String = "",

        @JsonProperty
        val password: String = "",

        @JsonProperty
        val verificationId: String = ""
)