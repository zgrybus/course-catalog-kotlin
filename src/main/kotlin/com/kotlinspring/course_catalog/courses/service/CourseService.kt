package com.kotlinspring.course_catalog.courses.service

import com.kotlinspring.course_catalog.Loggable
import com.kotlinspring.course_catalog.courses.dto.CourseDTO
import com.kotlinspring.course_catalog.courses.entity.Course
import com.kotlinspring.course_catalog.courses.event.CourseUpdatedEvent
import com.kotlinspring.course_catalog.courses.exception.CourseNotFoundException
import com.kotlinspring.course_catalog.courses.repository.CourseRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(val courseRepository: CourseRepository, val eventPublisher: ApplicationEventPublisher) : Loggable {

    @Transactional
    fun addCourse(courseDTO: CourseDTO): CourseDTO {
        logger.info { "Attempt to add new Course: $courseDTO" }

        val courseEntity = courseDTO.let {
            Course(id = null, name = it.name, category = it.category)
        }


        return courseRepository.save(courseEntity)
            .also {
                logger.info { "Saved course $it" }
            }
            .let {
                CourseDTO(id = it.id, name = it.name, category = it.category)
            }
    }

    @Transactional(readOnly = true)
    fun getAllCourses(): List<CourseDTO> {
        logger.info { "Attempt to get all courses" }

        return courseRepository
            .findAll()
            .also { logger.info { "Successfully retrieved ${it.count()} courses" } }
            .map { CourseDTO(id = it.id, name = it.name, category = it.category) }
    }

    @Transactional
    fun updateCourse(courseId: Int, courseDTO: CourseDTO): CourseDTO {
        logger.info { "Attempt to update course $courseDTO with $courseId id" }

        return courseRepository
            .findById(courseId)
            .orElseThrow {
                CourseNotFoundException("Course with id $courseId not found")
            }
            .apply {
                name = courseDTO.name
                category = courseDTO.category
            }
            .let {
                CourseDTO(id = it.id, name = it.name, category = it.category)
            }
            .also {
                eventPublisher.publishEvent(CourseUpdatedEvent(it))
            }
    }

    @Transactional
    fun deleteCourse(courseId: Int) {
        logger.info { "Attempt to delete course with id $courseId" }

        courseRepository
            .findById(courseId)
            .orElseThrow {
                CourseNotFoundException("Course with id $courseId not found")
            }
            .also {
                courseRepository.delete(it)
                logger.info { "Successfully deleted course $it" }
            }
    }
}