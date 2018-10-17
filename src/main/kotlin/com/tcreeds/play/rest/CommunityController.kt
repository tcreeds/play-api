package com.tcreeds.play.rest

import com.tcreeds.play.rest.resources.CommunityMemberResource
import com.tcreeds.play.rest.resources.CommunityResource
import com.tcreeds.play.rest.resources.UserResource
import com.tcreeds.play.service.CommunityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(value = "/communities")
class CommunityController(
    @Autowired
    val communityService: CommunityService
){

    @PostMapping(value="/add")
    fun newCommunity(authentication: Authentication, @Valid @RequestBody resource: CommunityResource, res: HttpServletResponse) : CommunityResource? {
        print(authentication.name)
        val community = communityService.addCommunity(resource.name, resource.description, authentication.name)
        if (community != null) {
            return CommunityResource.fromEntity(community)
        }
        else
            res.sendError(409)
        return null
    }

    @GetMapping
    fun listCommunities(): List<CommunityResource> {
        return communityService.getCommunities()
    }

    @GetMapping(value="/{id}")
    fun getCommunityWithMembers(@PathVariable id: Long): CommunityResource? {
        return communityService.getCommunityWithMembers(id) ?: null
    }

    @DeleteMapping(value="/{id}")
    fun deleteCommunity(@PathVariable id: Long): Boolean {
        return communityService.deleteCommunity(id)
    }

    @PostMapping(value="/members/add")
    fun addMember(@Valid @RequestBody resource: CommunityMemberResource, res: HttpServletResponse){
        if (!communityService.addMember(resource.userId, resource.communityId))
            res.sendError(409)
    }

    @PostMapping(value="/members/remove")
    fun removeMember(@Valid @RequestBody resource: CommunityMemberResource, res: HttpServletResponse){
        if (!communityService.removeMember(resource.userId, resource.communityId))
            res.sendError(401)
    }

    @GetMapping(value="/members")
    fun getMembers(@Valid @RequestBody resource: CommunityResource, res: HttpServletResponse): List<UserResource>{
        return communityService.getCommunityMembers(resource.id)
    }

}