package es.iesraprog2425.pruebaes

import es.iesraprog2425.pruebaes.app.Calculadora
import es.iesraprog2425.pruebaes.data.dao.OperacionDAO
import es.iesraprog2425.pruebaes.data.db.Dataobject
import es.iesraprog2425.pruebaes.service.OperacionService
import es.iesraprog2425.pruebaes.ui.Consola
import java.math.RoundingMode

fun main() {

    val dataSource =  Dataobject.getDataSource()

    Calculadora(Consola(), OperacionService(OperacionDAO(dataSource))).iniciar()

}

fun Double.redondear(decimales: Int):Double{
    return this.toBigDecimal().setScale(decimales, RoundingMode.HALF_UP).toDouble()
}
