package es.iesraprog2425.pruebaes.data.dao

import es.iesraprog2425.pruebaes.data.db.Dataobject
import es.iesraprog2425.pruebaes.model.Operaciones
import es.iesraprog2425.pruebaes.model.Operadores

class LogsDAO: ILogsDAO {
    override fun getAll(): List<Operaciones> {
        val operaciones = mutableListOf<Operaciones>()
        Dataobject.getDataSource().connection.use { connection ->
            connection.prepareStatement("SELECT * FROM Operaciones").use { stmt ->
                val resultados = stmt.executeQuery()
                while (resultados.next()){
                    operaciones.add(
                        Operaciones(
                            id = resultados.getInt("id"),
                            num1 = resultados.getDouble("num1"),
                            operador = Operadores.getOperador(resultados.getString("operador")),
                            num2
                        )
                    )
                }
            }
        }
    }

    override fun getById(id: Int): Operaciones? {
        TODO("Not yet implemented")
    }

    override fun add(producto: Operaciones) {
        TODO("Not yet implemented")
    }

    override fun update(producto: Operaciones, id: Int) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }


}