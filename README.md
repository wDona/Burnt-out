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

### Registro Diario (Micro-Check)
Para optimizar la experiencia de usuario y evitar la fatiga de encuesta, se recomienda un mapeo diario de 3 preguntas (escala Likert):

1. **CE (Energía):** Grado de agotamiento al finalizar la jornada laboral.
2. **D (Actitud):** Nivel de distanciamiento o irritabilidad percibida hacia el entorno.
3. **RP (Logro):** Percepción del valor real aportado por las tareas completadas.

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
*Desarrollado por wDona. Diseño basado en Material Theme Builder.*
