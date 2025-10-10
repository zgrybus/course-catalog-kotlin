package com.kotlinspring.course_catalog.courses.event

import com.kotlinspring.course_catalog.courses.dto.CourseDTO

data class CourseUpdatedEvent(val courseDTO: CourseDTO)