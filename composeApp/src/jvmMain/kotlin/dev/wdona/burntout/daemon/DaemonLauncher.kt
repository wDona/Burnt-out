package dev.wdona.burntout.daemon

import java.io.File

object DaemonLauncher {
    private val pidFile
        get() = File(System.getProperty("user.home"), ".burntout_app/daemon.pid")

    fun launchIfNeeded() {
        if (isDaemonRunning()) {
            println("[BurntOut] Daemon ya en ejecución (PID: ${pidFile.readText().trim()})")
            return
        }

        try {
            val javaCmd = ProcessHandle.current().info().command().orElse("java")
            val classpath = System.getProperty("java.class.path")
            val process = ProcessBuilder(javaCmd, "-cp", classpath, "dev.wdona.burntout.MainKt", "--daemon")
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()

            pidFile.parentFile?.mkdirs()

            pidFile.writeText(process.pid().toString())

            println("[BurntOut] Daemon lanzado con PID ${process.pid()}")
        } catch (e: Exception) {
            println("[BurntOut] No se pudo lanzar daemon: ${e.message}")
        }
    }

    private fun isDaemonRunning(): Boolean {
        if (!pidFile.exists()) return false
        val pid = pidFile.readText().trim().toLongOrNull() ?: return false
        return ProcessHandle.of(pid).map { it.isAlive }.orElse(false)
    }
}
