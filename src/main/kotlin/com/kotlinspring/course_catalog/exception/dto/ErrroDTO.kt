package com.kotlinspring.course_catalog.exception.dto

enum class ErrorType {
    NOT_FOUND,
    VALIDATION_ERROR,
    SOMETHING_WENT_WRONG
}

data class ErrorDTO(
    val type: ErrorType,
    val message: String
)

data class ErrorResponse(
    val path: String,
    val status: Int,
    val errors: List<ErrorDTO>
)