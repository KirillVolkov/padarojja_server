package padarojja.com.database

import org.jetbrains.exposed.sql.Table

object RoutePlace : Table("routeplaces") {
    val routeId = integer("routeid").references(Route.id)
    val placeId = integer("placeid").references(Place.id)
    val order = integer("Order")
    override val primaryKey = PrimaryKey(routeId, placeId)
}
