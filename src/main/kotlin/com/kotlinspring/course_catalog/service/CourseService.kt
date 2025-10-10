package com.kotlinspring.course_catalog.service

import com.kotlinspring.course_catalog.Loggable
import com.kotlinspring.course_catalog.dto.CourseDTO
import com.kotlinspring.course_catalog.entity.Course
import com.kotlinspring.course_catalog.event.CourseUpdatedEvent
import com.kotlinspring.course_catalog.exceptions.CourseNotFoundException
import com.kotlinspring.course_catalog.repository.CourseRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(val courseRepository: CourseRepository, val eventPublisher: ApplicationEventPublisher) : Loggable {
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

    fun getAllCourses(): List<CourseDTO> {
        logger.info { "Attempt to get all courses" }

        return courseRepository
            .findAll()
            .also { logger.info { "Successfully retrieved ${it.count()} courses" } }
            .map { CourseDTO(id = it.id, name = it.name, category =  it.category) }
    }

    @Transactional
    fun updateCourse(courseId: Int, courseDTO: CourseDTO): CourseDTO {
        logger.info { "Attempt to update course $courseDTO" }

        return courseRepository
            .findById(courseId)
            .orElseThrow {
                CourseNotFoundException(courseId.toString())
            }
            .apply {
                name = courseDTO.name
                category = courseDTO.category
            }
            .let {
                CourseDTO(id = it.id, name = it.name, category =  it.category)
            }
            .also {
                eventPublisher.publishEvent(CourseUpdatedEvent(it))
            }
    }
}