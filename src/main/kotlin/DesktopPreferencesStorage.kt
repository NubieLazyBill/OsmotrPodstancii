// Добавьте этот файл в ваш проект (например, DesktopPreferencesStorage.kt)
package org.example

import java.io.File
import java.util.Properties

object DesktopPreferencesStorage {
    private val propertiesFile = File("inspector_settings.properties")
    private val properties = Properties()

    init {
        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { properties.load(it) }
        }
    }

    private fun saveProperties() {
        propertiesFile.outputStream().use { properties.store(it, null) }
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return properties.getProperty(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        if (value == null) {
            properties.remove(key)
        } else {
            properties.setProperty(key, value)
        }
        saveProperties()
    }

    fun remove(key: String) {
        properties.remove(key)
        saveProperties()
    }
}