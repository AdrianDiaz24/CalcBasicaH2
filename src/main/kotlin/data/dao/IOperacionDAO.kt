package es.iesraprog2425.pruebaes.data.dao

import es.iesraprog2425.pruebaes.model.Operacion

interface IOperacionDAO {

    fun getAll(): List<Operacion>

    fun getAllWithLimit(): List<Operacion>

    fun getById(id: Int): Operacion?

    fun add(operacion: Operacion)

    fun update(operacion: Operacion, id: Int)

    fun delete(id: Int)

    fun inicializarTabla()

}