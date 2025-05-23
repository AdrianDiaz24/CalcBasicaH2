package es.iesraprog2425.pruebaes.data

import java.io.File

interface IRepoLogs {

    val logs: MutableList<String>

    fun agregarLog(log: String): Boolean{
        return logs.add(log)
    }

    fun subirLogs(ruta: String = "./log")

    fun mostrarUltimoLog(ruta: String = "./log")

    fun buscarUltimoLog(ruta: String = "./log"): File?

    fun comprobarRuta(ruta: String = "./log"): String
}