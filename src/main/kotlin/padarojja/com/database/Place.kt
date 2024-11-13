package padarojja.com.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Place : IdTable<Int>("culturalplaces") {

    override val id: Column<EntityID<Int>> = integer("placeid").autoIncrement().entityId()

    private val placeName = Place.varchar("placename", 100)
    private val cityId = Place.integer("cityid").references(City.cityId)
    private val address = Place.varchar("address", 100)
    private val lat = Place.decimal("latitude", 9, 6)
    private val lng = Place.decimal("longitude", 9, 6)
    private val description = Place.text("description")
    private val openingHours = Place.varchar("openinghours", 100)
    private val contacts = Place.varchar("contactinfo", 100)
    private val rating = Place.decimal("rating", 1, 1)

    @Serializable
    data class DTO(
        val id: Int,
        val name: String,
        val cityId: Int,
        val address: String,
        val lat: Double,
        val lng: Double,
        val description: String,
        val openingHours: String,
        val contacts: String,
        val media: List<String> = emptyList(),
        val rating: Double,
    )

    fun insert(place: DTO): Int {
        return transaction {
            Place.insertAndGetId {
                it[placeName] = place.name
                it[cityId] = place.cityId
                it[address] = place.address
                it[lat] = place.lat.toBigDecimal()
                it[lng] = place.lng.toBigDecimal()
                it[description] = place.description
                it[openingHours] = place.openingHours
                it[contacts] = place.contacts
            }.value
        }
    }

    fun selectByCity(cityId: Int): List<DTO> {
        return Place.selectAll().where {
            Place.cityId eq cityId
        }.mapToDto()
    }

    fun exists(placeId: Int): Boolean {
        return Place.selectAll().where {
            id eq placeId
        }.empty().not()
    }

    private fun Query.mapToDto(): List<DTO> {
        return map {
            it.toPlace()
        }
    }

    fun ResultRow.toPlace(): DTO {
        return DTO(
            id = this[id].value,
            cityId = this[cityId],
            name = this[placeName],
            address = this[address],
            lat = this[lat].toDouble(),
            lng = this[lng].toDouble(),
            description = this[description],
            openingHours = this[openingHours],
            contacts = this[contacts],
            rating = this[rating].toDouble()
        )
    }
}
