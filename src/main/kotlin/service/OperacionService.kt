package es.iesraprog2425.pruebaes.service

import es.iesraprog2425.pruebaes.data.dao.OperacionDAO
import es.iesraprog2425.pruebaes.model.Operacion

class OperacionService(val dao: OperacionDAO): IOperacionService {
    override fun getAll(): List<Operacion> {
        return dao.getAll()
    }

    override fun getAllWithLimit(): List<Operacion> {
        return dao.getAllWithLimit()
    }

    override fun getById(id: Int): Operacion? {
        require(id > 0) {throw IllegalArgumentException("La ID debe ser mayor a cero")}
        return dao.getById(id)
    }

    override fun add(operacion: Operacion) {
        dao.add(operacion)
    }

    override fun update(operacion: Operacion, id: Int) {
        require(id > 0) {throw IllegalArgumentException("La ID debe ser mayor a cero")}
        dao.update(operacion, id)
    }

    override fun delete(id: Int) {
        require(id > 0) {throw IllegalArgumentException("La ID debe ser mayor a cero")}
        dao.delete(id)
    }

    override fun inicializarTabla() {
        dao.inicializarTabla()
    }


}