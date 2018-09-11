package com.tcreeds.play.repository.entity

import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name="users")
data class UserEntity (

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var userId: Long = 0,

        @Column(unique = true)
        var email: String = "",

        @Column
        var password: String = "",

        @ManyToMany
        var communities: MutableList<CommunityEntity> = mutableListOf()

)