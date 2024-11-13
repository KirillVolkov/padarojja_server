package padarojja.com.plugins.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        routeCities()
        routePlaces()
        routeTrips()
    }
}

internal object Parameter {
    const val CITY_ID = "cityId"
    const val PLACE_ID = "placeId"
    const val ROUTE_ID = "placeId"
    const val FILE_NAME = "fileName"
}
