package com.tcreeds.play.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.tcreeds.play.repository.CommunityRepository
import com.tcreeds.play.repository.UserRepository
import com.tcreeds.play.repository.entity.CommunityEntity
import com.tcreeds.play.repository.entity.UserEntity
import com.tcreeds.play.rest.resources.CommunityResource
import com.tcreeds.play.rest.resources.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommunityService(

        @Autowired
        val communityRepository: CommunityRepository,

        @Autowired
        val userRepository: UserRepository,

        @Autowired
        val amazonSES: AmazonSimpleEmailService
){
    fun addCommunity(name: String, description: String, email: String): CommunityEntity? {
        val entity: CommunityEntity = CommunityEntity(name = name, description = description)
        val savedEntity = communityRepository.save(entity)
        val user = userRepository.findByEmail(email)
        if (user != null){
            user.communities.add(entity)
            userRepository.save(user)
        }
        return savedEntity
    }

    fun deleteCommunity(id: Long): Boolean {
        val entity = communityRepository.findByCommunityId(id)
        if (entity != null){
            communityRepository.delete(entity)
            return true
        }
        return false
    }

    fun getCommunities() : List<CommunityResource> {
        return communityRepository.findAll().toList().map { CommunityResource.fromEntity(it) }
    }

    fun addMember(userId: Long, communityId: Long): Boolean {
        val user: UserEntity? = userRepository.findByUserId(userId)
        val community: CommunityEntity? = communityRepository.findByCommunityId(communityId)
        if (user != null && community != null){
            community.members.add(user)
            communityRepository.save(community)
            return true
        }
        return false
    }

    fun addMember(email: String, communityId: Long): Boolean {
        val user: UserEntity? = userRepository.findByEmail(email)
        val community: CommunityEntity? = communityRepository.findByCommunityId(communityId)
        if (user != null && community != null){
            community.members.add(user)
            communityRepository.save(community)
            return true
        }
        return false
    }

    fun removeMember(userId: Long, communityId: Long): Boolean {
        val community: CommunityEntity? = communityRepository.findByCommunityId(communityId)
        if (community != null){
            community.members = community.members.filter { it.userId != userId }.toMutableList()
            communityRepository.save(community)
            return true
        }
        return false
    }

    fun getCommunityWithMembers(communityId: Long): CommunityResource? {
        val entity = communityRepository.findByCommunityId(communityId)
        if (entity != null) {
            val resource = CommunityResource(
                    id = entity.communityId,
                    name = entity.name,
                    description = entity.description,
                    members = entity.members.map{ UserResource(
                            id = it.userId,
                            displayName = it.displayName
                    )})
            return resource
        }
        return null
    }

    fun getCommunityMembers(communityId: Long): List<UserResource> {
        return communityRepository.findByCommunityId(communityId)?.members?.map{ UserResource(
                id = it.userId,
                displayName = it.displayName
        ) }?.toList() ?: listOf()
    }
}