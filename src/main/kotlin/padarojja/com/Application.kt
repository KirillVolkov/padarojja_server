package padarojja.com

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import padarojja.com.plugins.configureDatabase
import padarojja.com.plugins.routing.configureRouting
import padarojja.com.plugins.configureSecurity
import padarojja.com.plugins.configureSerialization

fun main() {
    configureDatabase()

    embeddedServer(CIO, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureRouting()
}
