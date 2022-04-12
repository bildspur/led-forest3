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

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SeparatorParameter(val name: String = "", val fontSize : Double = 14.0, val topPadding: Double = 10.0,  val bottomPadding: Double = 10.0)