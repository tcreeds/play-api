package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty
import com.tcreeds.play.repository.entity.CommunityEntity

data class CommunityResource(

        @JsonProperty
        val id: Long = 0,

        @JsonProperty
        val name: String = "",

        @JsonProperty
        val description: String = "",

        @JsonProperty
        val members: List<UserResource> = listOf()
) {
        companion object {
            fun fromEntity(entity: CommunityEntity): CommunityResource {
                return CommunityResource(id = entity.communityId, name = entity.name, description = entity.description)
            }
        }
}