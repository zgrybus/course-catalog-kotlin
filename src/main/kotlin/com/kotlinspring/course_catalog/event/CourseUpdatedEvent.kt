package com.kotlinspring.course_catalog.event

import com.kotlinspring.course_catalog.dto.CourseDTO

data class CourseUpdatedEvent(val courseDTO: CourseDTO)