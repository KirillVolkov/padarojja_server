package padarojja.com.plugins

import org.jetbrains.exposed.sql.Database

internal lateinit var databaseInstance: Database

fun configureDatabase() {
    databaseInstance = Database.connect(
        "jdbc:postgresql://localhost:5432/places",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "3a76"
    )
}
