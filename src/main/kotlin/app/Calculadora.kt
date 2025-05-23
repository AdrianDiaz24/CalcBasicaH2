package es.iesraprog2425.pruebaes.app

import es.iesraprog2425.pruebaes.data.RepoLogs
import es.iesraprog2425.pruebaes.model.Operadores
import es.iesraprog2425.pruebaes.redondear
import es.iesraprog2425.pruebaes.ui.Consola
import es.iesraprog2425.pruebaes.ui.IEntradaSalida

class Calculadora(private val ui: IEntradaSalida, private val repoLogs: RepoLogs = RepoLogs()) {

    private fun pedirNumero(msj: String, msjError: String = "Número no válido!"): Double {
        return ui.pedirDouble(msj) ?: throw InfoCalcException(msjError)
    }

    private fun pedirInfo(): Triple<Double, Operadores, Double> {
        var valorValido = false
        var num1 = 0.0
        var num2 = 0.0
        var operador = Operadores.SUMA
        while (!valorValido) {
            try {
                num1 = pedirNumero("Introduce el primer número: ", "El primer número no es válido!")
                valorValido = true
            } catch (e: InfoCalcException) {
                repoLogs.agregarLog("${e.message}")
                ui.mostrarError("${e.message}")
            }
        }
        valorValido = false
        while (!valorValido) {
            try {
                operador = Operadores.getOperador(ui.pedirInfo("Introduce el operador (+, -, *, /): ").firstOrNull())
                    ?: throw InfoCalcException("El operador no es válido!")
                valorValido = true
            } catch (e: InfoCalcException) {
                repoLogs.agregarLog("${e.message}")
                ui.mostrarError("${e.message}")
            }
        }
        valorValido = false
        while (!valorValido) {
            try {
                num2 = pedirNumero("Introduce el segundo número: ", "El segundo número no es válido!")
                valorValido = true
            } catch (e: InfoCalcException) {
                repoLogs.agregarLog("${e.message}")
                ui.mostrarError("${e.message}")
            }
        }

        return Triple(num1, operador, num2)
    }

    private fun realizarCalculo(numero1: Double, operador: Operadores, numero2: Double) =
        when (operador) {
            Operadores.SUMA -> numero1 + numero2
            Operadores.RESTA -> numero1 - numero2
            Operadores.MULTIPLICACION -> numero1 * numero2
            Operadores.DIVISION -> numero1 / numero2
        }

    fun pedirArgumentosInicialesEIniciar(){

        var ruta: String

        ui.mostrar("Introduce los argumentos (Opcional)")
        ui.mostrar("Intrucciones \n- Introduzca solo la ruta donde copiar el log \n- Introduzca la ruta, el 1º Nº, el operador y el 2º Nº separado por espacios \n- En caso de no querer introducirlo pulse intro")
        print(">> ")
        var argumentos = readln()
        if (argumentos.isNotBlank()) {
            if (argumentos.split(" ").size == 4) {
                val inputs = argumentos.split(" ")
                    ruta = inputs[0]
                    try {
                        val num1 = inputs[1].toDouble()
                        val operador = Operadores.getOperador(inputs[2].firstOrNull())
                        if (operador == null) throw InfoCalcException("Operador no valido")
                        val num2 = inputs[3].toDouble()
                        repoLogs.comprobarRuta(ruta)
                        val resultado = realizarCalculo(num1, operador, num2)
                        ui.mostrar("$num1 ${operador.simbolos[0]} $num2 = ${resultado.redondear(2)}")
                        repoLogs.agregarLog("$num1 ${operador.simbolos[0]} $num2 = ${resultado.redondear(2)}")
                    } catch (e: Exception) {
                        repoLogs.agregarLog("argumentos no validos")
                        repoLogs.subirLogs(ruta)
                    } catch (e: InfoCalcException) {
                        repoLogs.agregarLog(e.message.toString())
                        repoLogs.subirLogs(ruta)
                    }
                    repoLogs.subirLogs(ruta)
                    ui.limpiarPantalla(4)
                    ui.mostrar("Presione Enter/intro para continuar")
                    readln()
                    iniciar(ruta)
            } else if (argumentos.split(" ").size > 4 || argumentos.split(" ").size in 2..3) {
                ui.mostrarError("Cantidad de argumento no valida")
            } else {
                ruta = argumentos
                iniciar(ruta)
            }
        } else {
            iniciar()
        }
    }

    fun iniciar(ruta: String = "./log") {
        do {
            try {
                println(repoLogs.comprobarRuta(ruta))
                repoLogs.mostrarUltimoLog(ruta)
                ui.limpiarPantalla(2)
                val (numero1, operador, numero2) = pedirInfo()
                val resultado = realizarCalculo(numero1, operador, numero2)
                ui.mostrar("$numero1 ${operador.simbolos[0]} $numero2 = ${resultado.redondear(2)}")
                repoLogs.agregarLog("$numero1 ${operador.simbolos[0]} $numero2 = ${resultado.redondear(2)}")
            } catch (e: NumberFormatException) {
                ui.mostrarError(e.message ?: "Se ha producido un error!")
            }
        } while (ui.preguntar())
        repoLogs.subirLogs(ruta)
        ui.limpiarPantalla()
    }

}