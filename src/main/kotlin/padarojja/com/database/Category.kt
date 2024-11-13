package padarojja.com.database

import org.jetbrains.exposed.sql.Table

object Category : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
}
