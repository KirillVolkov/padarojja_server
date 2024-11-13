package padarojja.com.database

import org.jetbrains.exposed.sql.Table

object Country : Table("countries") {
    internal val countryId = Country.integer("countryid").uniqueIndex()
    private val countryName = Country.varchar("countryname", 100)
    private val countryCode = Country.varchar("countrycode", 10)
}
