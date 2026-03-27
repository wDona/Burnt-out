package dev.wdona.burntout.latest

import dev.wdona.burntout.domain.model.Ajuste
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Equipo
import dev.wdona.burntout.shared.domain.Organizacion
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Subtarea
import dev.wdona.burntout.shared.domain.Tablero
import dev.wdona.burntout.shared.domain.Tarea
import dev.wdona.burntout.shared.domain.Usuario

internal val ajustesPorUsuario = mutableMapOf<Long, MutableList<Ajuste>>()
internal var ajusteIdContador = 1L

internal val equipos = mutableListOf<Equipo>()
internal var equipoIdContador = 1L

internal val organizaciones = mutableListOf<Organizacion>()
internal var organizacionIdContador = 1L

internal val tableros = mutableListOf<Tablero>()
internal var tableroIdContador = 1L

internal val tareas = mutableListOf<Tarea>()
internal var tareaIdContador = 1L

internal val subtareas = mutableListOf<Subtarea>()
internal var subtareaIdContador = 1L

internal val usuarios = mutableListOf<Usuario>()
internal var usuarioIdContador = 1L

internal val preguntas = mutableListOf<Pregunta>()
internal var preguntaIdContador = 1L

internal val respuestas = mutableListOf<Respuesta>()