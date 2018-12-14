package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank

data class CommunityMemberResource(

        @JsonProperty
        val userId: String,

        @JsonProperty
        val communityId: String
)