package padarojja.com.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import padarojja.com.database.Place.toPlace

object Route : Table("routes") {
    val id = integer("routeid").uniqueIndex().autoIncrement()
    val name = varchar("routename", 255)
    val description = text("description")
    val distance = double("distance") // расстояние маршрута
    val duration = double("duration") // длительность в часах
    val rating = double("rating") // рейтинг


    @Serializable
    data class DTO(
        val id: Int,
        val name: String,
        val description: String,
        val places: List<Place.DTO>,
        val distance: Double,
        val duration: Double,
        val rating: Double,
        val media: List<String> = emptyList(),
    )

    fun getAllRoutesWithPlaces(): List<DTO> {
        return transaction {
            val routes = Route.selectAll().map { row ->

                val places = (RoutePlace innerJoin Place).selectAll()
                    .where { RoutePlace.routeId eq row[Route.id] }
                    .map { it.toPlace() }

                row.toRoute(places)
            }
            routes
        }
    }

    private fun ResultRow.toRoute(places: List<Place.DTO>): DTO {
        return DTO(
            id = this[id],
            name = this[name],
            description = this[description],
            places = places,
            distance = this[distance],
            duration = this[duration],
            rating = this[rating]
        )
    }
}


