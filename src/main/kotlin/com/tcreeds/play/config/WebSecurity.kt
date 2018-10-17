package com.tcreeds.play.config

import com.tcreeds.play.rest.JWTAuthenticationFilter
import com.tcreeds.play.rest.JWTAuthorizationFilter
import com.tcreeds.play.rest.SecurityUtils.HEADER_STRING
import com.tcreeds.play.rest.SecurityUtils.SIGN_UP_URL
import com.tcreeds.play.rest.SecurityUtils.LOGIN_URL
import com.tcreeds.play.rest.SecurityUtils.PROFILE_URL
import com.tcreeds.play.rest.SecurityUtils.RESET_PASSWORD_URL
import com.tcreeds.play.rest.SecurityUtils.SEND_RESET_URL
import com.tcreeds.play.rest.SecurityUtils.VERIFY_URL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurity(
        @Autowired
        var userDetailsService: UserDetailsService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL, LOGIN_URL, VERIFY_URL, SEND_RESET_URL, RESET_PASSWORD_URL).permitAll()
                .antMatchers("/users/mockAccount").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager()))
                .addFilter(JWTAuthorizationFilter(authenticationManager()))
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(BCryptPasswordEncoder())
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cors: CorsConfiguration = CorsConfiguration()
        cors.applyPermitDefaultValues()
        cors.addAllowedOrigin("*")
        cors.addExposedHeader(HEADER_STRING)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", cors)
        return source
    }
}