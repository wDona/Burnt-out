# Burn't out

**Burn't out** es una plataforma de gestión de tareas y proyectos diseñada para equilibrar la productividad con el bienestar mental. 
Inspirada en herramientas como Trello, su objetivo principal es prevenir el síndrome de Burnout en entornos corporativos mediante el monitoreo saludable del estado de ánimo y la carga de trabajo.

Es mi proyecto intermodular para el Grado Superior de Desarrollo de Aplicaciones Multiplataforma que estoy haciendo.

[![Ver Anteproyecto](https://img.shields.io/badge/Ver%20Anteproyecto-📄-red?style=for-the-badge)](./Burn't_Out_Anteproyecto.pdf) [![Descargar](https://img.shields.io/badge/Descargar-📄-green?style=for-the-badge)](https://github.com/wDona/Burnt-out/releases)

---

## 🌟 Características Principales

* **Gestión Visual:** Organización mediante tableros, tarjetas, tareas y subtareas
* **Monitoreo de Bienestar:** Encuestas rápidas (menos de 15 segundos) y anónimas sobre estrés y ánimo
* **Prevención Activa:** La app evalúa el riesgo de agotamiento, genera alertas y puede limitar la asignación de tareas si el riesgo es alto.
* **Cooperación y Gamificación:** Sistema de recompensas y tablas de clasificación para fomentar el trabajo en equipo y las pausas necesarias.
* **Modo Offline:** Capacidad de trabajar sin conexión, priorizando la sincronización de cambios al recuperar el acceso a internet.

---

## 🛠️ Stack Tecnológico

El proyecto utiliza **Kotlin Multiplatform (KMP)** para compartir lógica entre plataformas 
y **Compose** para la interfaz de usuario.

* **Cliente:** Android y Desktop.
* **Servidor:** API construida con Ktor.
* **Base de Datos:** 
  * **Local:** SQLite con SQLDelight.
  * **Nube:** MariaDB / PostgreSQL.
* **Seguridad:** Cifrado TLS, hashing de contraseñas y autenticación mediante JWT.

---

## 🚀 Guía de Build y Ejecución

### Requisitos Previos
* JDK 11 o superior.
* Android Studio o IntelliJ IDEA (con el plugin KMP).

### 🖥️ Escritorio (Desktop JVM)
Para compilar y ejecutar la versión de escritorio:
- **macOS/Linux:** `./gradlew :composeApp:run`
- **Windows:** `.\gradlew.bat :composeApp:run`

### 📱 Android
Para instalar la aplicación en un dispositivo o emulador:
- **macOS/Linux:** `./gradlew :composeApp:installDebug`
- **Windows:** `.\gradlew.bat :composeApp:installDebug`

### 🌐 Servidor
Para iniciar la API del servidor:
- **macOS/Linux:** `./gradlew :server:run`
- **Windows:** `.\gradlew.bat :server:run`

---

## 🛡️ Privacidad y Seguridad
* **Anonimato:** Configurable por el usuario para las encuestas de bienestar.
* **Protección de Datos:** Los logs no contienen información personal sensible.
* **Consentimiento:** Se requiere aceptación explícita antes del tratamiento de datos.

---
*Desarrollado por wDona*. *He usado [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)*
