package ch.bildspur

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

fun main() {
    thread(start = true) {
        runBlocking {
            val client = createSupabaseClient(
                supabaseUrl = "test",
                supabaseKey = "test"
            ) {
            }

            client.gotrue.importAuthToken("12345")
        }
    }

    println("waiting")
}