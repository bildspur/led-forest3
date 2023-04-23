
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val supabaseUrl = ""
        val supabaseKey = ""
        val serviceRoleToken = ""
        val newUsername = ""
        val newPassword = ""

        val client = createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(GoTrue) {}
        }

        client.gotrue.importAuthToken(serviceRoleToken)
        client.gotrue.admin.createUserWithEmail {
            email = newUsername
            password = newPassword
            autoConfirm = true
        }

        println("done!")
    }
}