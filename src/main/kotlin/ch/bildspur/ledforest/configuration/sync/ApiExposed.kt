package ch.bildspur.ledforest.configuration.sync

annotation class ApiExposed(val key: String, val receive: Boolean = true, val publish: Boolean = true)
