package es.iesraprog2425.pruebaes.app

import es.iesraprog2425.pruebaes.model.Operacion
import es.iesraprog2425.pruebaes.model.Operadores
import es.iesraprog2425.pruebaes.redondear
import es.iesraprog2425.pruebaes.service.IOperacionService
import es.iesraprog2425.pruebaes.ui.IEntradaSalida

class Calculadora(private val ui: IEntradaSalida, private val operacionService: IOperacionService) {

    private fun realizarCalculo(numero1: Double, operador: Operadores, numero2: Double) =
        when (operador) {
            Operadores.SUMA -> numero1 + numero2
            Operadores.RESTA -> numero1 - numero2
            Operadores.MULTIPLICACION -> numero1 * numero2
            Operadores.DIVISION -> numero1 / numero2
        }

    fun iniciar() { // Funcion refactorizada y ampliada actualizar el punto 6 del README.md
        var salida = false
        do {
            try {
                ui.mostrarMenu(arrayOf("--- MENU ---", "Realizar operacion", "Mostrar operaciones anteriores", "Eliminar operacion del historial", "Borrar historial", "Salir"))
                val eleccionMenu = ui.pedirNumeroConLimites(1, 5)
                when (eleccionMenu) {
                    1 -> {
                        ui.mostrar("--- Antiguas operaciones ---")
                        operacionService.getAllWithLimit().forEach { ui.mostrar(it.toString()) }
                        ui.limpiarPantalla(2)
                        ui.mostrar("--- CALCULADORA ---")
                        val (numero1, operador, numero2) = ui.pedirInfo()
                        val resultado = realizarCalculo(numero1, operador, numero2)
                        ui.mostrar("$numero1 ${operador.simbolos[0]} $numero2 = ${resultado.redondear(2)}")
                        operacionService.add(Operacion(num1 = numero1, operador = operador, num2 = numero2, resultado = resultado.redondear(2)))
                    }
                    2 -> {
                        ui.mostrar("--- OPERACIONES ANTERIORES ---")
                        operacionService.getAll().forEach { ui.mostrar(it.toString()) }
                    }
                    3 -> {
                        ui.mostrar("--- ELIMINAR OPERACION ---")
                        ui.mostrar("Introduzca la ID de la operacion a eliminar:")
                        val id = ui.pedirNumeroConLimites(1, operacionService.getAll().size)
                        operacionService.delete(id)
                    }
                    4 -> {
                        operacionService.inicializarTabla()
                        ui.mostrar("--- Historial Borrado ---\n")
                    }
                    5 -> salida = true
                }

            } catch (e: NumberFormatException) {
                ui.mostrarError(e.message ?: "Se ha producido un error!")
            }
        } while (!salida)
        ui.limpiarPantalla()
    }

}