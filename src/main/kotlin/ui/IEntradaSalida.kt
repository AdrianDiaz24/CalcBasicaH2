package es.iesraprog2425.pruebaes.ui

import es.iesraprog2425.pruebaes.model.Operadores

interface IEntradaSalida {
    fun mostrar(msj: String, salto: Boolean = true)
    fun mostrarError(msj: String, salto: Boolean = true)
    fun entrada(): String?
    fun pedirInfo(): Triple<Double, Operadores, Double>
    fun pedirNumero(): Double
    fun pedirNumeroConLimites(min: Int, max: Int): Int
    fun preguntar(msj: String = "¿Deseas intentarlo de nuevo? (s/n): "): Boolean
    fun limpiarPantalla(numSaltos: Int = 20)
    fun mostrarMenu(menu: Array<String>)
}