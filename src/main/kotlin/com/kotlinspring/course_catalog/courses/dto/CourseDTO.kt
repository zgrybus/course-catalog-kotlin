package com.kotlinspring.course_catalog.courses.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class CourseDTO(
    val id: Int?,
    @get:NotBlank(message = "Name is required")
    @get:Length(min = 1, message = "Name cannot be empty")
    val name: String?,
    @get:NotBlank(message = "Description is required")
    @get:Length(min = 1, message = "Description cannot be empty")
    val category: String?
)