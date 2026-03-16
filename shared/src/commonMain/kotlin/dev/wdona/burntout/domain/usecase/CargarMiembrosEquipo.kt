package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario

class CargarMiembrosEquipo(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(idEquipo: Long) : List<Usuario> {
        return usuarioRepository.getUsuariosByEquipo(idEquipo)
    }
}