package com.tcreeds.play.repository

import com.tcreeds.play.repository.entity.UserEntity
import com.tcreeds.play.rest.resources.CommunityMemberResource
import com.tcreeds.play.rest.resources.CommunityResource
import com.tcreeds.play.rest.resources.ProfileResource
import com.tcreeds.play.rest.resources.UserResource
import github.etx.neo4j.DefaultNeoSerializer
import github.etx.neo4j.NeoLogging
import github.etx.neo4j.NeoQuery
import github.etx.neo4j.destruct
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Config
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GraphClient(

        @Value("\${neo4j.url}")
        val url: String,

        @Value("\${neo4j.username}")
        val username: String,

        @Value("\${neo4j.password}")
        val password: String
) {


    val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var neo: NeoQuery

    init {
        val driver: Driver = GraphDatabase.driver(
                url,
                AuthTokens.basic(username, password),
                Config.build().withLogging(NeoLogging(logger)).toConfig())
        neo = NeoQuery(driver, DefaultNeoSerializer())
    }

    fun insertUser(id: String, displayName: String, email: String) {
        neo.submit("CREATE (u:User { id: {id}, displayName: { displayName }, email: { email } })-[:HAS_PROFILE]->(p:Profile) RETURN u", mapOf(
                "id" to id,
                "displayName" to displayName,
                "email" to email))
    }

    fun insertCommunity(id: String, name: String, description: String){
        neo.submit("CREATE (c:Community { id: {id}, name: {name}, description: { description } }) RETURN c", mapOf(
                "id" to id,
                "name" to name,
                "description" to description))
    }

    fun addUserToCommunity(userId: String, communityId: String) {
        val match = "MATCH (t:Community { id: { communityId } })-[:HAS_MEMBER]->(m:User),(u:User { id: {userId} }) "
        val merge = "MERGE (u)-[:MEMBER_OF]->(c)-[:HAS_MEMBER]->(u) "
        val get = "RETURN c,m"
        neo.submit("$match $merge", mapOf("userId" to userId, "communityId" to communityId).destruct())
                .unwrap("c")
                .let{
                    CommunityResource(
                            id = it.string("id"),
                            name =
                    )
                }
    }

    fun getUser(id: String): UserResource {
        val match = "MATCH (u:User { id: { userId } }) RETURN u"
        return neo.submit(match, mapOf("id" to id))
                .unwrap("u")
                .let {
                    UserResource(
                            id = it.string("id"),
                            displayName = it.string("displayName"))
                }
    }

    fun getUserProfile(id: String): ProfileResource {
        val match = "MATCH (c:Community)-[:HAS_MEMBER]->(u:User { id: { userId } })-[:HAS_PROFILE]->(p:Profile) RETURN u,p,c"
        return neo.submit(match, mapOf("id" to id))
                .let {
                    val user = it.unwrap("u")
                    val profile = it.unwrap("p")
                    val communities = it.unwrap("c")
                    ProfileResource(
                            username = user.string("displayName"),
                            bio = profile.string("bio"),
                            communities = communities
                                    .asSequence()
                                    .asIterable()
                                    .map { c -> CommunityResource(
                                            id = c.string("id"),
                                            name = c.string("name")) })
                }
    }

    fun getUserCommunities(userId: String): List<CommunityResource> {
        val match = "MATCH (u:User { id: { userId } })-[:MEMBER_OF]->(c:Community) RETURN c"
        return neo.submit(match, mapOf("userId" to userId).destruct())
                .map { it.unwrap("t") }
                .map { CommunityResource(
                        id = it.string("id"),
                        name = it.string("name"),
                        description = it.string("description"))}
                .toList()
    }

    fun getCommunityMembers(name: String): List<UserResource> {
        val match = "MATCH (c:Community { name: { name } })-[:HAS_MEMBER]->(u:User) RETURN u"
        return neo.submit(match, mapOf("name" to name))
                .map { it.unwrap("u") }
                .map { UserResource(
                        id = it.string("id"),
                        displayName = it.string("displayName"))}
                .toList()

    }
}