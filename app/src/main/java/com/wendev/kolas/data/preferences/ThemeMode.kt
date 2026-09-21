package com.wendev.kolas.data.preferences

/** User-selected appearance mode, persisted across launches. */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        /** Parses a persisted value, falling back to [SYSTEM] for anything unknown. */
        fun fromStorage(value: String?): ThemeMode =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
    }
}
