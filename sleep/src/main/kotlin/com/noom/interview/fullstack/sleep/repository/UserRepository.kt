package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}