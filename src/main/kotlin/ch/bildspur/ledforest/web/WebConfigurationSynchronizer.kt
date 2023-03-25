package ch.bildspur.ledforest.web

import ch.bildspur.ledforest.configuration.sync.ConfigSynchronizer
import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class WebConfigurationSynchronizer(project: DataModel<Project>) : ConfigSynchronizer(project) {
    val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .excludeFieldsWithoutExposeAnnotation()
        .serializeSpecialFloatingPointValues()
        .create()

    override fun start() {
        val server = embeddedServer(Netty, port = 8000, module = {
            install(FreeMarker) {
                templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
                outputFormat = HTMLOutputFormat.INSTANCE
            }

            routing {
                get("/") {
                    call.respond(
                        FreeMarkerContent("index.ftl", mapOf("syncableProperties" to syncableProperties))
                    )
                }
            }
        })

        server.start()
    }

    override fun publishValue(key: String, value: Any?, data: String) {}
}