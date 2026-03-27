package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.PreguntaRespuestaApi
import dev.wdona.burntout.domain.model.Respuesta
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class PreguntaRespuestaApiImpl(private val client: HttpClient = ApiClient.client) : PreguntaRespuestaApi {
    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> =
        client.get("preguntas?idOrg=$idOrg").body()

    override suspend fun crearPregunta(pregunta: Pregunta): HttpResponse =
        client.post("preguntas") { setBody(pregunta) }

    override suspend fun actualizarPregunta(pregunta: Pregunta): HttpResponse =
        client.put("preguntas/${pregunta.idPregunta}") { setBody(pregunta) }

    override suspend fun eliminarPregunta(idPregunta: Long): HttpResponse =
        client.delete("preguntas/$idPregunta")

    override suspend fun responderPregunta(respuesta: Respuesta): HttpResponse =
        client.post("respuestas") { setBody(respuesta) }

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> =
        client.get("respuestas?idPregunta=$idPregunta").body()

    override suspend fun getRespuestasByIdUsuario(idUser: Long): List<Respuesta> =
        client.get("respuestas?idUsuario=$idUser").body()

    override suspend fun getLastRespuestasByIdUsuario(idUser: Long): List<Respuesta> =
        client.get("respuestas?idUsuario=$idUser&last=true").body()

    override suspend fun getRespuestasByIdUsuarioAndDate(idUser: Long, date: Long): List<Respuesta> =
        client.get("respuestas?idUsuario=$idUser&fecha=$date").body()
}
