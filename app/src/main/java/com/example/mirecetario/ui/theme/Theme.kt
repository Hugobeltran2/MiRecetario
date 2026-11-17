package com.example.mirecetario.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MiRecetarioColorScheme = lightColorScheme(

    // Botones principales
    primary = Terracota,
    onPrimary = Blanco,

    // Elementos secundarios
    secondary = VerdeSalvia,
    onSecondary = Blanco,

    // Fondo general
    background = FondoCrema,
    onBackground = TextoOscuro,

    // Tarjetas, diálogos y campos
    surface = SuperficieClara,
    onSurface = TextoOscuro,

    // Variantes de superficie
    surfaceVariant = FondoCrema,
    onSurfaceVariant = TextoSecundario,

    // Bordes
    outline = BordeSuave,

    // Errores / acciones destructivas
    error = RojoEliminar,
    onError = Blanco
)

@Composable
fun MiRecetarioTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = MiRecetarioColorScheme,
        typography = Typography,
        content = content
    )
}