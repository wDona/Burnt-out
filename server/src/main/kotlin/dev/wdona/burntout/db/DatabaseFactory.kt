package dev.wdona.burntout.db
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.wdona.burntout.db.tables.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object DatabaseFactory {
    fun init() {
        Database.connect("jdbc:sqlite:server.db", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(
                OrganizacionesTable,
                EquiposTable,
                EquipoMiembrosTable,
                UsuariosTable,
                AjustesTable,
                TablerosTable,
                TareasTable,
                SubtareasTable,
                PreguntasTable,
                RespuestasTable
            )

            if (OrganizacionesTable.selectAll().empty()) {
                OrganizacionesTable.insert {
                    it[id] = 1L
                    it[nombre] = "Organización por defecto"
                }
            }

            if (PreguntasTable.selectAll().empty()) {
                val preguntasBase = listOf(
                    Triple("Debido a mi trabajo me siento emocionalmente agotado", "CE", 1L),
                    Triple("Al final de la jornada me siento agotado.", "CE", 2L),
                    Triple("Me encuentro cansado cuando me levanto por las mañanas y tengo que enfrentarme a otro día de trabajo.", "CE", 3L),
                    Triple("Puedo comprender fácilmente cómo se sienten las personas que tengo que atender.", "RP", 4L),
                    Triple("Creo que trato a algunas personas con indiferencia, como si fueran objetos impersonales.", "D", 5L),
                    Triple("Trabajar con personas todos los días es estresante/tenso para mí.", "CE", 6L),
                    Triple("Me enfrento bien a los problemas que me presentan las personas que tengo que atender.", "RP", 7L),
                    Triple("Siento que mi trabajo me está desgastando.", "CE", 8L),
                    Triple("Siento que mediante mi trabajo estoy influyendo positivamente en la vida de otros.", "RP", 9L),
                    Triple("Creo que me comporto de manera más insensible con la gente desde que hago este trabajo.", "D", 10L),
                    Triple("Me preocupa que este trabajo me esté endureciendo emocionalmente.", "D", 11L),
                    Triple("Me encuentro con mucha vitalidad/energético.", "RP", 12L),
                    Triple("Me siento frustrado por mi trabajo.", "CE", 13L),
                    Triple("Siento que estoy haciendo un trabajo demasiado duro/trabajando demasiado.", "CE", 14L),
                    Triple("Realmente no me importa lo que les ocurre a algunas personas a las que doy servicio.", "D", 15L),
                    Triple("Trabajar en contacto directo con personas me produce estrés.", "CE", 16L),
                    Triple("Tengo facilidad para crear un clima agradable en mi trabajo.", "RP", 17L),
                    Triple("Me siento estimulado después de trabajar junto con personas.", "RP", 18L),
                    Triple("He realizado muchas cosas valiosas en este trabajo.", "RP", 19L),
                    Triple("Siento que he llegado al límite de mis posibilidades.", "CE", 20L),
                    Triple("Siento que sé tratar con calma los conflictos emocionales en el trabajo.", "RP", 21L),
                    Triple("Siento que las personas que atiendo me culpan de sus problemas.", "D", 22L)
                )

                preguntasBase.forEach { (preguntaText, cat, idPregunta) ->
                    PreguntasTable.insert {
                        it[id] = idPregunta
                        it[pregunta] = preguntaText
                        it[idOrganizacion] = 1L
                        it[categoria] = cat
                    }
                }
            }
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
