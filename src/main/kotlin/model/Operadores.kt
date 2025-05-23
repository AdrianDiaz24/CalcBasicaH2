package es.iesraprog2425.pruebaes.model

import es.iesraprog2425.pruebaes.ui.Consola

enum class Operadores(val simbolos: List<Char>) {
    SUMA(listOf('+')),
    RESTA(listOf('-')),
    MULTIPLICACION(listOf('*', 'x')),
    DIVISION(listOf('/', ':'));

    companion object {

        fun getOperador(operador: Char?) = operador?.let { op -> entries.find { op in it.simbolos } }

        /*
        private fun buscarOperador(operador: Char, simbolos: List<Char>): Boolean {
            for (simbolo in simbolos) {
                if (simbolo == operador) {
                    return true
                }
            }
            return false
        }

        fun getOperador2(operador: Char?): Operadores? {
            if (operador != null) {
                for (valor in entries) {
                    if (buscarOperador(operador, valor.simbolos)) {
                        return valor
                    }
                }
            }
            return null
        }
        */
    }
}