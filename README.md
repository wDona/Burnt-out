# Burn't Out: Gestión de Proyectos y Prevención de Agotamiento

**Burn't Out** es una plataforma de gestión de tareas y proyectos diseñada para equilibrar la productividad con el bienestar mental. Inspirada en metodologías visuales como Kanban, su objetivo principal es prevenir el síndrome de Burnout en entornos corporativos mediante el monitoreo de la carga de trabajo y el estado anímico del usuario.

Este proyecto constituye el trabajo intermodular para el Grado Superior de Desarrollo de Aplicaciones Multiplataforma (DAM).

---

### Documentación del Proyecto

| Recurso | Enlace |
| :--- | :--- |
| **Anteproyecto Técnico** | [Ver Anteproyecto (PDF)](./Burn't_Out_Anteproyecto.pdf) |
| **Repositorio de Versiones** | [Descargar Releases](https://github.com/wDona/Burnt-out/releases) |

---

## 🧠 Marco Teórico: El Síndrome de Burnout

El síndrome de burnout es un fenómeno derivado del estrés crónico en el lugar de trabajo que no ha sido gestionado con éxito. He realizado una investigación detallada sobre su incidencia en diversos sectores profesionales en España, disponible en el siguiente enlace:

[**Acceder al Informe de Investigación**](https://docs.google.com/document/d/18-xCaPPE7kGjMz0NHPk7Fg39yHhOqnHPIx9ZnB0ufzU/edit?usp=sharing)

> **Nota de exención de responsabilidad:** Esta aplicación no ha sido desarrollada ni supervisada por profesionales de la psicología. La información contenida se basa en estudios públicos y literatura técnica disponible en internet. Ante cualquier síntoma, se recomienda encarecidamente consultar con un profesional colegiado.

---

## 📊 Evaluación de Riesgos (Modelo MBI)

El riesgo no se calcula como un promedio general, sino mediante el seguimiento de tres variables independientes según el Inventario de Burnout de Maslach (MBI).

[**Referencia de Items MBI**](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQUYVL7L37odkWwAdk7RaaJhwEGkSPPc59He11C60StwmHmWeRxuhFaa8pO1CBY8kWfGAfdaqx7prtYglhhskNa-gf0TqJo-gBKCcRLMj3t7gMFuvyyvctGuupEWA2Nv6SrxyWTXj3ttMd/s1600/eMBI_HSS.jpg)

| Dimensión | Ítems MBI (Ref) | Riesgo Bajo | Riesgo Medio | Riesgo Alto |
| :--- | :--- | :--- | :--- | :--- |
| **Cansancio Emocional (CE)** | 1, 2, 3, 6, 8, 13, 14, 16, 20 | < 18 | 19 - 26 | **≥ 27** |
| **Despersonalización (D)** | 5, 10, 11, 15, 22 | < 5 | 6 - 9 | **≥ 10** |
| **Realización Personal (RP)** | 4, 7, 9, 12, 17, 18, 19, 21 | > 40 | 34 - 39 | **≤ 33** |

### Registro Diario (Micro-Check)
Para optimizar la experiencia de usuario y evitar la fatiga de encuesta, se implementa un mapeo diario basado en tres cuestiones clave (Escala Likert):

1. **CE (Energía):** Grado de agotamiento al finalizar la jornada laboral.
2. **D (Actitud):** Nivel de distanciamiento o irritabilidad hacia el entorno profesional.
3. **RP (Logro):** Percepción del valor aportado mediante las tareas realizadas.

---

## 🛠️ Especificaciones Técnicas

El proyecto emplea **Kotlin Multiplatform (KMP)** para la lógica compartida y **Compose** para la interfaz de usuario.

### Stack Tecnológico
* **Arquitectura:** Kotlin Multiplatform (Android y Desktop JVM).
* **Interfaz:** Compose Multiplatform.
* **Backend:** API REST desarrollada con Ktor.
* **Persistencia de Datos:**
    * Local: SQLite con SQLDelight.
    * Remota: MariaDB / PostgreSQL.
* **Seguridad:** Cifrado TLS, hashing de credenciales y autenticación basada en JWT.

### Funcionalidades Destacadas
* **Gestión Visual:** Tableros, tarjetas y desglose de subtareas.
* **Prevención Activa:** Evaluación automática del riesgo con limitación opcional de asignación de tareas.
* **Resiliencia:** Soporte para trabajo offline con sincronización posterior.
* **Dinámicas de Equipo:** Sistemas de gamificación y fomento de pausas activas.

---

## 🚀 Instrucciones de Ejecución

### Requisitos Previos
* Java Development Kit (JDK) 11 o superior.
* Entorno de desarrollo compatible (Android Studio o IntelliJ IDEA con plugin KMP).

| Entorno | Comando (macOS/Linux) | Comando (Windows) |
| :--- | :--- | :--- |
| **Desktop** | `./gradlew :composeApp:run` | `.\gradlew.bat :composeApp:run` |
| **Android** | `./gradlew :composeApp:installDebug` | `.\gradlew.bat :composeApp:installDebug` |
| **Servidor** | `./gradlew :server:run` | `.\gradlew.bat :server:run` |

---

## 🛡️ Privacidad y Seguridad
* **Anonimato:** Las encuestas de bienestar permiten configuración de anonimato total.
* **Integridad:** Los registros de sistema (logs) excluyen información personal sensible.
* **Consentimiento:** Tratamiento de datos sujeto a la aceptación explícita de términos.

---
*Desarrollado por wDona. Identidad visual generada con Material Theme Builder.*
