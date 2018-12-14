package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.CommunityEntity
import com.tcreeds.play.repository.entity.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommunityRepository : CrudRepository<CommunityEntity, String> {
    fun findByCommunityId(id: String): CommunityEntity?
}