package com.kotlinspring.course_catalog.exception

import com.kotlinspring.course_catalog.Loggable
import com.kotlinspring.course_catalog.exception.dto.ErrorDTO
import com.kotlinspring.course_catalog.exception.dto.ErrorResponse
import com.kotlinspring.course_catalog.exception.dto.ErrorType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler : Loggable {

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error { "An exception occurred: $exception" }

        val errorDTO = ErrorDTO(ErrorType.SOMETHING_WENT_WRONG, "An exception occurred")
        val errorResponse = ErrorResponse(
            errors = listOf(errorDTO),
            path = request.getDescription(false),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}