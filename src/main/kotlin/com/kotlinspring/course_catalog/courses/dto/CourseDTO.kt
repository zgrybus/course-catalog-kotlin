package com.kotlinspring.course_catalog.courses.dto

import jakarta.validation.constraints.NotBlank

data class CourseDTO(
    val id: Int?,
    @get:NotBlank(message = "Name is required")
    val name: String?,
    @get:NotBlank(message = "Category is required")
    val category: String?
)