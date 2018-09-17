package com.tcreeds.play.repository.entity

import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name="unverified_users")
data class UnverifiedUserEntity (

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @Column(name="user_id")
        var userId: Long = 0,

        @Column(unique = true)
        var email: String,

        @Column(unique = true, name="verification_id")
        var verificationId: String,

        @Column
        var password: String

)