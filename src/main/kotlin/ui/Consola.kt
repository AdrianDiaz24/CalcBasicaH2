package es.iesraprog2425.pruebaes.ui

import java.util.Scanner

class Consola : IEntradaSalida {
    private val scanner = Scanner(System.`in`)

    override fun mostrar(msj: String, salto: Boolean) {
        print("$msj${if (salto) "\n" else ""}")
    }

    override fun mostrarError(msj: String, salto: Boolean) {
        mostrar("ERROR - $msj", salto)
    }

    override fun pedirInfo(msj: String): String {
        if (msj.isNotEmpty()) mostrar(msj, false)
        return scanner.nextLine().trim()
    }

    override fun pedirDouble(msj: String): Double? {
        return pedirInfo(msj).replace(',', '.').toDoubleOrNull()
    }

    override fun pedirEntero(msj: String): Int? {
        return pedirInfo(msj).toIntOrNull()
    }

    override fun preguntar(msj: String): Boolean {
        do {
            val respuesta = pedirInfo(msj).lowercase()
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

}

