package es.iesraprog2425.pruebaes.data.dao

import es.iesraprog2425.pruebaes.model.Operacion

interface ILogsDAO {

    fun getAll(): List<Operacion>

    fun getById(id: Int): Operacion?

    fun add(producto: Operacion)

    fun update(producto: Operacion, id: Int)

    fun delete(id: Int)

}