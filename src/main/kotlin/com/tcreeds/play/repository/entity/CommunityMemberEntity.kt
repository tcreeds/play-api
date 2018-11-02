package com.tcreeds.play.repository.entity

import javax.persistence.*

@Entity
@Table(name = "user_community")
data class CommunityMemberEntity(
        @EmbeddedId
        var id: CommunityMemberEntityId,

        @MapsId("user_id")
        @ManyToOne(fetch = FetchType.LAZY)
        var user: UserEntity,

        @MapsId("community_id")
        @ManyToOne(fetch = FetchType.LAZY)
        var community: CommunityEntity,

        @Column(name = "member_type")
        var memberType: String
) {
    override fun toString(): String {
        return ("CommunityMemberEntity(id=$id, user=${user.userId}, community=${community.communityId}, memberType=$memberType)")
    }

    companion object {

        fun createEntityId(user: UserEntity, community: CommunityEntity): CommunityMemberEntityId{
            return CommunityMemberEntityId(userId = user.userId, communityId = community.communityId)
        }

    }
}