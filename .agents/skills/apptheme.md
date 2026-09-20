# App Theme — Kolas

Material 3 color system derived from the dog's coat palette (black & tan) with warm accent tones.

## Source Palette

Extracted from the reference photo (black & tan coat, warm rust markings, red board, foliage).

| Role | Swatch | Hex | Source |
|------|--------|-----|--------|
| Deep charcoal (coat) | ⬛ | `#1A1A1A` | Body, head, nose |
| Near-black base | ⬛ | `#18120E` | Warm dark surface |
| Warm rust (tan markings) | 🟫 | `#8B5A2B` | Eyebrows, muzzle, paws |
| Light tan | 🟧 | `#E5A87A` | Highlight markings |
| Golden amber | 🟧 | `#E8A33D` | Brightest tan highlight |
| Signal red | 🟥 | `#C1272D` | Background board accent |
| Sage green | 🟩 | `#7FA87F` | Background foliage |
| Warm off-white | ⬜ | `#FFF8F5` | Neutral base |

## Design Intent

- **Primary** — warm rust to represent the tan markings. Used for buttons, active states, brand identity.
- **Secondary** — charcoal for the coat. Used for secondary surfaces, chips, supporting UI.
- **Tertiary** — signal red as a rare accent (badges, alerts, critical CTA).
- **Background** — warm off-white (light) / warm near-black (dark) so the UI feels like the photo's warmth.
- **Emotion mapping** — each of the 5 emotion classes has a dedicated color drawn from the same palette.

## Material 3 Color Scheme

### Light

```kotlin
private val LightColors = lightColorScheme(
    primary              = Color(0xFF8B5A2B),  // warm rust
    onPrimary            = Color(0xFFFFFFFF),
    primaryContainer     = Color(0xFFFFDCC2),
    onPrimaryContainer   = Color(0xFF2E1500),

    secondary            = Color(0xFF3A3330),  // charcoal
    onSecondary          = Color(0xFFFFFFFF),
    secondaryContainer   = Color(0xFFEDE0D9),
    onSecondaryContainer = Color(0xFF1F1A18),

    tertiary             = Color(0xFFC1272D),  // signal red
    onTertiary           = Color(0xFFFFFFFF),
    tertiaryContainer    = Color(0xFFFFDAD6),
    onTertiaryContainer  = Color(0xFF410002),

    error                = Color(0xFFBA1A1A),
    onError              = Color(0xFFFFFFFF),
    errorContainer       = Color(0xFFFFDAD6),
    onErrorContainer     = Color(0xFF410002),

    background           = Color(0xFFFFF8F5),  // warm off-white
    onBackground         = Color(0xFF201A17),
    surface              = Color(0xFFFFF8F5),
    onSurface            = Color(0xFF201A17),
    surfaceVariant       = Color(0xFFF2DFD3),
    onSurfaceVariant     = Color(0xFF52443B),
    outline              = Color(0xFF85736A),
    outlineVariant       = Color(0xFFD5C2B8),
)
```

### Dark

```kotlin
private val DarkColors = darkColorScheme(
    primary              = Color(0xFFE5A87A),  // light tan
    onPrimary            = Color(0xFF4A2800),
    primaryContainer     = Color(0xFF6B3D10),
    onPrimaryContainer   = Color(0xFFFFDCC2),

    secondary            = Color(0xFFD8C2B8),  // warm charcoal tint
    onSecondary          = Color(0xFF352F2C),
    secondaryContainer   = Color(0xFF4B4542),
    onSecondaryContainer = Color(0xFFF5DFD6),

    tertiary             = Color(0xFFFFB4AB),  // softened red
    onTertiary           = Color(0xFF690005),
    tertiaryContainer    = Color(0xFF93000A),
    onTertiaryContainer  = Color(0xFFFFDAD6),

    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFFDAD6),

    background           = Color(0xFF18120E),  // warm near-black (coat)
    onBackground         = Color(0xFFEDE0D9),
    surface              = Color(0xFF201A16),
    onSurface            = Color(0xFFEDE0D9),
    surfaceVariant       = Color(0xFF52443B),
    onSurfaceVariant     = Color(0xFFD5C2B8),
    outline              = Color(0xFF9E8C82),
    outlineVariant       = Color(0xFF52443B),
)
```

