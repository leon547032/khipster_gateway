package com.purpleio.gateway.repository

import com.purpleio.gateway.domain.Authority

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
