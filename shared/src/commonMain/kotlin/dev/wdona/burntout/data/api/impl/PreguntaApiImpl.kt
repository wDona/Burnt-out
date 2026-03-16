package dev.wdona.burntout.data.api.impl

import dev.wdona.burntout.data.api.PreguntaApi
import dev.wdona.burntout.shared.domain.Pregunta
import dev.wdona.burntout.shared.domain.Respuesta
import dev.wdona.burntout.shared.network.ApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

class PreguntaApiImpl(private val client: HttpClient = ApiClient.client) : PreguntaApi {
    override suspend fun getPreguntasByOrg(idOrg: Long): List<Pregunta> =
        client.get("/preguntas/organizacion=$idOrg").body()

    override suspend fun crearPregunta(pregunta: Pregunta): HttpResponse =
        client.post(
            "/nuevo/pregunta/pregunta=${pregunta.pregunta}" +
                    "&idOrg=${pregunta.idOrganizacion}"
        )

    override suspend fun actualizarPregunta(pregunta: Pregunta): HttpResponse =
        client.post(
            "/actualizar/pregunta/idPregunta=${pregunta.idPregunta}" +
                    "&pregunta=${pregunta.pregunta}"
        )

    override suspend fun eliminarPregunta(idPregunta: Long): HttpResponse =
        client.post("/eliminar/pregunta/idPregunta=$idPregunta")

    override suspend fun responderPregunta(respuesta: Respuesta): HttpResponse =
        client.post(
            "/nuevo/respuesta/idPregunta=${respuesta.idPregunta}" +
                    "&idUsuario=${respuesta.idUsuario}" +
                    "&respuesta=${respuesta.respuesta}" +
                    "&anonimo=${if (respuesta.anonimo) 1 else 0}"
        )

    override suspend fun getRespuestasByPregunta(idPregunta: Long): List<Respuesta> =
        client.get("/respuestas/pregunta=$idPregunta").body()
}

