package padarojja.com.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object City : Table("cities") {
    internal val cityId = City.integer("cityid").uniqueIndex().autoIncrement()
    private val cityName = City.varchar("cityname", 100)
    private val countryId = City.integer("countryid").references(Country.countryId)

    @Serializable
    data class DTO(
        val id: Int,
        val name: String,
        val countryId: Int
    )

    fun insert(city: DTO) {
        transaction {
            City.insert {
                it[cityId] = city.id
                it[cityName] = city.name
                it[countryId] = city.countryId
            }
        }
    }

    fun selectByCountry(countryId: Int): List<DTO> {
        return City.selectAll().where {
            City.countryId eq countryId
        }.mapToDto()
    }

    private fun Query.mapToDto(): List<DTO> {
        return map { DTO(it[cityId], it[cityName], it[countryId]) }
    }
}
