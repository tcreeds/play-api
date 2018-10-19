package com.tcreeds.play.repository.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "posts")
data class PostEntity (

        @Id
        @Column
        var id: Long? = 0,

        @ManyToOne
        var community: CommunityEntity,

        @ManyToOne
        var user: UserEntity,

        @Column
        var content: String
)