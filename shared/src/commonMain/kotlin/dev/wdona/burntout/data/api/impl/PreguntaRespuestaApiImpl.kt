package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.PreguntaRespuestaApi
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.network.ApiClient
import dev.wdona.burntout.shared.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.HttpResponse

class PreguntaRespuestaApiImpl(private val client: HttpClient = ApiClient.client) : PreguntaRespuestaApi {
    private val TAG = "PreguntaRespuestaApiImpl"

    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> {
        Logger.d(TAG, "getPreguntasByOrg: $idOrg")
        return client.get("preguntas?idOrg=$idOrg").body()
    }

    override suspend fun crearPregunta(pregunta: Pregunta): HttpResponse {
        Logger.d(TAG, "crearPregunta: $pregunta")
        return client.post("preguntas") {
            contentType(ContentType.Application.Json)
            setBody(pregunta)
        }
    }

    override suspend fun actualizarPregunta(pregunta: Pregunta): HttpResponse {
        Logger.d(TAG, "actualizarPregunta: $pregunta")
        return client.put("preguntas/${pregunta.idPregunta}") {
            contentType(ContentType.Application.Json)
            setBody(pregunta)
        }
    }

    override suspend fun eliminarPregunta(idPregunta: Long): HttpResponse {
        Logger.d(TAG, "eliminarPregunta: $idPregunta")
        return client.delete("preguntas/$idPregunta")
    }

    override suspend fun responderPregunta(respuesta: Respuesta): HttpResponse {
        Logger.d(TAG, "responderPregunta: $respuesta")
        return client.post("respuestas") {
            contentType(ContentType.Application.Json)
            setBody(respuesta)
        }
    }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> {
        Logger.d(TAG, "getRespuestasByPregunta: $idPregunta")
        return client.get("respuestas?idPregunta=$idPregunta").body()
    }

    override suspend fun getRespuestasByIdUsuario(idUser: Long): List<Respuesta> {
        Logger.d(TAG, "getRespuestasByIdUsuario: $idUser")
        return client.get("respuestas?idUsuario=$idUser").body()
    }

    override suspend fun getLastRespuestasByIdUsuario(idUser: Long): List<Respuesta> {
        Logger.d(TAG, "getLastRespuestasByIdUsuario: $idUser")
        return client.get("respuestas?idUsuario=$idUser&last=true").body()
    }

    override suspend fun getRespuestasByIdUsuarioAndDate(idUser: Long, date: Long): List<Respuesta> {
        Logger.d(TAG, "getRespuestasByIdUsuarioAndDate: user=$idUser, date=$date")
        return client.get("respuestas?idUsuario=$idUser&fecha=$date").body()
    }
}
