package es.iesraprog2425.pruebaes.data.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object Dataobject {
    fun getDataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:./Logs/Operaciones;AUTO_SERVER=TRUE"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
        }
        return HikariDataSource(config)
    }
}