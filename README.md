# Burn't Out: Gestión de Proyectos y Prevención de Agotamiento

**Burn't Out** es una plataforma de gestión de tareas y proyectos diseñada para equilibrar la productividad con el bienestar mental. Inspirada en herramientas como Trello, su objetivo principal es prevenir el síndrome de Burnout en entornos corporativos mediante el monitoreo saludable del estado de ánimo y la carga de trabajo.

Este proyecto constituye mi proyecto intermodular para el Grado Superior de Desarrollo de Aplicaciones Multiplataforma (DAM).

[![Ver Anteproyecto](https://img.shields.io/badge/Ver%20Anteproyecto-📄-red?style=for-the-badge)](./Burn't_Out_Anteproyecto.pdf) [![Descargar](https://img.shields.io/badge/Descargar-📄-green?style=for-the-badge)](https://github.com/wDona/Burnt-out/releases)

---

## 🧠 Sobre el Síndrome de Burnout

El burnout es un fenómeno derivado del estrés crónico en el entorno laboral. He realizado una investigación detallada que analiza cómo afecta a diversos sectores en España, permitiendo identificar síntomas que a menudo pasan desapercibidos.

[![Ver Informe](https://img.shields.io/badge/Ver%20Informe-📄-blue?style=for-the-badge)](https://docs.google.com/document/d/18-xCaPPE7kGjMz0NHPk7Fg39yHhOqnHPIx9ZnB0ufzU/edit?usp=sharing) 

> **Nota de exención de responsabilidad:** Esta aplicación no ha sido desarrollada ni consultada con profesionales del ámbito de la psicología. Antes de tomar cualquier decisión basada en la información aportada, consulte con un profesional cualificado. La información ha sido obtenida de fuentes públicas y literatura técnica de libre acceso.

---

## 📊 Evaluación de Riesgos (Modelo MBI)

El riesgo no se calcula como un promedio general, sino mediante el seguimiento de tres variables independientes según el Inventario de Burnout de Maslach (MBI).

[![Items MBI](https://img.shields.io/badge/Items%20MBI-📄-purple?style=for-the-badge)](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQUYVL7L37odkWwAdk7RaaJhwEGkSPPc59He11C60StwmHmWeRxuhFaa8pO1CBY8kWfGAfdaqx7prtYglhhskNa-gf0TqJo-gBKCcRLMj3t7gMFuvyyvctGuupEWA2Nv6SrxyWTXj3ttMd/s1600/eMBI_HSS.jpg) 

| Dimensión | Ítems MBI (Ref) | Riesgo Bajo | Riesgo Medio | Riesgo Alto |
| :--- | :--- | :--- | :--- | :--- |
| **Cansancio Emocional (CE)** | 1, 2, 3, 6, 8, 13, 14, 16, 20 | < 18 | 19 - 26 | **≥ 27** |
| **Despersonalización (D)** | 5, 10, 11, 15, 22 | < 5 | 6 - 9 | **≥ 10** |
| **Realización Personal (RP)** | 4, 7, 9, 12, 17, 18, 19, 21 | > 40 | 34 - 39 | **≤ 33** |

### Las 22 preguntas del MBI

Las preguntas están almacenadas en base de datos y son comunes a todos los usuarios. Las respuestas se recogen en una escala de frecuencia de 0 a 6:

| Valor | Frecuencia |
|:---:|:---|
| 0 | Nunca / Ninguna vez |
| 1 | Casi nunca / Pocas veces al año |
| 2 | Algunas veces / Una vez al mes o menos |
| 3 | Regularmente / Pocas veces al mes |
| 4 | Bastantes veces / Una vez por semana |
| 5 | Casi siempre / Pocas veces por semana |
| 6 | Siempre / Todos los días |

1. Debido a mi trabajo me siento emocionalmente agotado.
2. Al final de la jornada me siento agotado.
3. Me encuentro cansado cuando me levanto por las mañanas y tengo que enfrentarme a otro día de trabajo.
4. Puedo comprender facilmente como se sienten las personas que tengo que atender.
5. Creo que trato a algunas personas con indiferencia, como si fueran objetos impersonales.
6. Trabajar con personas todos los días es estresante/tenso para mí.
7. Me enfrento muy bien a los problemas que me presentan las personas que tengo que atender.
8. Me siento siento que mi trabajo me esta desgastando.
9. Siento que mediante mi trabajo estoy influyendo positivamente en la vida de otros.
10. Creo que me comporto de manera más insensible con la gente desde que hago este trabajo.
11. Me preocupa que este trabajo me esté endureciendo emocionalmente.
12. Me encuentro con mucha vitalidad/energetico.
13. Me siento frustrado por mi trabajo.
14. Siento que estoy haciendo un trabajo demasiado duro/trabajando demasiado.
15. Realmente no me importa lo que les ocurre a algunas personas a las que doy servicio.
16. Trabajar en contacto directo con personas me produce estrés.
17. Tengo facilidad para crear un clima agradable en mi trabajo.
18. Me siento estimulado después de trabajar en contacto con personas.
19. He realizado muchas cosas valiosas en este trabajo.
20. Me siento que he llegado al límite de mis posibilidades.
21. Siento que se tratar con calma los conflictos emocionales en el trabajo.
22. Siento que las personas que atiendo me culpan de sus problemas.

### Algoritmo de cálculo

El resultado se expone como un `Double` entre `0.0` y `1.0` almacenado en `RiesgoBurnout`, junto con el nivel individual de cada subescala. El cálculo se implementa en un `CalcularRiesgoBurnout` dentro de la capa `domain`, siguiendo la arquitectura Clean Architecture + MVVM.

```kotlin
data class ResultadoBurnout(
    val nivelCE: Double,        // 0-0.33 = bajo, 0.34-0.66 = medio, 0.67-1.0 = alto
    val nivelD: Double,         // 0-0.33 = bajo, 0.34-0.66 = medio, 0.67-1.0 = alto
    val nivelRP: Double,        // 0-0.33 = bajo, 0.34-0.66 = medio, 0.67-1.0 = alto
    val riesgoGlobal: Float     // (nivelCE + nivelD + nivelRP) / 3
)
```

> ⚠️ **La subescala RP es inversa**: una puntuación baja indica riesgo alto.

### Flujo de encuesta

- **Onboarding:** el usuario responde las 22 preguntas completas la primera vez, estableciendo su riesgo base. La encuesta se presenta una pregunta por pantalla para optimizar la experiencia en dispositivos móviles.
- **Encuesta periódica:** se repite la encuesta completa de forma semanal o quincenal, manteniendo la integridad de la informacion.
- **Frescura del dato:** si el usuario lleva más de un número determinado de días sin responder, el riesgo se muestra como desactualizado en la UI. No se obliga al usuario a responder, pero se le indica que el dato puede no ser preciso. Cada respuesta almacena un timestamp Unix para permitir este seguimiento.

### Registro Diario (Micro-Check)

Para optimizar la experiencia de usuario y evitar la fatiga de encuesta, se hace un mapeo diario de 3 preguntas (escala Likert):

1. **CE (Energía):** Grado de agotamiento al finalizar la jornada laboral.
2. **D (Actitud):** Nivel de distanciamiento o irritabilidad percibida hacia el entorno.
3. **RP (Logro):** Percepción del valor real aportado por las tareas completadas.

### Integración con la gestión de tareas

El nivel de burnout de cada usuario influye directamente en la asignación de tareas dentro del tablero:

| Nivel de riesgo | Comportamiento al asignar tarea |
|:---|:---|
| **Bajo** | Sin restricciones |
| **Medio** | Aviso informativo al manager |
| **Alto** | Confirmación obligatoria alarmante antes de asignar |

Adicionalmente, las tarjetas deben mostrar un indicador visual junto al avatar del usuario asignado según su nivel de riesgo, y al asignar tareas los usuarios se ordenan priorizando los de menor riesgo.

### Limitaciones conocidas y trabajo futuro

- El MBI estándar incluye preguntas orientadas a perfiles que trabajan directamente con personas. Usuarios con perfiles distintos pueden encontrar algunas preguntas poco aplicables. Esta limitación se documenta y se contempla como mejora futura.

---

## 🛠️ Especificaciones Técnicas

El proyecto emplea **Kotlin Multiplatform (KMP)** para compartir la lógica de negocio y **Compose** para la interfaz de usuario.

### Stack Tecnológico
* **Arquitectura:** Kotlin Multiplatform (Android y Desktop JVM).
* **Interfaz de Usuario:** Compose Multiplatform.
* **Backend:** API REST construida con Ktor.
* **Persistencia de Datos:**
  * Local: SQLite con SQLDelight.
  * Nube: MariaDB / PostgreSQL.
* **Seguridad:** Cifrado TLS, hashing de credenciales y autenticación JWT.

### Características Principales
* **Gestión Visual:** Organización mediante tableros, tarjetas, tareas y subtareas.
* **Monitoreo de Bienestar:** Encuestas anónimas y rápidas sobre el estado de ánimo.
* **Prevención Activa:** Evaluación de riesgo con capacidad de limitar la carga de trabajo si el agotamiento es elevado.
* **Resiliencia Operativa:** Soporte para trabajo offline con sincronización automática de cambios.

---

## 🚀 Guía de Ejecución en Desarrollo

### Requisitos Previos
* Java Development Kit (JDK) 11 o superior.
* Android Studio o IntelliJ IDEA (con plugin KMP configurado).

| Entorno | Comando (macOS/Linux) | Comando (Windows) |
| :--- | :--- | :--- |
| **Desktop** | `./gradlew :composeApp:run` | `.\gradlew.bat :composeApp:run` |
| **Android** | `./gradlew :composeApp:installDebug` | `.\gradlew.bat :composeApp:installDebug` |
| **Servidor** | `./gradlew :server:run` | `.\gradlew.bat :server:run` |

---

## 🛡️ Privacidad y Seguridad
* **Anonimato:** Opción de encuestas de bienestar configurables para proteger la identidad del usuario.
* **Integridad de Datos:** Los registros de sistema no contienen información personal sensible.
* **Consentimiento:** Tratamiento de datos sujeto a la aceptación explícita de términos.

---
*Desarrollado por wDona. Diseño usando Material Theme Builder.*
