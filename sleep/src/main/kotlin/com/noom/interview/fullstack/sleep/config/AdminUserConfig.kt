package com.noom.interview.fullstack.sleep.config

import com.noom.interview.fullstack.sleep.model.User
import com.noom.interview.fullstack.sleep.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AdminUserConfig(
    val userRepository: UserRepository
) {
    @Bean
    fun adminUser(): User = userRepository.findByUsername("admin") ?: userRepository.save(
        User.builder()
            .username("admin")
            .password("admin")
            .build()
    )
}