package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.data.dao.TareaRepository
import dev.wdona.burntout.domain.model.TipoEstadoTarea
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.shared.domain.Tarea

private const val PUNTOS_POR_TAREA = 10L

class CompletarTareaUseCase(
    private val tareaRepository: TareaRepository,
    private val equipoRepository: EquipoRepository
) {
    suspend operator fun invoke(tarea: Tarea, idEquipo: Long) {
        if (tarea.estado == TipoEstadoTarea.COMPLETADA.string) return
        tareaRepository.actualizarTarea(tarea.copy(estado = TipoEstadoTarea.COMPLETADA.string))
        equipoRepository.updatePuntuacion(idEquipo, PUNTOS_POR_TAREA)
    }
}