package es.iesraprog2425.pruebaes.service

import es.iesraprog2425.pruebaes.model.Operacion

interface IOperacionService {

    fun getAll(): List<Operacion>

    fun getAllWithLimit(): List<Operacion>

    fun getById(id: Int): Operacion?

    fun add(producto: Operacion)

    fun update(producto: Operacion, id: Int)

    fun delete(id: Int)

    fun inicializarTabla()

}