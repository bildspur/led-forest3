package ch.bildspur.ledforest.model

import com.google.gson.annotations.Expose

data class NumberRange<T : Number>(
        @Expose val start: T,
        @Expose val end: T)