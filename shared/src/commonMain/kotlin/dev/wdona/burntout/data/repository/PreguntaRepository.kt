package dev.wdona.burntout.data.repository

import dev.wdona.burntout.data.dao.PreguntaRepository
import dev.wdona.burntout.data.datasource.local.OperacionPendienteLocalDataSource
import dev.wdona.burntout.data.datasource.local.PreguntaLocalDataSource
import dev.wdona.burntout.data.datasource.mapper.PreguntaMapper
import dev.wdona.burntout.data.datasource.mapper.RespuestaMapper
import dev.wdona.burntout.data.datasource.remote.PreguntaRemoteDataSource
import dev.wdona.burntout.domain.entity.Entity
import dev.wdona.burntout.domain.model.TipoAccion
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreguntaRepositoryImpl(
    private val local: PreguntaLocalDataSource,
    private val remote: PreguntaRemoteDataSource,
    private val pendiente: OperacionPendienteLocalDataSource
) : PreguntaRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default)

    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> = withContext(Dispatchers.IO) {
        repositoryScope.launch {
            try {
                val remoteList = remote.getPreguntasByOrg(idOrg)
                remoteList.forEach { 
                     local.upsertPregunta(it)
                }
            } catch (e: Exception) {
                println("Servidor offline (getPreguntas): ${e.message}")
            }
        }
        local.getPreguntasByOrg(idOrg)
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> = withContext(Dispatchers.IO) {
        repositoryScope.launch {
            try {
                val remoteList = remote.getRespuestasByPregunta(idPregunta)
                remoteList.forEach {
                    local.responderPregunta(it)
                }
            } catch (e: Exception) {
                println("Servidor offline (getRespuestas): ${e.message}")
            }
        }
        local.getRespuestasByPregunta(idPregunta)
    }

    override suspend fun crearPregunta(pregunta: Pregunta) {
        withContext(Dispatchers.IO) {
            try {
                local.crearPregunta(pregunta)
            } catch (e: Exception) {
                println("Error local: ${e.message}")
            }
        }
        repositoryScope.launch {
            var exito = false
            var idRemoto: Long = -1
            try {
                idRemoto = remote.crearPregunta(pregunta)
                exito = idRemoto != -1L
            } catch (e: Exception) {
                println("Servidor offline: ${e.message}")
            }
            
            savePendingOp(
                TipoAccion.CREACION, 
                Entity.PREGUNTA,
                if(exito) idRemoto else 0L,
                PreguntaMapper.toJson(pregunta),
                exito
            )
        }
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta) {
        withContext(Dispatchers.IO) {
             local.actualizarPregunta(pregunta)
        }
        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.actualizarPregunta(pregunta)
            } catch (e: Exception) {
                println("Servidor offline: ${e.message}")
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
        withContext(Dispatchers.IO) {
            local.eliminarPregunta(idPregunta)
        }
        repositoryScope.launch {
            var exito = false
            try {
                exito = remote.eliminarPregunta(idPregunta)
            } catch (e: Exception) {
                 println("Servidor offline: ${e.message}")
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
        withContext(Dispatchers.IO) {
            local.responderPregunta(respuesta)
        }
        repositoryScope.launch {
            var exito = false
            try {
                remote.responderPregunta(respuesta)
                exito = true
            } catch (e: Exception) {
                 println("Servidor offline: ${e.message}")
            }
            savePendingOp(
                TipoAccion.CREACION,
                Entity.RESPONDER,
                0L, // Composite key, relies on JSON content
                RespuestaMapper.toJson(respuesta),
                exito
            )
        }
    }
    
    private suspend fun savePendingOp(tipo: TipoAccion, entity: Entity, id: Long, json: String, success: Boolean) {
        withContext(Dispatchers.IO) {
             pendiente.insertOperacionPendiente(
                 tipo.getNombreAccion(),
                 entity.getNombreEntity(),
                 id,
                 json,
                 System.currentTimeMillis(),
                 if (success) 1L else 0L
             )
        }
    }
}