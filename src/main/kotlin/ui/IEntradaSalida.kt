package es.iesraprog2425.pruebaes.ui

interface IEntradaSalida {
    fun mostrar(msj: String, salto: Boolean = true)
    fun mostrarError(msj: String, salto: Boolean = true)
    fun pedirInfo(msj: String = ""): String
    fun pedirDouble(msj: String = ""): Double?
    fun pedirEntero(msj: String = ""): Int?
    fun preguntar(msj: String = "¿Deseas intentarlo de nuevo? (s/n): "): Boolean
    fun limpiarPantalla(numSaltos: Int = 20)
}