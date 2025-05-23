package es.iesraprog2425.pruebaes.model

data class Operacion(val id: Int = 0, val num1: Double, val operador: Operadores?, val num2: Double, val resultado: Double) {

    override fun toString(): String {
        return "ID $id: $num1 ${operador?.simbolos?.getOrNull(0) ?: "?"} $num2 = $resultado"
    }

}
