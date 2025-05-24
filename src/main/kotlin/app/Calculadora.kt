package es.iesraprog2425.pruebaes.app

import es.iesraprog2425.pruebaes.data.dao.IOperacionDAO
import es.iesraprog2425.pruebaes.model.Operacion
import es.iesraprog2425.pruebaes.model.Operadores
import es.iesraprog2425.pruebaes.redondear
import es.iesraprog2425.pruebaes.ui.IEntradaSalida

class Calculadora(private val ui: IEntradaSalida, private val operacionDAO: IOperacionDAO) {

    fun pedirNumero(): Double{
        var valorValido = false
        var input = 0.0
        while (!valorValido){
            print(">> ")
            try {
                input = readln().toDouble()
                valorValido = true
            } catch (e: IllegalArgumentException){
                println("**ERROR** Introduce un Nº")
            }
        }
        return input
    }

    private fun pedirInfo(): Triple<Double, Operadores, Double> {
        var valorValido = false
        var num1 = 0.0
        var num2 = 0.0
        var operador = Operadores.SUMA

        ui.mostrar("Introduce el 1º numero")
        num1 = pedirNumero()

        while (!valorValido) {
            try {
                operador = Operadores.getOperador(ui.pedirInfo("Introduce el operador (+, -, *, /)\n>>  ").firstOrNull().toString())
                    ?: throw InfoCalcException("El operador no es válido!")
                valorValido = true
            } catch (e: InfoCalcException) {
                ui.mostrarError("${e.message}")
            }
        }

        ui.mostrar("Introduce el 2º numero")
        num2 = pedirNumero()

        return Triple(num1, operador, num2)
    }

    private fun realizarCalculo(numero1: Double, operador: Operadores, numero2: Double) =
        when (operador) {
            Operadores.SUMA -> numero1 + numero2
            Operadores.RESTA -> numero1 - numero2
            Operadores.MULTIPLICACION -> numero1 * numero2
            Operadores.DIVISION -> numero1 / numero2
        }

    fun pedirArgumentosInicialesEIniciar(){ // Esta funcion esta creada para una cosa que pedia la calculado con logs en .txt que entendi mal, aunque lo he refactorizado para que funcione prefiero no usarla

        var ruta: String
        ui.mostrar("--- CALCUALDORA ---")
        ui.mostrar("Introduce los argumentos (Opcional)")
        ui.mostrar("Intrucciones \n- Introduzca el 1º Nº, el operador y el 2º Nº separado por espacios \n- En caso de no querer introducirlo pulse intro")
        print(">> ")
        var argumentos = readln()
        if (argumentos.isNotBlank()) {
            if (argumentos.split(" ").size == 3) {
                val inputs = argumentos.split(" ")
                    ruta = inputs[0]
                    try {
                        val num1 = inputs[1].toDouble()
                        val operador = Operadores.getOperador(inputs[2].firstOrNull().toString())
                        if (operador == null) throw InfoCalcException("Operador no valido")
                        val num2 = inputs[3].toDouble()
                        val resultado = realizarCalculo(num1, operador, num2)
                        ui.mostrar("$num1 ${operador.simbolos[0]} $num2 = ${resultado.redondear(2)}")
                        operacionDAO.add(Operacion(num1 = num1, operador = operador, num2 = num2, resultado = resultado.redondear(2)))
                    } catch (e: Exception) {
                        println("**UNEXPECTED ERROR** $e")
                    } catch (e: InfoCalcException) {
                        println("**ERROR** al realizar el calculo")
                    }
                    ui.limpiarPantalla(4)
                    ui.mostrar("Presione Enter/intro para continuar")
                    readln()
                    iniciar()
            } else {
                ui.mostrarError("Cantidad de argumento no valida")
            }
        } else {
            iniciar()
        }
    }

    fun iniciar() {
        do {
            try {
                ui.mostrar("--- Antiguas operaciones ---")
                operacionDAO.getAll().forEach { println(it) }
                ui.limpiarPantalla(2)
                ui.mostrar("--- CALCULADORA ---")
                val (numero1, operador, numero2) = pedirInfo()
                val resultado = realizarCalculo(numero1, operador, numero2)
                ui.mostrar("$numero1 ${operador.simbolos[0]} $numero2 = ${resultado.redondear(2)}")
                operacionDAO.add(Operacion(num1 = numero1, operador = operador, num2 = numero2, resultado = resultado.redondear(2)))
            } catch (e: NumberFormatException) {
                ui.mostrarError(e.message ?: "Se ha producido un error!")
            }
        } while (ui.preguntar())
        ui.limpiarPantalla()
    }

}