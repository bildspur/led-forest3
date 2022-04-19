package ch.bildspur.ledforest.configuration

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.Collider
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.model.DataModel
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.*
import processing.core.PVector
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.reflect.typeOf


/**
 * Created by cansik on 11.07.17.
 * todo: replace this class with the configuration controller directly from bildspur-base
 */
class ConfigurationController {
    companion object {
        @JvmStatic
        val CONFIGURATION_FILE = "${Sketch.URI_NAME}.json"

        @JvmStatic
        val CONFIGURATION_DIR: Path = Paths.get(System.getProperty("user.home"), ".bildspur", Sketch.URI_NAME)

        @JvmStatic
        val CONFIGURATION_PATH: Path = Paths.get(CONFIGURATION_DIR.toString(), CONFIGURATION_FILE)
    }

    val gsonBuilder: GsonBuilder = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(DataModel::class.java, DataModelInstanceCreator())
            .registerTypeAdapter(PVector::class.java, PVectorSerializer())
            .registerTypeAdapter(PVector::class.java, PVectorDeserializer())
            .registerTypeAdapter(Tube::class.java, TubeInstanceCreator())
            .registerTypeAdapter(LandmarkPulseCollider::class.java, LandmarkPulseColliderInstanceCreator())
            .registerTypeAdapterFactory(PostProcessingEnabler())

    val gson: Gson = gsonBuilder.create()

    fun loadAppConfig(): AppConfig {
        if (!Files.exists(CONFIGURATION_DIR)) {
            Files.createDirectories(CONFIGURATION_DIR)
            saveAppConfig(AppConfig())
        }

        return loadData(CONFIGURATION_PATH)
    }

    fun saveAppConfig(config: AppConfig) {
        saveData(CONFIGURATION_PATH, config)
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

            // todo: fix enumset creator
            /*
            if (defaultValue is ParameterizedType && defaultValue.rawType == EnumSet::class.java) {
                val subTypeParameters = defaultValue.actualTypeArguments
                val subDefaultValue = (subTypeParameters[0] as Class<*>).enumConstants[0]
                return DataModel(EnumSet.noneOf(PoseLandmark.Nose.javaClass))
            }
            */

            return DataModel(defaultValue as Class<*>)
        }
    }

    private inner class TubeInstanceCreator : InstanceCreator<Tube> {
        override fun createInstance(type: Type): Tube {
            return Tube()
        }
    }

    private inner class LandmarkPulseColliderInstanceCreator : InstanceCreator<LandmarkPulseCollider> {
        override fun createInstance(type: Type): LandmarkPulseCollider {
            return LandmarkPulseCollider()
        }
    }
}