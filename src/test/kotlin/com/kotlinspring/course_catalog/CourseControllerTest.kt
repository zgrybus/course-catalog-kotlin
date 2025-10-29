package com.kotlinspring.course_catalog

import com.fasterxml.jackson.databind.ObjectMapper
import com.kotlinspring.course_catalog.courses.dto.CourseDTO
import com.kotlinspring.course_catalog.courses.entity.Course
import com.kotlinspring.course_catalog.courses.repository.CourseRepository
import com.kotlinspring.course_catalog.exception.dto.ErrorResponse
import com.kotlinspring.course_catalog.exception.dto.ErrorType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DisplayName("CourseControllerTest")
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

    @Nested
    @DisplayName("POST /v1/courses")
    inner class AddCourseTests {
        @Test
        internal fun `should create a new course and return it`() {
            val courseDTO = CourseDTO(id = null, name = "Typescript god", category = "Typescript")

            val responseBody = mockMvc.post("/v1/courses") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(courseDTO)
            }.andExpect {
                status { isCreated() }
            }.andReturn().response.contentAsString

            val savedCourseDTO = objectMapper.readValue(responseBody, CourseDTO::class.java)
            assertNotNull(savedCourseDTO.id)
            assertEquals(courseDTO.name, savedCourseDTO.name)
            assertEquals(courseDTO.category, savedCourseDTO.category)

            val coursesInDb = courseRepository.findAll()
            assertEquals(1, coursesInDb.count())
            val courseInDb = coursesInDb.first()
            assertEquals(courseDTO.name, courseInDb.name)
            assertEquals(courseDTO.category, courseInDb.category)
        }
    }

    @Nested
    @DisplayName("GET /v1/course/{courseId}")
    inner class GetCourseByIdTests {
        @Test
        internal fun `should return an existing`() {
            val savedCourses = courseRepository.saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Kotlin"),
            ))

            val firstCourseId = savedCourses.first().id

            val responseBody = mockMvc.get("/v1/courses/$firstCourseId") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

            val course = objectMapper.readValue(responseBody, CourseDTO::class.java)
            assertNotNull(course)
            assertEquals("Typescript bez tajemnic", course.name)
            assertEquals("Typescript", course.category)
        }

        @Test
        internal fun `should return 404, when course does not exist`() {
            courseRepository.saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Kotlin"),
            ))

            val responseBody = mockMvc.get("/v1/courses/9999999") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isNotFound() }
            }.andReturn().response.contentAsString

            val errorResponse = objectMapper.readValue(responseBody, ErrorResponse::class.java)

            assertEquals(1, errorResponse.errors.size)
            assertEquals(ErrorType.NOT_FOUND, errorResponse.errors[0].type)
            assertEquals("Course with id 9999999 not found", errorResponse.errors[0].message)
        }
    }

    @Nested
    @DisplayName("GET /v1/courses")
    inner class GetAllCoursesTests {
        @Test
        internal fun `should return all created courses`() {
            courseRepository.saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Kotlin"),
            ))

            val responseBody = mockMvc.get("/v1/courses") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

            val courses = objectMapper.readValue(responseBody, Array<CourseDTO>::class.java).toList()
            assertEquals(2, courses.size)

            assertTrue(courses.any { it.name == "Typescript bez tajemnic" })
            assertTrue(courses.any { it.name == "Kotlin bez tajemnic" })
        }

        @Test
        internal fun `should return courses that contains provided name`() {
            courseRepository.saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Kotlin"),
            ))

            val responseBody = mockMvc.get("/v1/courses?name=Typescript") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

            val courses = objectMapper.readValue(responseBody, Array<CourseDTO>::class.java).toList()
            assertEquals(1, courses.size)

            assertEquals("Typescript bez tajemnic", courses[0].name)
            assertEquals("Typescript", courses[0].category)
        }

        @Test
        internal fun `should return courses that contains provided category`() {
            courseRepository.saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Kotlin"),
            ))

            val responseBody = mockMvc.get("/v1/courses?category=Kotlin") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

            val courses = objectMapper.readValue(responseBody, Array<CourseDTO>::class.java).toList()
            assertEquals(1, courses.size)

            assertEquals("Kotlin bez tajemnic", courses[0].name)
            assertEquals("Kotlin", courses[0].category)
        }

        @Test
        internal fun `should return courses that contains provided category and name`() {
            courseRepository.saveAll(listOf(
                Course(null, "Typescript bez tajemnic", "Typescript"),
                Course(null, "Kotlin bez tajemnic", "Typescript"),
            ))

            val responseBody = mockMvc.get("/v1/courses?category=Typescript&name=bez") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

            val courses = objectMapper.readValue(responseBody, Array<CourseDTO>::class.java).toList()
            assertEquals(2, courses.size)

            assertEquals("Typescript bez tajemnic", courses[0].name)
            assertEquals("Typescript", courses[0].category)

            assertEquals("Kotlin bez tajemnic", courses[1].name)
            assertEquals("Typescript", courses[1].category)
        }

        @Test
        internal fun `should return empty list when no courses exist`() {
            val responseBody = mockMvc.get("/v1/courses") {
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
            }.andReturn().response.contentAsString

            val courses = objectMapper.readValue(responseBody, Array<CourseDTO>::class.java).toList()
            assertTrue(courses.isEmpty())
        }
    }

    @Nested
    @DisplayName("PUT /v1/courses/{courseId}")
    inner class UpdateCourseTests {
        @Test
        internal fun `should update an existing course and return it`() {
            val courseToUpdate = courseRepository.save(
                Course(null, "Kotlin bez tajemnic", "Kotlin")
            )

            val updatePayload = CourseDTO(
                id = courseToUpdate.id,
                name = "Java i my",
                category = "Java 8"
            )

            val responseBody = mockMvc.put("/v1/courses/${updatePayload.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatePayload)
            }
                .andExpect { status { isOk() } }
                .andReturn().response.contentAsString

            val updatedCourseDTO = objectMapper.readValue(responseBody, CourseDTO::class.java)
            assertEquals(updatePayload.name, updatedCourseDTO.name)
            assertEquals(updatePayload.category, updatedCourseDTO.category)

            val updatedCourseInDb = courseRepository.findById(courseToUpdate.id!!).get()
            assertEquals(updatePayload.name, updatedCourseInDb.name)
            assertEquals(updatePayload.category, updatedCourseInDb.category)
        }

        @Test
        internal fun `should return 404 when updating a non-existent course`() {
            val nonExistentId = 999
            val updatePayload = CourseDTO(id = nonExistentId, name = "Nowa nazwa", category = "Nowa kategoria")

            val responseBody = mockMvc.put("/v1/courses/$nonExistentId") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatePayload)
            }
                .andExpect {
                    status { isNotFound() }
                }
                .andReturn().response.contentAsString

            val errorResponse = objectMapper.readValue(responseBody, ErrorResponse::class.java)

            assertEquals(1, errorResponse.errors.size)
            assertEquals(ErrorType.NOT_FOUND, errorResponse.errors[0].type)
            assertEquals("Course with id 999 not found", errorResponse.errors[0].message)
        }

        @Test
        internal fun `should return 400 when updating course with missing name`() {
            val courseToUpdate = courseRepository.save(
                Course(null, "Kotlin bez tajemnic", "Kotlin")
            )

            val updatePayload = CourseDTO(
                id = courseToUpdate.id,
                name = null,
                category = "Java 8"
            )

            val responseBody = mockMvc.put("/v1/courses/${updatePayload.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatePayload)
            }
                .andExpect { status { isBadRequest() } }
                .andReturn().response.contentAsString

            val errorResponse = objectMapper.readValue(responseBody, ErrorResponse::class.java)

            assertEquals(1, errorResponse.errors.size)
            assertEquals(ErrorType.VALIDATION_ERROR, errorResponse.errors[0].type)
            assertEquals("Name is required", errorResponse.errors[0].message)
        }

        @Test
        internal fun `should return 400 when updating course with missing category name`() {
            val courseToUpdate = courseRepository.save(
                Course(null, "Kotlin bez tajemnic", "Kotlin")
            )

            val updatePayload = CourseDTO(
                id = courseToUpdate.id,
                name = "Nowa nazwa",
                category = null
            )

            val responseBody = mockMvc.put("/v1/courses/${updatePayload.id}") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatePayload)
            }
                .andExpect { status { isBadRequest() } }
                .andReturn().response.contentAsString

            val errorResponse = objectMapper.readValue(responseBody, ErrorResponse::class.java)

            assertEquals(1, errorResponse.errors.size)
            assertEquals(ErrorType.VALIDATION_ERROR, errorResponse.errors[0].type)
            assertEquals("Category is required", errorResponse.errors[0].message)
        }
    }

    @Nested
    @DisplayName("DELETE /v1/courses/{courseId}")
    inner class DeleteCourseTests {
        @Test
        fun `should delete an existing course`() {
            val courseToUpdate = courseRepository.save(
                Course(null, "Kotlin bez tajemnic", "Kotlin")
            )
            assertEquals(1, courseRepository.findAll().count())

            mockMvc.delete("/v1/courses/${courseToUpdate.id}") {
                contentType = MediaType.APPLICATION_JSON
            }
                .andExpect { status { isNoContent() } } // Lepszy status dla DELETE

            assertEquals(0, courseRepository.findAll().count())
        }

        @Test
        internal fun `should return 404 when deleting a non-existent course`() {
            val nonExistentId = 999

            mockMvc.delete("/v1/courses/$nonExistentId")
                .andExpect {
                    status { isNotFound() }
                }
        }
    }
}