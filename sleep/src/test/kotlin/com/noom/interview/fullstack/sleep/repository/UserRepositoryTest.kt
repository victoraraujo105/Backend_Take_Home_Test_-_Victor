package com.noom.interview.fullstack.sleep.repository

import com.noom.interview.fullstack.sleep.model.User
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.saveAll(listOf(
            User.builder()
                .username("test1")
                .password("test1")
            .build(),
            User.builder()
                .username("test2")
                .password("test2")
            .build()
        ))
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun findByUsername_WhenUserExists_ReturnsUser() {
        val user = userRepository.findByUsername("test1")
        assertThat(user).isNotNull
        assertThat(user!!.username).isEqualTo("test1")
    }

    @Test
    fun findByUsername_WhenUserDoesNotExist_ReturnsNull() {
        val user = userRepository.findByUsername("test3")
        assertThat(user).isNull()
    }

    @Test
    fun createUser_WhenUserDoesNotExist_CreatesUser() {
        val user = User.builder()
            .username("test3")
            .password("test3")
        .build()
        userRepository.save(user)
        val savedUser = userRepository.findByUsername("test3")
        assertThat(savedUser).isNotNull
        assertThat(savedUser!!.username).isEqualTo("test3")
    }

    @Test
    fun createUser_WhenUserExists_UpdatesUser() {
        val user = userRepository.findByUsername("test1")
        user!!.password = "newPassword"
        val updatedUser = userRepository.save(user)
        assertThat(updatedUser.password).isEqualTo("newPassword")
    }
}