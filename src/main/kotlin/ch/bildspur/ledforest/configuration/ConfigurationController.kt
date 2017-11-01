package ch.bildspur.ledforest.configuration

import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.gson.*
import processing.core.PVector
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Created by cansik on 11.07.17.
 */
class ConfigurationController {
    companion object {
        @JvmStatic
        val CONFIGURATION_FILE = "config/ledforest.json"
    }

    val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(DataModel::class.java, DataModelInstanceCreator())
            .registerTypeAdapter(PVector::class.java, PVectorSerializer())
            .registerTypeAdapter(PVector::class.java, PVectorDeserializer())
            .registerTypeAdapter(Tube::class.java, TubeInstanceCreator())
            .registerTypeAdapterFactory(PostProcessingEnabler())
            .create()

    fun loadAppConfig(): AppConfig {
        return loadData(Paths.get(CONFIGURATION_FILE))
    }

    fun saveAppConfig(config: AppConfig) {
        saveData(Paths.get(CONFIGURATION_FILE), config)
    }

    fun loadProject(projectFile: String): Project {
        return loadData(Paths.get(projectFile))
    }

    fun saveProject(projectFile: String, project: Project) {
        saveData(Paths.get(projectFile), project)
    }

    inline fun <reified T : Any> loadData(configFile: Path): T {
        val content = String(Files.readAllBytes(configFile))
        return gson.fromJson(content)
    }

    inline fun <reified T : Any> saveData(configFile: Path, config: T) {
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

    private inner class DataModelInstanceCreator : InstanceCreator<DataModel<*>> {
        override fun createInstance(type: Type): DataModel<*> {
            val typeParameters = (type as ParameterizedType).actualTypeArguments
            val defaultValue = typeParameters[0]
            return DataModel(defaultValue as Class<*>)
        }
    }

    private inner class TubeInstanceCreator : InstanceCreator<Tube> {
        override fun createInstance(type: Type): Tube {
            return Tube()
        }
    }
}