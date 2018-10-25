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

        @OneToMany(
                mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true
        )
        var members: MutableList<CommunityMemberEntity> = mutableListOf()

)