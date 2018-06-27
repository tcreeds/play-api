package com.tcreeds.play.repository.entity

import javax.persistence.*


@Entity
@Table(name="users")
data class UserEntity (

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var userId: Long = 0,

        @Column(unique = true)
        var email: String = "",

        @Column(unique = true)
        var verificationId: String = "",

        @Column
        var verified: Boolean = false,

        @Column
        var password: String = "",

        @ManyToMany
        var communities: MutableList<CommunityEntity> = mutableListOf()

) {
    companion object {
        fun isVerifiedUser(userDataEntity: UserEntity): Boolean {
            return userDataEntity.verified
        }
    }
}
