package com.example.iam.repository

import com.example.iam.domain.Resource
import org.springframework.data.jpa.repository.JpaRepository

interface ResourceRepository : JpaRepository<Resource, Long>
