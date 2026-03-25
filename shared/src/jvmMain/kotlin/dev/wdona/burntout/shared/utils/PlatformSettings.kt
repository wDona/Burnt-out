package dev.wdona.burntout.shared.utils

import com.russhwolf.settings.Settings
import com.russhwolf.settings.PropertiesSettings
import java.util.Properties
import java.io.File

actual fun createSettings(): Settings {
    val appDataDir = File(System.getProperty("user.dir"), "burntout_data")
    if (!appDataDir.exists()) {
        appDataDir.mkdirs()
    }
    val settingsFile = File(appDataDir, "settings.properties")
    return FileSettings(settingsFile)
}

class FileSettings(private val file: File) : Settings {
    private val props = Properties()
    private val delegate: PropertiesSettings

    init {
        if (file.exists()) {
            file.inputStream().use { props.load(it) }
        }
        delegate = PropertiesSettings(props)
    }

    private fun save() {
        try {
            file.outputStream().use { props.store(it, null) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun clear() {
        delegate.clear()
        save()
    }

    override fun remove(key: String) {
        delegate.remove(key)
        save()
    }

    override fun hasKey(key: String): Boolean = delegate.hasKey(key)

    override fun putInt(key: String, value: Int) {
        delegate.putInt(key, value)
        save()
    }

    override fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)

    override fun getIntOrNull(key: String): Int? = delegate.getIntOrNull(key)

    override fun putLong(key: String, value: Long) {
        delegate.putLong(key, value)
        save()
    }

    override fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(key, defaultValue)

    override fun getLongOrNull(key: String): Long? = delegate.getLongOrNull(key)

    override fun putString(key: String, value: String) {
        delegate.putString(key, value)
        save()
    }

    override fun getString(key: String, defaultValue: String): String = delegate.getString(key, defaultValue)

    override fun getStringOrNull(key: String): String? = delegate.getStringOrNull(key)

    override fun putFloat(key: String, value: Float) {
        delegate.putFloat(key, value)
        save()
    }

    override fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat(key, defaultValue)

    override fun getFloatOrNull(key: String): Float? = delegate.getFloatOrNull(key)

    override fun putDouble(key: String, value: Double) {
        delegate.putDouble(key, value)
        save()
    }

    override fun getDouble(key: String, defaultValue: Double): Double = delegate.getDouble(key, defaultValue)

    override fun getDoubleOrNull(key: String): Double? = delegate.getDoubleOrNull(key)

    override fun putBoolean(key: String, value: Boolean) {
        delegate.putBoolean(key, value)
        save()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = delegate.getBoolean(key, defaultValue)

    override fun getBooleanOrNull(key: String): Boolean? = delegate.getBooleanOrNull(key)

    override val keys: Set<String> get() = delegate.keys
    override val size: Int get() = delegate.size
}

