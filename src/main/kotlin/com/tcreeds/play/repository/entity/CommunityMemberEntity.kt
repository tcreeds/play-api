package com.tcreeds.play.repository.entity

import javax.persistence.*

@Entity
@Table(name = "user_community")
data class CommunityMemberEntity(
        @EmbeddedId
        var id: CommunityMemberEntityId,

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("user_id")
        var user: UserEntity,

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("community_id")
        var community: CommunityEntity,

        @Column(name = "member_type")
        var memberType: String
) {
    companion object {

        fun createEntityId(user: UserEntity, community: CommunityEntity): CommunityMemberEntityId{
            return CommunityMemberEntityId(userId = user.userId, communityId = community.communityId)
        }

    }
}