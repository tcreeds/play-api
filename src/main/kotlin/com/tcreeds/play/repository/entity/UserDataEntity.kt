package com.tcreeds.play.repository.entity

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName="play")
class UserDataEntity (

        @DynamoDBHashKey
        var email: String = "",

        @DynamoDBAttribute
        var verificationId: String = "",

        @DynamoDBAttribute
        var password: String = ""


) {
    companion object {
        fun isVerifiedUser(userDataEntity: UserDataEntity): Boolean {
            return userDataEntity.password != ""
        }
    }
}
