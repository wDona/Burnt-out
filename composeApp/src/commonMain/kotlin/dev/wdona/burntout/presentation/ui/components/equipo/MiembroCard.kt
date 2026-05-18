package dev.wdona.burntout.presentation.ui.components.equipo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import dev.wdona.burntout.presentation.ui.components.common.BateriaBurnout
import dev.wdona.burntout.presentation.ui.theme.BurntOutMaterialTheme
import dev.wdona.burntout.shared.domain.Usuario

@Composable
fun MiembroCard(
    miembro: Usuario,
    onClick: () -> Unit,
    esAdminOrOwner: Boolean = false,
    onCambiarRol: ((String) -> Unit)? = null,
    onEliminar: (() -> Unit)? = null
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val esOwner = miembro.rol == "OWNER"
    val esAdmin = miembro.rol == "ADMIN"
    val puedeEliminar = esAdminOrOwner && !esOwner && onEliminar != null
    val puedeGestionar = (esAdminOrOwner && !esOwner && onCambiarRol != null) || puedeEliminar

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(bottom = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val riesgo = miembro.riesgoBurnout
        if (riesgo != null) {
            BateriaBurnout(riesgo = riesgo, mostrarTexto = false, modifier = Modifier.padding(start = 16.dp, end = 8.dp))
        } else {
            BateriaBurnout(riesgo = -1.0, mostrarTexto = false, modifier = Modifier.padding(start = 16.dp, end = 8.dp))
        }
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Icono de usuario",
            modifier = Modifier.padding(end = 8.dp),
            tint = BurntOutMaterialTheme.getColorScheme().onSurface
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = miembro.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = BurntOutMaterialTheme.getColorScheme().onSurface,
            )
            Text(
                text = "@${miembro.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = BurntOutMaterialTheme.getColorScheme().onSurface,
                modifier = Modifier.padding(start = 8.dp).alpha(0.7f)
            )
            if (esOwner || esAdmin) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (esOwner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = if (esOwner) "Owner" else "Admin",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (esOwner) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        if (puedeGestionar) {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones de rol")
                }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    if (onCambiarRol != null) {
                        if (esAdmin) {
                            DropdownMenuItem(
                                text = { Text("Quitar admin") },
                                onClick = {
                                    menuExpanded = false
                                    onCambiarRol("MEMBER")
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Hacer admin") },
                                onClick = {
                                    menuExpanded = false
                                    onCambiarRol("ADMIN")
                                }
                            )
                        }
                    }
                    if (puedeEliminar) {
                        DropdownMenuItem(
                            text = { Text("Eliminar usuario", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onEliminar?.invoke()
                            }
                        )
                    }
                }
            }
        }
    }
}
