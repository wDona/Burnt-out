package dev.wdona.burntout.data.datasource.local

import dev.wdona.burntout.data.datasource.common.OrganizacionDataSource
import dev.wdona.burntout.shared.domain.Organizacion

interface OrganizacionLocalDataSource : OrganizacionDataSource {
    suspend fun insertOrUpdateOrganizacion(organizacion: Organizacion): Boolean
    suspend fun insertPreguntasMBI(idOrg: Long)
}