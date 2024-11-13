package padarojja.com.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import padarojja.com.database.City

fun Routing.routeCities() {

    /**
     * Получить все доступные города
     */
    get("/cities") {
        try {
            val cities = transaction { City.selectByCountry(1) }
            call.respond(HttpStatusCode.OK, cities)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
        }
    }
}