## Emotion Accent Colors

Each classifier output maps to a fixed color for cards, badges, and charts.

```kotlin
object EmotionColors {
    val Happy  = Color(0xFFE8A33D)  // golden amber
    val Relax  = Color(0xFF7FA87F)  // sage green
    val Alert  = Color(0xFFE07A2F)  // vivid orange
    val Angry  = Color(0xFFC1272D)  // signal red
    val Frown  = Color(0xFF8B6B9E)  // muted plum

    fun forEmotion(emotion: String): Color = when (emotion.lowercase()) {
        "happy" -> Happy
        "relax" -> Relax
        "alert" -> Alert
        "angry" -> Angry
        "frown" -> Frown
        else    -> Color(0xFF85736A)  // neutral outline
    }
}
```

| Emotion | Color | Hex |
|---------|-------|-----|
| happy | golden amber | `#E8A33D` |
| relax | sage green | `#7FA87F` |
| alert | vivid orange | `#E07A2F` |
| angry | signal red | `#C1272D` |
| frown | muted plum | `#8B6B9E` |

## Theme Setup

### Color.kt

```kotlin
package com.yourname.kolas.ui.theme

import androidx.compose.ui.graphics.Color

// Light
val WarmRust       = Color(0xFF8B5A2B)
val Charcoal       = Color(0xFF3A3330)
val SignalRed      = Color(0xFFC1272D)
val WarmOffWhite   = Color(0xFFFFF8F5)
val WarmTint       = Color(0xFFF2DFD3)

// Dark
val LightTan       = Color(0xFFE5A87A)
val WarmBlack      = Color(0xFF18120E)
val WarmSurface    = Color(0xFF201A16)
val WarmOnSurface  = Color(0xFFEDE0D9)

// Emotion accents
val EmotionHappy   = Color(0xFFE8A33D)
val EmotionRelax   = Color(0xFF7FA87F)
val EmotionAlert   = Color(0xFFE07A2F)
val EmotionAngry   = Color(0xFFC1272D)
val EmotionFrown   = Color(0xFF8B6B9E)
```

### Theme.kt

```kotlin
package com.yourname.kolas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = WarmRust,
    secondary = Charcoal,
    tertiary = SignalRed,
    background = WarmOffWhite,
    surface = WarmOffWhite,
    surfaceVariant = WarmTint,
)

private val DarkColors = darkColorScheme(
    primary = LightTan,
    secondary = WarmSurface,
    tertiary = EmotionAngry,
    background = WarmBlack,
    surface = WarmSurface,
    onSurface = WarmOnSurface,
)

@Composable
fun KolasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep brand colors; disable dynamic color by default.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KolasTypography,
        content = content,
    )
}
```

## Usage Rules

- **Never hardcode emotion colors in composables** — always resolve via `EmotionColors.forEmotion(...)`.
- **Use `MaterialTheme.colorScheme.*` for all chrome** (backgrounds, text, borders); emotion colors are accents only.
- **Disabled dynamic color by default** so the brand palette stays consistent across devices. Opt in per-screen only if needed.
- **On emotion-colored surfaces, use white text** for `happy`/`alert`/`angry` (sufficient contrast); use `onSurface` for `relax`/`frown` in dark theme.

```kotlin
@Composable
fun EmotionBadge(emotion: String) {
    val bg = EmotionColors.forEmotion(emotion)
    Surface(
        color = bg,
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            text = emotion.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
```

## Accessibility

| Pair | Ratio | Verdict |
|------|-------|---------|
| `#8B5A2B` on `#FFF8F5` | ~6.4:1 | AA pass (normal text) |
| `#E8A33D` on `#FFFFFF` | ~2.1:1 | Use dark text on amber |
| `#C1272D` on `#FFFFFF` | ~5.6:1 | AA pass |
| `#7FA87F` on `#FFFFFF` | ~3.0:1 | Large text only |
| `#EDE0D9` on `#18120E` | ~13.5:1 | AAA pass (dark theme body) |

**Rule:** amber (`happy`) and sage (`relax`) badges must use `onSurface` (dark) text in light theme, not white.

## Related Skills

- **android-jetpack-compose**: theming, Material 3 components
- **android-ai-integration**: emotion classifier output → color mapping
