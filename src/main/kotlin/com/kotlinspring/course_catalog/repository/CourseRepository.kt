package com.kotlinspring.course_catalog.repository

import com.kotlinspring.course_catalog.entity.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
}