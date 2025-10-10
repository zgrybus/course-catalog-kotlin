package com.kotlinspring.course_catalog

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

interface Loggable {
    val logger: KLogger get() = KotlinLogging.logger { javaClass.name }
}