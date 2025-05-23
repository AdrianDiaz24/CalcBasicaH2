package es.iesraprog2425.pruebaes.data.dao

import es.iesraprog2425.pruebaes.model.Operaciones

interface ILogsDAO {

    fun getAll(): List<Operaciones>

    fun getById(id: Int): Operaciones?

    fun add(producto: Operaciones)

    fun update(producto: Operaciones, id: Int)

    fun delete(id: Int)

}