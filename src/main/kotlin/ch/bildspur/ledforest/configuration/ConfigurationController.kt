package ch.bildspur.ledforest.configuration

import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.gson.*
import processing.core.PVector
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Created by cansik on 11.07.17.
 */
class ConfigurationController() {
    companion object {
        @JvmStatic val CONFIGURATION_FILE = "config/ledforest.json"
    }

    private val vectorDeserializer = PVectorDeserializer()

    val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            //.registerTypeAdapter(Tube::class.java, TubeDeserializer())
            //.registerTypeAdapter(Universe::class.java, UniverseDeserializer())
            .registerTypeAdapter(PVector::class.java, PVectorSerializer())
            .registerTypeAdapter(PVector::class.java, PVectorDeserializer())
            .create()

    fun loadAppConfig(): AppConfig {
        return loadConfiguration(Paths.get(CONFIGURATION_FILE))
    }

    fun saveAppConfig(config: AppConfig) {
        saveConfiguration(Paths.get(CONFIGURATION_FILE), config)
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

    private inner class PVectorDeserializer : JsonDeserializer<PVector> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PVector {
            val x = json["x"].asFloat
            val y = json["y"].asFloat
            val z = json["z"].asFloat
            return PVector(x, y, z)
        }
    }

    private inner class PVectorSerializer : JsonSerializer<PVector> {
        override fun serialize(src: PVector, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val obj = JsonObject()
            obj.addProperty("x", src.x)
            obj.addProperty("y", src.y)
            obj.addProperty("z", src.z)
            return obj
        }
    }

    private inner class TubeDeserializer : JsonDeserializer<Tube> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Tube {
            val universe = json["universe"].asInt
            val ledCount = json["ledCount"].asInt
            val addressStart = json["addressStart"].asInt
            val position = vectorDeserializer.deserialize(json["position"], typeOfT, context)
            val rotation = vectorDeserializer.deserialize(json["rotation"], typeOfT, context)
            val tube = Tube()
            tube.ledCount.value = ledCount

            return tube
        }
    }

    private inner class UniverseDeserializer : JsonDeserializer<Universe> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Universe {
            val id = json["id"].asInt
            return Universe(id)
        }
    }
}