package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.repository.UsuarioRepository
import dev.wdona.burntout.shared.domain.Usuario

class GetUsuarioByUsernameUseCase(private val usuarioRepository: UsuarioRepository) {
    suspend operator fun invoke(id: Long): Usuario {
        return usuarioRepository.getUserById(id)
    }
}
