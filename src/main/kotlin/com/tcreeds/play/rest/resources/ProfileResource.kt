package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileResource (

        @JsonProperty
        val username: String,

        @JsonProperty
        val bio: String
)