package com.kotlinspring.course_catalog.courses.controller

import com.kotlinspring.course_catalog.courses.dto.CourseDTO
import com.kotlinspring.course_catalog.courses.service.CourseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/courses")
@Validated
class CourseController(val courseService: CourseService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addCourse(@RequestBody @Valid courseDTO: CourseDTO): CourseDTO {
        return courseService.addCourse(courseDTO)
    }

    @GetMapping
    fun getAllCourses(): List<CourseDTO> {
        return courseService.getAllCourses()
    }

    @PutMapping("/{courseId}")
    fun updateCourse(@PathVariable courseId: Int, @RequestBody @Valid courseDTO: CourseDTO): CourseDTO {
        return courseService.updateCourse(courseId,courseDTO)
    }

    @DeleteMapping("/{courseId}")
    fun deleteCourse(@PathVariable courseId: Int) {
        courseService.deleteCourse(courseId)
    }
}