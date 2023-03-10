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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SupabaseConfigSynchronizer(project: DataModel<Project>) : ConfigSynchronizer(project) {
    private val installationTableName = "installation"
    private val configurationTableName = "configuration"
    private val configurationUpdateChannelName = "#random"

    @Serializable
    data class Installation(
        val id: Int,
        val name: String,
        @SerialName("created_at") val createdAt: String
    )

    @Serializable
    data class Configuration(
        val id: Int,
        val installation: Int,
        val interactive: Boolean,
        val brightness: Float
    )

    private lateinit var client: SupabaseClient
    private lateinit var activeInstallation: Installation
    private lateinit var activateConfiguration: Configuration

    val installation: Installation
        get() = activeInstallation

    val configuration: Configuration
        get() = activateConfiguration

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

    suspend fun useInstallation(installationName: String) {
        val result = client.postgrest[installationTableName]
            .select {
                Installation::name eq installationName
            }

        activeInstallation = result.decodeList<Installation>().first()
        updateConfiguration()
    }

    suspend fun updateConfiguration() {
        val result = client.postgrest[configurationTableName]
            .select {
                Configuration::installation eq activeInstallation.id
            }

        activateConfiguration = result.decodeList<Configuration>().first()
    }

    suspend fun setupRealtime() {
        client.realtime.connect()

        val channel = client.realtime.createChannel(configurationUpdateChannelName) {
            presence {
                //presence options
            }

            broadcast {
                //broadcast options
            }
        }

        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = configurationTableName
            filter = "id=eq.${activateConfiguration.id}"
        }

        channel.join()

        println("connected to realtime database")

        changeFlow.collect {
            val record = it.decodeRecord<Configuration>()
            println(record)
            activateConfiguration = record
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

    override fun start() {
        val config = project.value.supabase

        if (!config.enabled.value) return

        GlobalScope.async {
            kotlin.runCatching {
                connect(config.projectUrl.value, config.projectSecret.value)
                login(config.userEmail.value, config.userPassword.value)
                useInstallation(config.installationName.value)

                launch {
                    setupRealtime()
                }

                println("Supabase has been setup")
            }
        }
    }
}