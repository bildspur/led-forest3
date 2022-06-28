package ch.bildspur.ledforest.web

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class BooleanWebEndpoint(val url: String)