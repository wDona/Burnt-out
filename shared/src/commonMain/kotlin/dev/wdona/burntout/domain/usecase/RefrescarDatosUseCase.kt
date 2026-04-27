package dev.wdona.burntout.domain.usecase

import dev.wdona.burntout.domain.repository.AjusteRepository
import dev.wdona.burntout.domain.repository.EquipoRepository
import dev.wdona.burntout.domain.repository.TableroRepository
import dev.wdona.burntout.shared.utils.SettingsManager

class RefrescarDatosUseCase(
    private val tableroRepository: TableroRepository,
    private val equipoRepository: EquipoRepository,
    private val ajusteRepository: AjusteRepository
) {
    suspend operator fun invoke() {
        val idOrg = SettingsManager.getIdOrganizacionActual()
        val idUsuario = SettingsManager.getIdUsuarioActual()
        val idEquipo = SettingsManager.getIdEquipoActual()

        if (idOrg == Long.MIN_VALUE && idUsuario == Long.MIN_VALUE) return

        try { tableroRepository.getTablerosByEquipo(idOrg, idEquipo) } catch (e: Exception) {
            println("Error al refrescar tableros: ${e.message}")
        }
        try { equipoRepository.getEquiposByOrg(idOrg) } catch (e: Exception) {
            println("Error al refrescar equipos: ${e.message}")
        }
        try { ajusteRepository.getAjustesByUsuario(idUsuario) } catch (e: Exception) {
            println("Error al refrescar ajustes: ${e.message}")
        }
    }
}