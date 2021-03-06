package com.tcreeds.play.service

import com.tcreeds.play.repository.CommunityRepository
import com.tcreeds.play.repository.PostRepository
import com.tcreeds.play.repository.UserRepository
import com.tcreeds.play.repository.entity.PostEntity
import com.tcreeds.play.rest.ResultMessage
import com.tcreeds.play.rest.resources.PostResource
import com.tcreeds.play.rest.resources.UserResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostService(
        @Autowired
        val postRepository: PostRepository,

        @Autowired
        val userRepository: UserRepository,

        @Autowired
        val communityRepository: CommunityRepository
) {

    fun getPosts(communityId: Long): List<PostResource> {
        return postRepository.findByCommunityCommunityId(communityId).map{
            PostResource(
                    id = it.id!!,
                    user = UserResource(
                            id = it.user.userId,
                            displayName =  it.user.displayName),
                    community = it.community.communityId,
                    content = it.content )
        }
    }

    fun addPost(email: String, communityId: Long, content: String): ResultMessage {

        val user = userRepository.findByEmail(email) ?: return ResultMessage.USER_NOT_FOUND
        val community = communityRepository.findByCommunityId(communityId) ?: return ResultMessage.COMMUNITY_NOT_FOUND
        postRepository.save(PostEntity(
                user = user,
                community = community,
                content = content
        ))
        return ResultMessage.SUCCESS
    }
}