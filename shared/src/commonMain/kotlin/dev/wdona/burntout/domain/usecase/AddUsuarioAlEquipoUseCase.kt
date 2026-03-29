package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.repository.EquipoRepository

class AddUsuarioAlEquipoUseCase(private val equipoRepository: EquipoRepository) {
    suspend operator fun invoke(idEquipo: Long, idUsuario: Long): Boolean {
        return equipoRepository.addUsuarioAlEquipo(idEquipo, idUsuario)
    }
}
