package dev.wdona.burntout.commands

import dev.wdona.burntout.db.DatabaseFactory
import kotlin.system.exitProcess

internal suspend fun comandoHandler(comando: String, args: List<String> = emptyList()) {
    when (comando) {
        "/exit", "/quit", "/leave", "/stop" -> {
            println("Saliendo...")
            exitProcess(0)
        }
        "/test" -> {
            println("Comando de prueba ejecutado con argumentos: $args")
        }
        "/cleardb" -> {
            println("Limpiando base de datos...")
            DatabaseFactory.clearDB()
            println("Base de datos limpiada")
        }
        else -> {
            if (comando.startsWith("/")) println("Comando desconocido: $comando")
            else println("Los comandos deben empezar por /")
        }
    }
}