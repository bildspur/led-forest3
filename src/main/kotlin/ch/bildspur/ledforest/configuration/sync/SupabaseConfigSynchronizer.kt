package ch.bildspur.ledforest.configuration.sync

import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

class SupabaseConfigSynchronizer(project: DataModel<Project>) : ConfigSynchronizer(project) {
    private val installationTableName = "installation"
    private val configurationUpdateChannelName = "#config"

    private val idColumnName = "id"
    private val installationIdColumnName = "installation"

    @Serializable
    data class Installation(
        val id: Int,
        val key: String,
        val name: String,
        val active: Boolean,
        @SerialName("config_table") val configTable: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("last_ping") val lastPing: String,
    )

    private lateinit var client: SupabaseClient
    private lateinit var activeInstallation: Installation

    val installation: Installation
        get() = activeInstallation

    fun connect(supabaseUrl: String, supabaseKey: String) {
        client = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(GoTrue) {
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest) {

            }
            install(Realtime) {

            }
        }
    }

    suspend fun login(userEmail: String, userPassword: String) {
        client.gotrue.loginWith(Email) {
            email = userEmail
            password = userPassword
        }
    }

    suspend fun useInstallation(installationKey: String) {
        val result = client.postgrest[installationTableName]
            .select {
                Installation::key eq installationKey
            }

        activeInstallation = result.decodeList<Installation>().first()
        updateConfiguration()
    }

    suspend fun updateConfiguration() {
        val result = client.postgrest[installation.configTable]
            .select {
                eq(installationIdColumnName, activeInstallation.id)
            }

        val jsonObject = result.body.jsonArray[0] as JsonObject
        onUpdate(jsonObject)
    }

    suspend fun setupRealtime() {
        client.realtime.connect()

        val channel = client.realtime.createChannel(configurationUpdateChannelName) {
            presence {
                key = activeInstallation.key
            }

            broadcast {
                //broadcast options
            }
        }

        val tableChangeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = installation.configTable
            filter = "$installationIdColumnName=eq.${installation.id}"
        }

        channel.join()

        println("connected to realtime database")

        tableChangeFlow.collect {
            onUpdate(it.record)
        }
    }

    suspend fun disconnect() {
        client.close()
    }

    suspend fun createUser(authToken: String, userEmail: String, userPassword: String) {
        client.gotrue.importAuthToken(authToken)
        client.gotrue.admin.createUserWithEmail {
            email = userEmail
            password = userPassword
            autoConfirm = true
        }

        print("account created!")
    }

    private fun onUpdate(result: JsonObject) {
        result.entries.filter { it.key != idColumnName && it.key != installationTableName }.forEach {
            val value = it.value as JsonPrimitive
            onValueReceived(it.key, value.content)
        }
    }

    override fun publishValue(key: String, value: Any?, data: String) {
        GlobalScope.async {
            kotlin.runCatching {
                client.postgrest[installation.configTable].update({
                    set(key, data)
                }) {
                    eq(installationIdColumnName, activeInstallation.id)
                }
            }
        }
    }

    override fun start() {
        val config = project.value.supabase

        if (!config.enabled.value) return

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Handle $exception in CoroutineExceptionHandler")
        }

        GlobalScope.async(coroutineExceptionHandler) {
            connect(config.projectUrl.value, config.projectSecret.value)
            login(config.userEmail.value, config.userPassword.value)
            useInstallation(config.installationKey.value)

            config.installationName.value = activeInstallation.name

            launch(coroutineExceptionHandler) {
                setupRealtime()
            }

            println("Supabase has been setup")
        }
    }
}