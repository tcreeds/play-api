package com.tcreeds.play.rest

import com.tcreeds.play.rest.resources.*
import com.tcreeds.play.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

enum class ResultMessage(val message: String) {
    EMAIL_IN_USE("Email is already in use"),
    RESEND_VERIFICATION_EMAIL("Resent Verification Email"),
    SENT_VERIFICATION_EMAIL("Sent Verification Email"),

    VERIFIED_USER("Created Verified User"),
    VERIFICATION_ID_NOT_FOUND("Verification ID not associated with a user"),
    VERIFICATION_ID_MISMATCH("Verification ID did not match the email provided"),

    LOGIN_SUCCESS("Login successful"),
    LOGIN_FAILED("Login failed"),

    SENT_PASSWORD_RESET_EMAIL("Password reset email has been sent"),
    PASSWORD_RESET_EMAIL_FAILED("Password reset email failed to send"),

    PASSWORD_RESET_SUCCESSFUL("Password reset was successful"),
    INVALID_PASSWORD_RESET_ATTEMPT("Invalid password reset request"),

    DELETED_USER("Successfully deleted user"),
    USER_NOT_FOUND("Unable to find user"),
    UPDATED_PROFILE("Successfully updated profile")

}

@RestController
@RequestMapping(value = "/users")
class UserController(
    @Autowired
    val userService: UserService
){

    @PostMapping(value="/newuser")
    fun newUser(@Valid @RequestBody resource: LoginResource, res: HttpServletResponse) {
        val result = userService.createUser(resource)
        if (result != ResultMessage.EMAIL_IN_USE)
            generateTokenHeader(res, resource.email)
        else
            res.sendError(400, result.message)
    }

    @PostMapping(value="/verifyemail")
    fun verifyEmail(@Valid @RequestBody resource: VerificationResource, res: HttpServletResponse) {
        val result = userService.verifyUser(resource)
        if (result == ResultMessage.VERIFIED_USER)
            generateTokenHeader(res, resource.email)
        else
            res.sendError(400, result.message)
    }

    @PostMapping(value="/login")
    fun login(@Valid @RequestBody resource: LoginResource, res: HttpServletResponse): UserResource? {
        val result = userService.checkLogin(resource)
        if (result == ResultMessage.LOGIN_SUCCESS) {
            generateTokenHeader(res, resource.email)
            return userService.getUser(resource.email)
        }
        else
            res.sendError(401, result.message)
        return null
    }

    @GetMapping(value="/profile/{userId}")
    fun getProfile(@PathVariable(required=true) userId: Long, res: HttpServletResponse): ProfileResource? {
        val userProfile = userService.getProfile(userId)
        if (userProfile != null)
            return userProfile
        res.sendError(404, "Unable to retrieve user id")
        return null
    }

    @PostMapping(value="/profile")
    fun updateProfile(auth: Authentication, @Valid @RequestBody resource: ProfileResource, res: HttpServletResponse){
        val result = userService.updateProfile(resource, auth.name)
        if (result != ResultMessage.UPDATED_PROFILE)
            res.sendError(400, result.message)
    }

    @PostMapping(value="/generateresetcode")
    fun resetPasswordEmail(@Valid @RequestBody resource: UserResource, res: HttpServletResponse) {
        val result = userService.sendResetPasswordEmail(resource)
        if (result != ResultMessage.SENT_PASSWORD_RESET_EMAIL)
            res.sendError(400, result.message)
    }

    @PostMapping(value="/resetpassword")
    fun resetPassword(@Valid @RequestBody resource: PasswordResetResource, res: HttpServletResponse) {
        val result = userService.resetPassword(resource)
        if (result == ResultMessage.PASSWORD_RESET_SUCCESSFUL)
            generateTokenHeader(res, resource.email)
        else
            res.sendError(400, result.message)

    }

    @PostMapping(value="/delete")
    fun deleteUser(@Valid @RequestBody resource: UserResource, res: HttpServletResponse) {
        val result = userService.deleteUser(resource)
        if (result != ResultMessage.DELETED_USER)
            res.sendError(500, result.message)
    }

    @PostMapping(value="/mockAccount")
    fun mockAccount() {
        userService.mockUser("test", "test")
    }

    private fun generateTokenHeader(res: HttpServletResponse, email: String) {
        res.addHeader(SecurityUtils.HEADER_STRING, SecurityUtils.TOKEN_PREFIX + SecurityUtils.generateToken(email))
    }
}