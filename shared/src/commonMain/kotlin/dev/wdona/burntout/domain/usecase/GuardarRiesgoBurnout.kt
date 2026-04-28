package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.repository.PreguntaRespuestaRepository
import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.utils.SettingsManager

class GuardarRiesgoBurnout(
    private val usuarioRepository: UsuarioRepository,
    private val preguntaRespuestaRepository: PreguntaRespuestaRepository
) {
    suspend operator fun invoke(idUsuario: Long) {
        val respuestas = preguntaRespuestaRepository.getRespuestasByIdUsuario(idUsuario)
        val preguntas = preguntaRespuestaRepository.getPreguntasByOrg(SettingsManager.getIdOrganizacionActual())
        val riesgoBurnout = CalcularRiesgoBurnout().invoke(respuestas, preguntas)
        usuarioRepository.updateRiesgoBurnout(idUsuario, riesgoBurnout.riesgoTotal)

        SettingsManager.setRiesgoCEUsuarioActual(riesgoBurnout.scoreCE)
        SettingsManager.setRiesgoDUsuarioActual(riesgoBurnout.scoreD)
        SettingsManager.setRiesgoRPUsuarioActual(riesgoBurnout.scoreRP)
    }
}