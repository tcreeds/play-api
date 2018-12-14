package com.tcreeds.play.repository.entity

import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name="users")
data class UserEntity (

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name="user_id")
        var userId: String = "",

        @Column(unique = true)
        var email: String = "",

        @Column(name="display_name")
        var displayName: String? = "",

        @Column
        var password: String = "",

        @OneToMany(
                mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true
        )
        var communities: MutableList<CommunityMemberEntity> = mutableListOf(),

        @Column(columnDefinition = "text")
        var bio: String? = ""

)