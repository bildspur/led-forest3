package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.Project
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import processing.core.PApplet
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Created by cansik on 11.07.17.
 */
class ConfigurationController(internal var sketch: PApplet) {
    companion object {
        @JvmStatic val CONFIGURATION_FILE = "ledforest.json"
    }

    lateinit var gson: Gson

    fun setup() {
        gson = GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
    }

    fun loadAppConfig(): AppConfig {
        return loadConfiguration(Paths.get(sketch.dataPath(CONFIGURATION_FILE)))
    }

    fun saveAppConfig(config: AppConfig) {
        saveConfiguration(Paths.get(sketch.dataPath(CONFIGURATION_FILE)), config)
    }

    fun loadProject(projectFile: String): Project {
        return loadConfiguration(Paths.get(projectFile))
    }

    fun saveProject(projectFile: String, project: Project) {
        saveConfiguration(Paths.get(projectFile), project)
    }

    internal inline fun <reified T : Any> loadConfiguration(configFile: Path): T {
        val content = String(Files.readAllBytes(configFile))
        return gson.fromJson(content)
    }

    internal inline fun <reified T : Any> saveConfiguration(configFile: Path, config: T) {
        val content = gson.toJson(config)
        Files.write(configFile, content.toByteArray())
    }
}