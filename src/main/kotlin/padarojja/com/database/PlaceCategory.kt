package padarojja.com.database

import org.jetbrains.exposed.sql.Table

object PlaceCategory : Table() {
    val placeId = integer("placeid").references(Place.id)
    val categoryId = integer("categoryid").references(Category.id)
    override val primaryKey = PrimaryKey(placeId, categoryId)
}
