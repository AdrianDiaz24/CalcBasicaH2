package es.iesraprog2425.pruebaes.data

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RepoLogs: IRepoLogs {
    override var logs: MutableList<String> = mutableListOf()

    override fun subirLogs(ruta: String) {
        val formatoFecha = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val archivo = File("$ruta/log${LocalDateTime.now().format(formatoFecha)}.txt")
        for (log in logs) {
            archivo.appendText("$log\n")
        }
        logs = mutableListOf()
    }

    override fun mostrarUltimoLog(ruta: String) {
        val archivo = buscarUltimoLog(ruta)
        if (archivo !== null) {
            val lineas = archivo.readLines()
            for (linea in lineas) {
                println(linea)
            }
        } else {
            println("No existen ficheros de Log")
        }
    }

    override fun buscarUltimoLog(ruta: String): File? {
        val directorio = File(ruta)
        val logs = directorio.listFiles { archivos -> archivos.isFile && archivos.name.startsWith("log") && archivos.name.endsWith(".txt") }
        var fechaMasNueva = "0"
        for (log in logs) {
            val fechaLog = log.nameWithoutExtension.replace("log", "")
            if (fechaLog > fechaMasNueva) {
                fechaMasNueva = fechaLog
            }
        }
        return if (fechaMasNueva != "0") File("$ruta/log$fechaMasNueva.txt") else null
    }

    override fun comprobarRuta(ruta: String): String {
        val directorio = File(ruta)
        if (!directorio.exists()){
            directorio.mkdirs()
            return "Ruta $ruta creada"
        }
        return ""
    }
}