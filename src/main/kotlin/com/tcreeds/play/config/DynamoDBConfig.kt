package com.tcreeds.play.config

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import org.springframework.context.annotation.Bean
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@EnableDynamoDBRepositories(basePackages= ["com.tcreeds.play.repository"])
open class DynamoDBConfig {

    @Value("\${aws.dynamodb.endpoint}")
    private val amazonDynamoDBEndpoint: String? = null

    @Bean
    @Profile("dev")
    open fun amazonDynamoDB(): AmazonDynamoDB {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build()
    }

    @Bean(name=["amazonDynamoDB"])
    @Profile("local")
    open fun amazonDynamoDBLocal(): AmazonDynamoDB {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:8010", "us-east-1"))
                .build()
    }

}