package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.model.Project
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import processing.core.PApplet
import java.nio.file.Files
import java.nio.file.Paths


/**
 * Created by cansik on 11.07.17.
 */
class ConfigurationController(internal var sketch: PApplet) {
    companion object {
        @JvmStatic val CONFIGURATION_FILE = "ledforest.json"
    }

    lateinit var gson: Gson

    lateinit var settings: Project

    fun setup() {
        gson = GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
        settings = Project()
    }

    fun loadConfiguration() {
        val content = String(Files.readAllBytes(Paths.get(sketch.dataPath(CONFIGURATION_FILE))))
        settings = gson.fromJson<Project>(content)
    }

    fun saveConfiguration() {
        val content = gson.toJson(settings)
        Files.write(Paths.get(sketch.dataPath(CONFIGURATION_FILE)), content.toByteArray())
    }
}