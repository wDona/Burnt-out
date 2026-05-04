package dev.wdona.burntout.db.tables
import org.jetbrains.exposed.sql.Table
object OrganizacionesTable : Table() {
    val id = long("id_organizacion").autoIncrement()
    val nombre = varchar("nombre", 255)
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object EquiposTable : Table() {
    val id = long("id_equipo").autoIncrement()
    val titulo = varchar("titulo", 255)
    val puntuacion = long("puntuacion").nullable()
    val idOrganizacion = long("id_organizacion").references(OrganizacionesTable.id)
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object EquipoMiembrosTable : Table() {
    val idEquipo = long("id_equipo")
    val idMiembro = long("id_miembro")
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(idEquipo, idMiembro)
}

object UsuariosTable : Table() {
    val id = long("id_usuario").autoIncrement()
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
    val nombre = varchar("nombre", 255)
    val riesgoBurnout = double("riesgo_burnout").nullable()
    val descripcion = text("descripcion").nullable()
    val idOrganizacion = long("id_organizacion")
    val idEquipo = long("id_equipo")
    val rol = varchar("rol", 20).default("MEMBER")
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object AjustesTable : Table() {
    val id = long("id_ajuste").autoIncrement()
    val idUsuario = long("id_usuario").references(UsuariosTable.id)
    val nombre = varchar("nombre", 255)
    val valorAjuste = varchar("valor_ajuste", 255)
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object TablerosTable : Table() {
    val id = varchar("id_tablero", 50)
    val titulo = varchar("titulo", 255)
    val idOrganizacion = long("id_organizacion")
    val idEquipo = long("id_equipo").nullable()
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object TareasTable : Table() {
    val id = varchar("id_tarea", 50)
    val titulo = varchar("titulo", 255)
    val descripcion = text("descripcion").nullable()
    val estado = varchar("estado", 50)
    val idTablero = varchar("id_tablero_perteneciente", 50)
    val idUsuarioAsignado = long("id_usuario_asignado")
    val fechaVencimiento = long("fecha_vencimiento").nullable()
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object SubtareasTable : Table() {
    val id = varchar("id_subtarea", 50)
    val titulo = varchar("titulo", 255)
    val descripcion = text("descripcion").nullable()
    val completado = bool("completado")
    val idTarea = varchar("id_tarea_perteneciente", 50)
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object InvitacionesTable : Table() {
    val code = varchar("code", 50)
    val idOrganizacion = long("id_organizacion").references(OrganizacionesTable.id)
    val rol = varchar("rol", 20)
    val creadoPor = long("creado_por")
    val creadoEn = long("creado_en")
    val expiraEn = long("expira_en").nullable()
    val usadoEn = long("usado_en").nullable()
    val usadoPor = long("usado_por").nullable()
    override val primaryKey = PrimaryKey(code)
}

object PreguntasTable : Table() {
    val id = long("id_pregunta").autoIncrement()
    val pregunta = text("pregunta")
    val idOrganizacion = long("id_organizacion")
    val categoria = varchar("categoria", 50)
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object RespuestasTable : Table() {
    val id = varchar("id_respuesta", 50)
    val idUsuario = long("id_usuario")
    val idPregunta = long("id_pregunta")
    val anonimo = bool("anonimo")
    val respuesta = long("respuesta")
    val nombreUsuario = varchar("nombre_usuario", 255).nullable()
    val fecha = long("fecha").nullable()
    val isDeleted = bool("is_deleted").default(false)
    val updatedAt = long("updated_at").default(0L)
    override val primaryKey = PrimaryKey(id)
}

object SyncLogTable : Table() {
    val id = long("id").autoIncrement()
    val idUsuario = long("id_usuario")
    val idOrganizacion = long("id_organizacion")
    val tabla = varchar("tabla", 50)
    val idEntidad = varchar("id_entidad", 50)
    val operacion = varchar("operacion", 10) // INSERT, UPDATE, DELETE
    val timestamp = long("timestamp")
    override val primaryKey = PrimaryKey(id)
}
