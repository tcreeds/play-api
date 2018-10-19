package com.tcreeds.play.rest.resources

data class PostResource (
     val id: Long?,
     val user: UserResource?,
     val community: Long,
     val content: String
)