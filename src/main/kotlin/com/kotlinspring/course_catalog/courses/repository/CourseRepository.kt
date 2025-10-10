package com.kotlinspring.course_catalog.courses.repository

import com.kotlinspring.course_catalog.courses.entity.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Int> {
}