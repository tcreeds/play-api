package com.tcreeds.play.repository.entity

import javax.persistence.*

@Entity
@Table(name="password_reset")
data class PasswordResetEntity (

        @Id
        @Column(name="user_id")
        var userId: String,

        @Column(unique = true)
        var email: String,

        @Column(unique = true, name="reset_id")
        var resetId: String

)