package com.tcreeds.play.service

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.tcreeds.play.repository.CommunityRepository
import com.tcreeds.play.repository.UserRepository
import com.tcreeds.play.repository.entity.CommunityEntity
import com.tcreeds.play.repository.entity.CommunityMemberEntity
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
    fun addCommunity(name: String, description: String, email: String): CommunityResource? {
        val entity = CommunityEntity(name = name, description = description)
        val user = userRepository.findByEmail(email)

        if (user != null){

            var savedEntity = communityRepository.save(entity)
            entity.members.add(CommunityMemberEntity(
                    id = CommunityMemberEntity.createEntityId(user, savedEntity),
                    user = user,
                    community = savedEntity,
                    memberType = "Admin"))
            userRepository.save(user)
            savedEntity = communityRepository.save(savedEntity)
            println(savedEntity)

            return CommunityResource(
                    id = savedEntity.communityId,
                    name = savedEntity.name,
                    description = savedEntity.description,
                    members = savedEntity.members.map{ UserResource(id = it.user.userId, displayName = it.user.displayName)}
            )
        }
        throw Exception("Couldn't find user by email $email")
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
            community.members.add(CommunityMemberEntity(
                    id = CommunityMemberEntity.createEntityId(user, community),
                    user = user,
                    community = community,
                    memberType = "Member"))
            communityRepository.save(community)
            return true
        }
        return false
    }

    fun addMember(email: String, communityId: Long): Boolean {
        val user: UserEntity? = userRepository.findByEmail(email)
        val community: CommunityEntity? = communityRepository.findByCommunityId(communityId)
        if (user != null && community != null){
            community.members.add(CommunityMemberEntity(
                    id = CommunityMemberEntity.createEntityId(user, community),
                    user = user,
                    community = community,
                    memberType = "Member"))
            communityRepository.save(community)
            return true
        }
        return false
    }

    fun removeMember(userId: Long, communityId: Long): Boolean {
        val community: CommunityEntity? = communityRepository.findByCommunityId(communityId)
        if (community != null){
            community.members = community.members.filter { it.user.userId != userId }.toMutableList()
            communityRepository.save(community)
            return true
        }
        return false
    }

    fun getCommunityWithMembers(communityId: Long): CommunityResource? {
        val entity = communityRepository.findByCommunityId(communityId)
        if (entity != null) {
            println(entity)
            val resource = CommunityResource(
                    id = entity.communityId,
                    name = entity.name,
                    description = entity.description,
                    admins = entity.members.map{ UserResource(id = it.user.userId, displayName = it.user.displayName)},
                    members = entity.members.map{ UserResource(
                            id = it.user.userId,
                            displayName = it.user.displayName
                    )})
            return resource
        }
        return null
    }

    fun getCommunityMembers(communityId: Long): List<UserResource> {
        return communityRepository.findByCommunityId(communityId)?.members?.map{ UserResource(
                id = it.user.userId,
                displayName = it.user.displayName
        ) }?.toList() ?: listOf()
    }
}