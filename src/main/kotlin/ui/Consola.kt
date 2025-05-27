package es.iesraprog2425.pruebaes.ui

import es.iesraprog2425.pruebaes.app.InfoCalcException
import es.iesraprog2425.pruebaes.model.Operadores
import java.util.Scanner

class Consola : IEntradaSalida {
    private val scanner = Scanner(System.`in`)

    override fun mostrar(msj: String, salto: Boolean) {
        print("$msj${if (salto) "\n" else ""}")
    }

    override fun mostrarError(msj: String, salto: Boolean) {
        mostrar("ERROR - $msj", salto)
    }

    override fun pedirNumero(): Double{
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

    override fun pedirNumeroConLimites(min: Int, max: Int): Int {
        var valorValido = false
        var input = 0
        while (!valorValido){
            print(">> ")
            try {
                input = readln().toInt()
                if (input < min || input > max) throw IllegalArgumentException()
                valorValido = true
            } catch (e: IllegalArgumentException){
                println("**ERROR** Introduce un Nº entre $min y $max")
            }
        }
        return input
    }

    override fun entrada(): String {
        return readln().firstOrNull().toString()
    }

    override fun pedirInfo(): Triple<Double, Operadores, Double> {
        var valorValido = false
        var num1 = 0.0
        var num2 = 0.0
        var operador = Operadores.SUMA

        mostrar("Introduce el 1º numero")
        num1 = pedirNumero()

        while (!valorValido) {
            try {
                mostrar("Introduce el operador (+, -, *, /)\n>> ", false)
                operador = Operadores.getOperador(entrada())
                    ?: throw InfoCalcException("El operador no es válido!")
                valorValido = true
            } catch (e: InfoCalcException) {
                mostrarError("${e.message}")
            }
        }

        mostrar("Introduce el 2º numero")
        num2 = pedirNumero()

        return Triple(num1, operador, num2)
    }

    override fun preguntar(msj: String): Boolean {
        do {
            mostrar(msj)
            val respuesta = entrada().lowercase()
            when (respuesta) {
                "s", "si" -> return true
                "n", "no" -> return false
                else -> mostrarError("Respuesta no válida. Responde con s, n, si o no.")
            }
        } while (true)
    }

    override fun limpiarPantalla(numSaltos: Int) {
        if (System.console() != null) {
            mostrar("\u001b[H\u001b[2J", false)
            System.out.flush()
        } else {
            repeat(numSaltos) {
                mostrar("")
            }
        }
    }

    override fun mostrarMenu(menu: Array<String>) {
        menu.forEachIndexed { num, fila ->
            if (num == 0) mostrar(fila) else mostrar("$num. $fila")
        }
    }

}

