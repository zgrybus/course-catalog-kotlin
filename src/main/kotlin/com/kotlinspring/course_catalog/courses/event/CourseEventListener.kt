package com.kotlinspring.course_catalog.courses.event

import com.kotlinspring.course_catalog.Loggable
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class CourseEventListener : Loggable {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleCourseUpdate(event: CourseUpdatedEvent) {
        logger.info { "Course updated: ${event.courseDTO}" }
    }
}