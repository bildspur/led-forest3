package ch.bildspur.ledforest.web

import ch.bildspur.ledforest.annotation.AnnotationReader
import ch.bildspur.ledforest.annotation.AnnotationRegistryEntry
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.web.mapping.BaseDataMapper
import ch.bildspur.ledforest.web.mapping.BooleanDataMapper
import ch.bildspur.model.DataModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

class WebInterface(val project: DataModel<Project>) {
    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .excludeFieldsWithoutExposeAnnotation()
        .serializeSpecialFloatingPointValues()
        .create()

    var apiPrefix = "/api"
    var webAnnotationRegistry = mutableListOf<AnnotationRegistryEntry<*, BaseDataMapper<*>>>(
        AnnotationRegistryEntry(BooleanWebEndpoint::class.java, ::BooleanDataMapper)
    )

    fun start() {
        val reader = AnnotationReader(webAnnotationRegistry)
        val annotationFields = reader.readAnnotations(project.value.leda)

        val server = embeddedServer(Netty, port = 8000) {
            routing {
                static("/") {
                    staticRootFolder = File("web")
                    files(".")
                    default("index.html")
                }

                for(annotationField in annotationFields) {
                    val mapper = annotationField.entry as BaseDataMapper<*>

                    get("${apiPrefix}${mapper.url}") {
                        if (call.request.queryParameters.contains("value")) {
                            mapper.set(call.request.queryParameters["value"].toString())
                        }

                        call.respondText(mapper.get())
                    }
                }
            }
        }
        server.start()
    }
}