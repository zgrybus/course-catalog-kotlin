package com.kotlinspring.course_catalog.courses.exception

import com.kotlinspring.course_catalog.courses.controller.CourseController
import com.kotlinspring.course_catalog.exception.GlobalExceptionHandler
import com.kotlinspring.course_catalog.exception.dto.ErrorDTO
import com.kotlinspring.course_catalog.exception.dto.ErrorResponse
import com.kotlinspring.course_catalog.exception.dto.ErrorType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice(assignableTypes = [CourseController::class])
class CourseExceptionHandler : GlobalExceptionHandler() {

    @ExceptionHandler(CourseNotFoundException::class)
    fun handleCourseNotFound(exception: CourseNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error { "Course not found from CourseController: ${exception.message}" }

        val errorDTO = ErrorDTO(ErrorType.NOT_FOUND, exception.message ?: "Course not found")
        val errorResponse = ErrorResponse(
            errors = listOf(errorDTO),
            status = HttpStatus.NOT_FOUND.value(),
            path = request.getDescription(false)
        )

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleCourseNotValid(exception: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ErrorResponse> {
        logger.error { "Course not valid: $exception" }

        val listOfErrorsDTO = exception.fieldErrors.map {
            error ->
                ErrorDTO(ErrorType.VALIDATION_ERROR, error.defaultMessage ?: "Invalid value for ${error.field}")
        }
        val errorResponse = ErrorResponse(
            errors = listOfErrorsDTO,
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.getDescription(false)
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}