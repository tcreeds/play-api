package com.tcreeds.play.rest

import com.tcreeds.play.rest.resources.PostResource
import com.tcreeds.play.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping(value = "/posts")
class PostController(
        @Autowired
        val postService: PostService
) {

    @GetMapping(value="/{communityId}")
    fun getPostsForCommunity(@PathVariable communityId: Long): List<PostResource> {
        return postService.getPosts(communityId)
    }

    @PostMapping()
    fun addPost(auth: Authentication, @Valid @RequestBody resource: PostResource, res: HttpServletResponse) {
        postService.addPost(auth.name, resource.community, resource.content)
    }
}