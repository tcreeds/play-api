package com.tcreeds.play.rest.resources

import com.fasterxml.jackson.annotation.JsonProperty
import com.tcreeds.play.repository.entity.UserEntity

data class UserResource(

        @JsonProperty
        val id: Long = 0,

        @JsonProperty
        val email: String = "",

        @JsonProperty
        val password: String = "",

        @JsonProperty
        val verificationId: String = ""
) {
        companion object {
            fun fromEntity(entity: UserEntity): UserResource{
                    return UserResource(id = entity.userId, email = entity.email)
            }
        }
}