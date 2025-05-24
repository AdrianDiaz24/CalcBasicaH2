package es.iesraprog2425.pruebaes.data.dao

import es.iesraprog2425.pruebaes.data.db.Dataobject
import es.iesraprog2425.pruebaes.model.Operacion
import es.iesraprog2425.pruebaes.model.Operadores

class OperacionDAO: IOperacionDAO {
    override fun getAll(): List<Operacion> {
        val operaciones = mutableListOf<Operacion>()
        Dataobject.getDataSource().connection.use { connection ->
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

    override fun getById(id: Int): Operacion? {
        var operaciones: Operacion? = null
        Dataobject.getDataSource().connection.use { connection ->
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
        Dataobject.getDataSource().connection.use { connection ->
            connection.prepareStatement("INSERT INTO Operaciones(num1, operador, num2, resultado) VALUES (?, ?, ?, ?)").use { stmt ->
                stmt.setDouble(1, operacion.num1)
                stmt.setString(2, operacion.operador?.simbolos?.firstOrNull())
                stmt.setDouble(3, operacion.num2)
                stmt.setDouble(4, operacion.resultado)
                stmt.executeUpdate()
            }
        }
    }

    override fun update(producto: Operacion, id: Int) {
        Dataobject.getDataSource().connection.use { connection ->
            connection.prepareStatement("UPDATE Operaciones SET num1 = ?, operador = ?, num2 = ?, resultado = ? WHERE id = ?").use { stmt ->
                stmt.setDouble(1, producto.num1)
                stmt.setString(2, producto.operador?.simbolos?.firstOrNull())
                stmt.setDouble(3, producto.num2)
                stmt.setDouble(4, producto.resultado)
                stmt.setInt(5, id)
                stmt.executeUpdate()
            }
        }
    }

    override fun delete(id: Int) {
        Dataobject.getDataSource().connection.use { connection ->
            connection.prepareStatement("DELETE FROM Operaciones WHERE id = ?").use { stmt ->
                stmt.setInt(1, id)
                stmt.executeUpdate()
            }
        }
    }


}