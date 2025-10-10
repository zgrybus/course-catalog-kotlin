package com.kotlinspring.course_catalog

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlinspring.course_catalog.courses.dto.CourseDTO
import com.kotlinspring.course_catalog.courses.entity.Course
import com.kotlinspring.course_catalog.courses.repository.CourseRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CourseControllerTest {
    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        courseRepository.deleteAll()
    }

    @Test
    internal fun addCourse() {
        val courseDTO = CourseDTO(id = null, name = "Typescript god", category = "Typescript")

        val result = mockMvc.post("/v1/courses") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(courseDTO)
        }.andExpect {
            status { isCreated() }
        }.andReturn()

        val responseBody = result.response.contentAsString
        val savedCourseDTO = objectMapper.readValue(responseBody, CourseDTO::class.java)

        Assertions.assertTrue {
            savedCourseDTO.id != null
            savedCourseDTO.name == "Typescript god"
            savedCourseDTO.category == "Typescript"
        }
    }

    @Test
    internal fun getAllCourses() {
        val savedCourses = courseRepository
            .saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Kotlin"),
            ))
            .map { CourseDTO(id = it.id, name = it.name, category = it.category) }

        val result = mockMvc.get("/v1/courses") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val responseBody = result.response.contentAsString
        val courses = objectMapper.readValue(responseBody, Array<CourseDTO>::class.java)

        Assertions.assertEquals(2, courses.size)
        Assertions.assertEquals(savedCourses, courses.toList())
    }
}