package ch.bildspur.ledforest.ui.properties

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PVectorParameter(val name: String)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class PVectorAngleParameter(val name: String)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArrowControlParameter(val name: String, val invokesChange : Boolean = true)