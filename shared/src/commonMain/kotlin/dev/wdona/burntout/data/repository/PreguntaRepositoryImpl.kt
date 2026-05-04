package dev.wdona.burntout.data.repository

import dev.wdona.burntout.domain.repository.PreguntaRespuestaRepository
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.PreguntaRespuestaLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.PreguntaMapper
import dev.wdona.burntout.data.datasource.mapper.RespuestaMapper
import dev.wdona.burntout.data.datasource.remote.PreguntaRespuestaRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.utils.SettingsManager
import dev.wdona.burntout.shared.utils.getCurrentTimestampSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreguntaRespuestaRepositoryImpl(
    private val local: PreguntaRespuestaLocalDataSource,
    private val remote: PreguntaRespuestaRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : PreguntaRespuestaRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> = withContext(NonCancellable + Dispatchers.IO) {
        val currentLocal = local.getPreguntasByOrg(idOrg)
        if (currentLocal.isEmpty() && !SettingsManager.isUsuarioInvitado()) {
            try {
                val remoteList = remote.getPreguntasByOrg(idOrg)
                remoteList.forEach { local.upsertPregunta(it) }
                return@withContext remoteList
            } catch (e: Exception) {
                println("Error bajando preguntas iniciales: ${e.message}")
            }
        } else if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val remoteList = remote.getPreguntasByOrg(idOrg)
                    remoteList.forEach {
                         local.upsertPregunta(it)
                    }
                } catch (e: Exception) {
                    println("Servidor invitado (getPreguntas): ${e.message}")
                }
            }
        }
        local.getPreguntasByOrg(idOrg)
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val remoteList = remote.getRespuestasByPregunta(idPregunta)
                    remoteList.forEach {
                        local.responderPregunta(it)
                    }
                } catch (e: Exception) {
                    println("Servidor invitado (getRespuestas): ${e.message}")
                }
            }
        }
        local.getRespuestasByPregunta(idPregunta)
    }

    override suspend fun getRespuestasByIdUsuario(idUser: Long): List<Respuesta> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val remoteList = remote.getRespuestasByIdUsuario(idUser)
                    remoteList.forEach {
                        local.responderPregunta(it)
                    }
                } catch (e: Exception) {
                    println("Servidor invitado (getRespuestasByIdUsuario): ${e.message}")
                }
            }
        }
        local.getRespuestasByIdUsuario(idUser)
    }

    override suspend fun getLastRespuestasByIdUsuario(idUser: Long): List<Respuesta> = withContext(NonCancellable + Dispatchers.IO) {
        // En este caso priorizamos la base de datos local que ya tiene el histórico,
        // opcionalmente podríamos pedir al server las "últimas" si el endpoint existiera.
        // Dado que syncronizamos en otros puntos, confiamos en lo local.
        local.getLastRespuestasByIdUsuario(idUser)
    }

    override suspend fun getRespuestasByIdUsuarioAndDate(idUser: Long, date: Long): List<Respuesta> = withContext(NonCancellable + Dispatchers.IO) {
        if (!SettingsManager.isUsuarioInvitado()) {
            repositoryScope.launch {
                try {
                    val remoteList = remote.getRespuestasByIdUsuarioAndDate(idUser, date)
                    remoteList.forEach {
                        local.responderPregunta(it)
                    }
                } catch (e: Exception) {
                    println("Servidor invitado (getRespuestasByIdUsuarioAndDate): ${e.message}")
                }
            }
        }
        local.getRespuestasByIdUsuarioAndDate(idUser, date)
    }

    override suspend fun crearPregunta(pregunta: Pregunta) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.crearPregunta(pregunta)
            } catch (e: Exception) {
                println("Error local: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            var idRemoto: Long = -1
            try {
                idRemoto = remote.crearPregunta(pregunta)
                exito = idRemoto != -1L
            } catch (e: Exception) {
                println("Servidor invitado: ${e.message}")
            }
            savePendingOp(
                TipoAccion.CREACION,
                Entity.PREGUNTA,
                if (exito) idRemoto else 0L,
                PreguntaMapper.toJson(pregunta),
                exito
            )
        }
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.actualizarPregunta(pregunta)
            } catch (e: Exception) {
                println("Error local al actualizar pregunta: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.actualizarPregunta(pregunta)
            } catch (e: Exception) {
                println("Servidor invitado: ${e.message}")
            }
            savePendingOp(
                TipoAccion.ACTUALIZACION,
                Entity.PREGUNTA,
                pregunta.idPregunta,
                PreguntaMapper.toJson(pregunta),
                exito
            )
        }
    }

    override suspend fun eliminarPregunta(idPregunta: Long) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.eliminarPregunta(idPregunta)
            } catch (e: Exception) {
                println("Error local al eliminar pregunta: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                exito = remote.eliminarPregunta(idPregunta)
            } catch (e: Exception) {
                println("Servidor invitado: ${e.message}")
            }
            savePendingOp(
                TipoAccion.ELIMINACION,
                Entity.PREGUNTA,
                idPregunta,
                "",
                exito
            )
        }
    }

    override suspend fun responderPregunta(respuesta: Respuesta) {
        withContext(NonCancellable + Dispatchers.IO) {
            try {
                local.responderPregunta(respuesta)
            } catch (e: Exception) {
                println("Error local al responder pregunta: ${e.message}")
            }
        }
        if (SettingsManager.isUsuarioInvitado()) return
        withContext(NonCancellable + Dispatchers.IO) {
            var exito = false
            try {
                remote.responderPregunta(respuesta)
                exito = true
            } catch (e: Exception) {
                println("Servidor invitado: ${e.message}")
            }
            savePendingOpString(
                TipoAccion.CREACION,
                Entity.RESPONDER,
                respuesta.idRespuesta,
                RespuestaMapper.toJson(respuesta),
                exito
            )
        }
    }

    private suspend fun savePendingOp(tipo: TipoAccion, entity: Entity, id: Long, json: String, success: Boolean) {
        savePendingOpString(tipo, entity, id.toString(), json, success)
    }

    private suspend fun savePendingOpString(tipo: TipoAccion, entity: Entity, id: String, json: String, success: Boolean) {
        withContext(NonCancellable + Dispatchers.IO) {
             pendiente.insertOperacionPendiente(
                 tipo.getNombreAccion(),
                 entity.getNombreEntity(),
                 id,
                 json,
                 getCurrentTimestampSeconds(),
                 if (success) 1L else 0L
             )
        }
    }
}