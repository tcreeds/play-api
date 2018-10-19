package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.PostEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : CrudRepository<PostEntity, Long> {

    fun findByCommunityCommunityId(communityId: Long): List<PostEntity>
}