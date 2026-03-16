# Burn't out

**Burn't out** es una plataforma de gestión de tareas y proyectos diseñada para equilibrar la productividad con el bienestar mental. 
Inspirada en herramientas como Trello, su objetivo principal es prevenir el síndrome de Burnout en entornos corporativos mediante el monitoreo saludable del estado de ánimo y la carga de trabajo.

Es mi proyecto intermodular para el Grado Superior de Desarrollo de Aplicaciones Multiplataforma.

[![Ver Anteproyecto](https://img.shields.io/badge/Ver%20Anteproyecto-📄-red?style=for-the-badge)](./Burn't_Out_Anteproyecto.pdf) [![Descargar](https://img.shields.io/badge/Descargar-📄-green?style=for-the-badge)](https://github.com/wDona/Burnt-out/releases)

---
## 🧠 Sobre Burn out
Si no lo has escuchado nunca, puede que te preguntes que es este sindrome. He hecho una investigacion que responde a esa pregunta y a varias mas con datos sobre como afecta a España en varios sectores. 

Y si lo has escuchado alguna vez, echale un vistazo, ya que puede que hayas sido victima de este sindrome alguna vez (o lo estes siendo) y no lo sepas.

[![Ver Informe](https://img.shields.io/badge/Ver%20Informe-📄-blue?style=for-the-badge)](https://docs.google.com/document/d/18-xCaPPE7kGjMz0NHPk7Fg39yHhOqnHPIx9ZnB0ufzU/edit?usp=sharing) 

*Esta aplicacion no esta hecha ni consultada con profesionales del ambito de la psicologia. Antes de tomar alguna decision basada en alguna informacion aportada, consulta con un profesional. La informacion esta sacada de internet y papers publicos que todo el mundo puede consultar.*

---

## 📊 Evaluar Riesgos
El riesgo no se calcula como un promedio general, sino mediante el seguimiento de tres variables independientes. 

[![Items MBI](https://img.shields.io/badge/Items%20MBI-📄-purple?style=for-the-badge)](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQUYVL7L37odkWwAdk7RaaJhwEGkSPPc59He11C60StwmHmWeRxuhFaa8pO1CBY8kWfGAfdaqx7prtYglhhskNa-gf0TqJo-gBKCcRLMj3t7gMFuvyyvctGuupEWA2Nv6SrxyWTXj3ttMd/s1600/eMBI_HSS.jpg) 

| Dimensión | Ítems MBI (Ref) | Riesgo Bajo | Riesgo Medio | Riesgo Alto |
| :--- | :--- | :--- | :--- | :--- |
| **Cansancio Emocional (CE)** | 1, 2, 3, 6, 8, 13, 14, 16, 20 | < 18 | 19 - 26 | **≥ 27** |
| **Despersonalización (D)** | 5, 10, 11, 15, 22 | < 5 | 6 - 9 | **≥ 10** |
| **Realización Personal (RP)** | 4, 7, 9, 12, 17, 18, 19, 21 | > 40 | 34 - 39 | **≤ 33** |

---

## 📅 Registro Diario (Micro-Check)
Para evitar la fatiga de encuesta en el usuario, se recomienda un mapeo diario de 3 preguntas (escala Likert 0-6 o 1-5):

1. **CE (Energía):** "¿Qué tan agotado te sientes hoy al terminar tu jornada?"
2. **D (Actitud):** "¿Has sentido irritación o distancia hacia tus compañeros/clientes hoy?"
3. **RP (Logro):** "¿Sientes que tus tareas de hoy han aportado valor real?"

---

# 🌟 Características Principales sobre la App

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

## 🚀 Guía de Build y Ejecución en Desarrollo

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
