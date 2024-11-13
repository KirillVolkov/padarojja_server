package padarojja.com.plugins.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.jetbrains.exposed.sql.transactions.transaction
import padarojja.com.database.Place
import padarojja.com.plugins.routing.Parameter.CITY_ID
import padarojja.com.plugins.routing.Parameter.FILE_NAME
import padarojja.com.plugins.routing.Parameter.PLACE_ID
import java.io.File

fun Routing.routePlaces() {

    route("/places") {

        /**
         * Получить все места в городе
         */
        get("/{$CITY_ID}") {
            try {
                val places = transaction {
                    Place.selectByCity(
                        cityId = call.parameters[CITY_ID]?.toInt()
                            ?: throw RuntimeException("missing cityId") // в запросе пустой айди города
                    ).map {
                        it.copy(media = getMediaList(it.id))
                    }
                }
                call.respond(HttpStatusCode.OK, places)
            } catch (e: Exception) {
                e.printStack()
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
            }
        }

        post("/add") {
            try {
                val newPlace = call.receive<Place.DTO>()
                val newPlaceId = Place.insert(newPlace)

                call.respond(
                    HttpStatusCode.OK,
                    mapOf("newPlaceId" to newPlaceId)
                )

            } catch (e: Exception) {
                e.printStack()
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
            }
        }

        /**
         * Загрузка фото
         */
        post("/{$PLACE_ID}/upload") {
            try {
                call.parameters[PLACE_ID]?.toInt()?.let { placeId ->

                    if (transaction { Place.exists(placeId) }) {
                        val file = File("places/$placeId/image_${getMediaIndex(placeId)}.jpg")
                        call.receiveChannel().copyAndClose(file.writeChannel())
                        call.respond(HttpStatusCode.OK)
                    } else
                        throw RuntimeException("Unknown place with id $placeId") // в базе нет такого места

                } ?: throw RuntimeException("missing placeId") // в запросе пустой айди места

            } catch (e: Exception) {
                e.printStack()
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "")
            }
        }

        /**
         * Чтение фото
         */
        get("/{$PLACE_ID}/{$FILE_NAME}") {
            call.parameters[PLACE_ID]?.toInt()?.let { placeId ->
                val fileName = call.parameters[FILE_NAME]
                val file = File("places/$placeId/$fileName")
                if (file.exists()) {
                    call.respondBytes {
                        file.readBytes()
                    }
                } else call.respond(HttpStatusCode.NotFound)
            } ?: throw RuntimeException("missing placeId") // в запросе пустой айди места
        }
    }
}

/**
 * Получить следующий индекс медиафайла для места
 */
private fun getMediaIndex(placeId: Int): Int {

    val dir = File("places/$placeId")
    if (dir.exists().not()) {
        dir.mkdirs()
    }

    return (dir.list()?.size ?: 0) + 1
}

/**
 * Получить список медиафайлов для места
 */
fun getMediaList(placeId: Int): List<String> {
    val dir = File("places/$placeId")
    return if (dir.exists()) {
        return dir.list { file, name ->
            name.contains( "jpg")
        }?.toList() ?: emptyList()
    } else emptyList()
}

