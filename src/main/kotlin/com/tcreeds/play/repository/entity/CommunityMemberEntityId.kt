package com.tcreeds.play.repository.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class CommunityMemberEntityId(

        @Column(name = "user_id")
        var userId: Long,

        @Column(name = "community_id")
        var communityId: Long

) : Serializable