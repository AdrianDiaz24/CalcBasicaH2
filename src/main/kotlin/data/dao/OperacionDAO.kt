package es.iesraprog2425.pruebaes.data.dao

import es.iesraprog2425.pruebaes.data.db.Dataobject
import es.iesraprog2425.pruebaes.model.Operacion
import es.iesraprog2425.pruebaes.model.Operadores
import javax.sql.DataSource

class OperacionDAO(private val dataSource: DataSource): IOperacionDAO {
    override fun getAll(): List<Operacion> {
        val operaciones = mutableListOf<Operacion>()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM Operaciones").use { stmt ->
                val resultados = stmt.executeQuery()
                while (resultados.next()){
                    operaciones.add(
                        Operacion(
                            id = resultados.getInt("id"),
                            num1 = resultados.getDouble("num1"),
                            operador = Operadores.getOperador(resultados.getString("operador")),
                            num2 = resultados.getDouble("num2"),
                            resultado = resultados.getDouble("resultado")
                        )
                    )
                }
            }
        }
        return operaciones
    }

    override fun getAllWithLimit(): List<Operacion> {
        val operaciones = mutableListOf<Operacion>()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM Operaciones ORDER BY id DESC LIMIT 5").use { stmt ->
                val resultados = stmt.executeQuery()
                while (resultados.next()){
                    operaciones.add(
                        Operacion(
                            id = resultados.getInt("id"),
                            num1 = resultados.getDouble("num1"),
                            operador = Operadores.getOperador(resultados.getString("operador")),
                            num2 = resultados.getDouble("num2"),
                            resultado = resultados.getDouble("resultado")
                        )
                    )
                }
            }
        }
        return  operaciones.asReversed()
    }

    override fun getById(id: Int): Operacion? {
        var operaciones: Operacion? = null
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT * FROM Operaciones WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                val resultados = stmt.executeQuery()
                while (resultados.next()){
                    operaciones = Operacion(
                        id = resultados.getInt("id"),
                        num1 = resultados.getDouble("num1"),
                        operador = Operadores.getOperador(resultados.getString("operador")),
                        num2 = resultados.getDouble("num2"),
                        resultado = resultados.getDouble("resultado")
                    )
                }
            }
        }
        return operaciones
    }

    override fun add(operacion: Operacion) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO Operaciones(num1, operador, num2, resultado) VALUES (?, ?, ?, ?)").use { stmt ->
                stmt.setDouble(1, operacion.num1)
                stmt.setString(2, operacion.operador?.simbolos?.firstOrNull())
                stmt.setDouble(3, operacion.num2)
                stmt.setDouble(4, operacion.resultado)
                stmt.executeUpdate()
            }
        }
    }

    override fun update(operacion: Operacion, id: Int) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE Operaciones SET num1 = ?, operador = ?, num2 = ?, resultado = ? WHERE id = ?").use { stmt ->
                stmt.setDouble(1, operacion.num1)
                stmt.setString(2, operacion.operador?.simbolos?.firstOrNull())
                stmt.setDouble(3, operacion.num2)
                stmt.setDouble(4, operacion.resultado)
                stmt.setInt(5, id)
                stmt.executeUpdate()
            }
        }
    }

    override fun delete(id: Int) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("DELETE FROM Operaciones WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
        }
    }

    override fun inicializarTabla() {
        val sql =
            "DROP TABLE IF EXISTS Operaciones;\n" +
                    "CREATE TABLE Operaciones (\n" +
                    "id IDENTITY PRIMARY KEY,\n" +
                    "num1 DOUBLE NOT NULL,\n" +
                    "operador VARCHAR(10) NOT NULL,\n" +
                    "num2 DOUBLE NOT NULL,\n" +
                    "resultado DOUBLE\n" +
                    ");"


        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sql)
            }
        }
    }


}