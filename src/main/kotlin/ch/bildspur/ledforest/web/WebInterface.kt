package ch.bildspur.ledforest.web

import ch.bildspur.ledforest.model.Project
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

    private val interactionRoute = "/api/interaction"
    private val pulsesRoute = "/api/pulses"
    private val brightnessRoute = "/api/brightness"
    private val handInteractionRoute = "/api/hand-interaction"

    fun start() {
        val server = embeddedServer(Netty, port = 8000) {
            routing {
                static("/") {
                    staticRootFolder = File("web")
                    files(".")
                    default("index.html")
                }

                get(interactionRoute) {
                    if (call.request.queryParameters.contains("value")) {
                        project.value.leda.enabledInteraction.value = call.request.queryParameters["value"] == "1"
                    }

                    val result = if (project.value.leda.enabledInteraction.value) "1" else "0"
                    call.respondText(result)
                }

                get(pulsesRoute) {
                    if (call.request.queryParameters.contains("value")) {
                        project.value.leda.enableRandomPulses.value = call.request.queryParameters["value"] == "1"
                    }

                    val result = if (project.value.leda.enableRandomPulses.value) "1" else "0"
                    call.respondText(result)
                }

                get(brightnessRoute) {
                    if (call.request.queryParameters.contains("value")) {
                        val data = call.parameters["value"]?.toFloat()
                        if (data != null) {
                            project.value.light.luminosity.value = data
                        }
                    }

                    call.respondText(project.value.light.luminosity.value.toString())
                }

                get(handInteractionRoute) {
                    if (call.request.queryParameters.contains("value")) {
                        project.value.leda.colliderSceneOnly.value = call.request.queryParameters["value"] != "1"
                    }

                    val result = if (!project.value.leda.colliderSceneOnly.value) "1" else "0"
                    call.respondText(result)
                }
            }
        }
        server.start()
    }
}