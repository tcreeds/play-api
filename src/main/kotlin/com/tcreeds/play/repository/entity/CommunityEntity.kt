package com.tcreeds.play.repository.entity

import javax.persistence.*


@Entity
@Table(name="communities")
data class CommunityEntity (

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name="community_id")
        val communityId: Long = 0,

        @Column
        var name: String = "",

        @Column
        var description: String = "",


        @ManyToMany(mappedBy = "communities")
        var members: MutableList<UserEntity> = mutableListOf()

)