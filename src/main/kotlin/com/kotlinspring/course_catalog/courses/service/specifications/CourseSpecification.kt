package com.kotlinspring.course_catalog.courses.service.specifications

import com.kotlinspring.course_catalog.courses.entity.Course
import com.kotlinspring.course_catalog.courses.entity.Course_
import org.springframework.data.jpa.domain.Specification

object CourseSpecification {
    fun hasNameLike(courseName: String): Specification<Course> {
        return Specification { root, query, builder ->
            val courseNameInDb = builder.lower(root.get(Course_.name))

            val searchCourseName = "%${courseName.lowercase()}%"

            builder.like(courseNameInDb, searchCourseName)
        }
    }

    fun hasCategoryLike(categoryName: String): Specification<Course> {
        return Specification {root, query, builder ->
            val categoryInDb = builder.lower(root.get(Course_.category))

            val searchCategory = "%${categoryName.lowercase()}%"

            builder.like(categoryInDb, searchCategory)
        }
    }
}