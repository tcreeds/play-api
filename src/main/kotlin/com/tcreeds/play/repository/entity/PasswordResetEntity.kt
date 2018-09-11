package com.tcreeds.play.repository.entity

import javax.persistence.*

@Entity
@Table(name="password_reset")
data class PasswordResetEntity (

        @Id
        var userId: Long = 0,

        @Column(unique = true)
        var email: String,

        @Column(unique = true)
        var resetId: String

)