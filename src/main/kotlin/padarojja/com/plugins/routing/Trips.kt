package padarojja.com.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import org.jetbrains.exposed.sql.transactions.transaction
import padarojja.com.database.Route
import padarojja.com.plugins.routing.Parameter.FILE_NAME
import padarojja.com.plugins.routing.Parameter.ROUTE_ID
import java.io.File

fun Routing.routeTrips() {
    route("/trips") {

        /**
         * Получить все маршруты
         */
        get {
            try {
                val trips = transaction {
                    Route.getAllRoutesWithPlaces().map { route ->
                        route.copy(
                            media = getMediaListRoutes(route.id),
                            places = route.places.map { place ->
                                place.copy(
                                    media = getMediaList(place.id)
                                )
                            })
                    }
                }
                call.respond(HttpStatusCode.OK, trips)
            } catch (e: Exception) {
                e.printStack()
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
            }
        }

        /**
         * Чтение фото
         */
        get("/{$ROUTE_ID}/{$FILE_NAME}") {
            call.parameters[ROUTE_ID]?.toInt()?.let { routeId ->
                val fileName = call.parameters[FILE_NAME]
                val file = File("routes/$routeId/$fileName")
                if (file.exists()) {
                    call.respondBytes {
                        file.readBytes()
                    }
                } else call.respond(HttpStatusCode.NotFound)
            } ?: throw RuntimeException("missing routeId") // в запросе пустой айди места
        }
    }
}

/**
 * Получить список медиафайлов для места
 */
private fun getMediaListRoutes(routeId: Int): List<String> {
    val dir = File("routes/$routeId")
    return if (dir.exists()) {
        return dir.list { file, name ->
            name.contains( "jpg")
        }?.toList() ?: emptyList()
    } else emptyList()
}
