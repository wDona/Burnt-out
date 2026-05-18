# Burn't Out

Aplicación de gestión de entregas estilo Trello con monitoreo de burnout y gamificacion integrados. Mi proyecto intermodular para el Grado Superior de DAM.

[![Ver Anteproyecto](https://img.shields.io/badge/Ver%20Anteproyecto-📄-red?style=for-the-badge)](./Burn't_Out_Anteproyecto.pdf)

---

## Descarga

### Windows, Linux (Debian), Android:
[![Descargar](https://img.shields.io/badge/Descargar-📄-green?style=for-the-badge)](https://github.com/wDona/Burnt-out/releases)

### AUR (Arch)
```zsh
yay -S burnt-out
```

---

## ¿Qué es el burnout? 
El burnout es un estado de agotamiento crónico causado **principalmente** por el estrés sostenido en el trabajo. Afecta a nivel emocional, actitudinal y a la percepción de logro personal. En muchos casos sus síntomas pasan desapercibidos hasta que están muy avanzados.

[![Aprender mas](https://img.shields.io/badge/Aprender%20mas-📄-blue?style=for-the-badge)](https://wdona.dev/blogs/sindrome-burn-out) 

> **Nota:** Esta aplicación no ha sido desarrollada ni consultada con profesionales de la psicología. Consulte con un profesional antes de tomar decisiones basadas en la información aportada.

---

## Evaluación de riesgo (MBI)

El riesgo se calcula a partir del Inventario de Burnout de Maslach (MBI), que mide tres subescalas independientes:

| Dimensión | Ítems MBI (Ref) | Riesgo Bajo | Riesgo Medio | Riesgo Alto |
| :--- | :--- | :--- | :--- | :--- |
| **Cansancio Emocional (CE)** | 1, 2, 3, 6, 8, 13, 14, 16, 20 | < 18 | 19 - 26 | **≥ 27** |
| **Despersonalización (D)** | 5, 10, 11, 15, 22 | < 5 | 6 - 9 | **≥ 10** |
| **Realización Personal (RP)** | 4, 7, 9, 12, 17, 18, 19, 21 | > 40 | 34 - 39 | **≤ 33** |

[![Items MBI](https://img.shields.io/badge/Items%20MBI-📄-purple?style=for-the-badge)](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiQUYVL7L37odkWwAdk7RaaJhwEGkSPPc59He11C60StwmHmWeRxuhFaa8pO1CBY8kWfGAfdaqx7prtYglhhskNa-gf0TqJo-gBKCcRLMj3t7gMFuvyyvctGuupEWA2Nv6SrxyWTXj3ttMd/s1600/eMBI_HSS.jpg)

Las respuestas se recogen en escala de 0 a 6 (de "Nunca" a "Siempre"). La encuesta completa de 22 preguntas se realiza en el onboarding y se repite periódicamente. Para el seguimiento diario se usa un micro-check de 3 preguntas para evitar fatiga de encuesta.

El resultado se almacena como un `Double` entre `0.0` y `1.0`, como riesgo global haciendo la media de las tres. En la UI se muestra multiplicado por 100 (porcentaje). *La subescala RP es inversa: puntuación baja en las preguntas = riesgo alto.*

---

## Integración con tareas

El nivel de riesgo de cada usuario afecta directamente a la asignación de tareas.

### Niveles de riesgo

El riesgo se comunica visualmente mediante un icono de batería con color asociado:

| Rango (`riesgoBurnout`) | Nivel | Color |
|:---|:---|:---|
| `< 0` | Datos insuficientes | Gris |
| `≤ 0.10` | Sin riesgo | Verde |
| `≤ 0.25` | Riesgo bajo | Verde claro |
| `≤ 0.40` | Riesgo moderado | Amarillo |
| `≤ 0.55` | Riesgo alto | Naranja |
| `≤ 0.65` | Riesgo muy alto | Naranja oscuro |
| `≤ 0.80` | Burnout leve | Rojo oscuro |
| `≤ 0.90` | Burnout moderado | Rojo |
| `> 0.90` | Burnout grave | Rojo error |

### Comportamiento al asignar tareas

| Umbral | Comportamiento |
|:---|:---|
| `riesgo > 0.40` | Aviso inline bajo el selector: *"⚠ Cuidado, este usuario tiene [nivel]"* |
| `riesgo > 0.80` | Diálogo de confirmación obligatorio: *"Soy consciente de que le asigno una tarea a un trabajador con burnout"* |

El icono de batería aparece en el selector de usuario al crear/editar tareas y en las tarjetas del tablero.

---

## Stack técnico

- **Arquitectura:** Kotlin Multiplatform (Android + Desktop JVM), Clean Architecture + MVVM
- **UI:** Compose Multiplatform
- **Backend:** API REST con Ktor
- **Persistencia local:** SQLite + SQLDelight
- **Persistencia en nube:** Exposed ORM
- Soporte para trabajo offline con sincronización automática

### Ejecutar en desarrollo

Requisitos: JDK 11+, Android Studio o IntelliJ IDEA con plugin KMP.

| Entorno | Comando (Linux) | Comando (Windows) |
| :--- | :--- | :--- |
| **Desktop** | `./gradlew :composeApp:run` | `.\gradlew.bat :composeApp:run` |
| **Android** | `./gradlew :composeApp:installDebug` | `.\gradlew.bat :composeApp:installDebug` |
| **Servidor** | `./gradlew :server:run` | `.\gradlew.bat :server:run` |

---

*Desarrollado por wDona. Colores de Material Theme Builder*
