package es.iesraprog2425.pruebaes

import es.iesraprog2425.pruebaes.app.Calculadora
import es.iesraprog2425.pruebaes.data.db.Dataobject
import es.iesraprog2425.pruebaes.ui.Consola
import java.math.RoundingMode

/*
fun main() {
    val scanner = Scanner(System.`in`)

    println("Introduce el primer número:")
    val numero1 = scanner.nextDouble()
    println("Introduce el operador (+, -, *, /):")
    val operador = scanner.next()[0]
    println("Introduce el segundo número:")
    val numero2 = scanner.nextDouble()

    val resultado = when (operador) {
        '+' -> numero1 + numero2
        '-' -> numero1 - numero2
        '*' -> numero1 * numero2
        '/' -> numero1 / numero2
        else -> "Operador no válido"
    }

    println("Resultado: $resultado")
}
*/

fun main() {
//   Creacion de la tabla, comentada para que no salte el error de que ya existe
//    val sql =
//    "CREATE TABLE Operaciones (\n" +
//        "id IDENTITY PRIMARY KEY,\n" +
//        "num1 DOUBLE NOT NULL,\n" +
//        "operador VARCHAR(10) NOT NULL,\n" +
//        "num2 DOUBLE NOT NULL,\n" +
//        "resultado DOUBLE\n" +
//    ");"
//
//
//    Dataobject.getDataSource().connection.use { conn ->
//        conn.createStatement().use { stmt ->
//            stmt.execute(sql)
//        }
//    }

    Calculadora(Consola()).iniciar()
    //Calculadora(Consola()).pedirArgumentosInicialesEIniciar()
}


fun Double.redondear(decimales: Int):Double{
    return this.toBigDecimal().setScale(decimales, RoundingMode.HALF_UP).toDouble()
}

/*
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    val numLineas = scanner.nextInt()
    scanner.nextLine() // Limpia el salto de línea pendiente

    var resultado = 1

    for (i in 1..numLineas) {
        var suma = 0
        while (scanner.hasNextInt()) {
            suma += scanner.nextInt()
        }
        resultado *= suma
        if (scanner.hasNextLine()) scanner.nextLine() // pasar a la siguiente línea
    }

    println(resultado)
}
*/