package com.kotlinspring.course_catalog.courses.repository

import com.kotlinspring.course_catalog.courses.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CourseRepository : JpaRepository<Course, Int>, JpaSpecificationExecutor<Course>