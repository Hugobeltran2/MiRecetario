package com.example.mirecetario

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mirecetario.ui.theme.MiRecetarioTheme
import kotlinx.coroutines.launch


/*
 * =========================================================
 * COLORES
 * =========================================================
 */

private val FondoApp = Color(0xFFF8F3EC)
private val Terracota = Color(0xFFD97957)
private val TerracotaOscuro = Color(0xFFB65F43)

private val VerdeSalvia = Color(0xFF7F9868)
private val VerdeMuyClaro = Color(0xFFEAF0E6)

private val TarjetaClara = Color(0xFFFFFCF8)
private val BordeSuave = Color(0xFFE8D7CC)

private val TextoOscuro = Color(0xFF3E2F2A)
private val TextoSecundario = Color(0xFF7B6A64)

private val RojoEliminar = Color(0xFFB54A43)

private val NaranjaDecorativo = Color(0xFFF2C8B8)
private val VerdeDecorativo = Color(0xFFDDE7D6)


/*
 * =========================================================
 * PANTALLAS
 * =========================================================
 */

enum class Pantalla {
    INICIO,
    NUEVA_RECETA,
    DETALLE_RECETA,
    EDITAR_RECETA,
    LISTA_COMPRAS
}


/*
 * =========================================================
 * ACTIVITY
 * =========================================================
 */

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            MiRecetarioTheme {

                AppMiRecetario()
            }
        }
    }
}


/*
 * =========================================================
 * APP / NAVEGACIÓN
 * =========================================================
 */

@Composable
fun AppMiRecetario() {

    val context =
        LocalContext.current

    val baseDeDatos =
        remember {
            RecetarioDatabase.obtenerBaseDeDatos(
                context
            )
        }

    val recetaDao =
        baseDeDatos.recetaDao()

    val ingredienteDao =
        baseDeDatos.ingredienteDao()

    val pasoRecetaDao =
        baseDeDatos.pasoRecetaDao()

    val itemCompraDao =
        baseDeDatos.itemCompraDao()

    val multimediaPasoDao =
        baseDeDatos.multimediaPasoDao()


    var pantallaActual by remember {
        mutableStateOf(
            Pantalla.INICIO
        )
    }

    var recetaSeleccionada by remember {
        mutableStateOf<Receta?>(null)
    }

    val historialPantallas =
        remember {
            mutableStateListOf<Pantalla>()
        }


    fun navegarA(
        nuevaPantalla: Pantalla
    ) {

        historialPantallas.add(
            pantallaActual
        )

        pantallaActual =
            nuevaPantalla
    }


    fun regresarPantallaAnterior() {

        if (
            historialPantallas.isNotEmpty()
        ) {

            pantallaActual =
                historialPantallas.removeAt(
                    historialPantallas.lastIndex
                )
        }
    }


    BackHandler(
        enabled =
            historialPantallas.isNotEmpty()
    ) {

        regresarPantallaAnterior()
    }


    when (
        pantallaActual
    ) {

        Pantalla.INICIO -> {

            PantallaInicio(
                recetaDao =
                    recetaDao,

                onNuevaReceta = {

                    navegarA(
                        Pantalla.NUEVA_RECETA
                    )
                },

                onListaCompras = {

                    navegarA(
                        Pantalla.LISTA_COMPRAS
                    )
                },

                onAbrirReceta = {
                        receta ->

                    recetaSeleccionada =
                        receta

                    navegarA(
                        Pantalla.DETALLE_RECETA
                    )
                }
            )
        }


        Pantalla.NUEVA_RECETA -> {

            PantallaNuevaReceta(
                recetaDao =
                    recetaDao,

                ingredienteDao =
                    ingredienteDao,

                pasoRecetaDao =
                    pasoRecetaDao,

                multimediaPasoDao =
                    multimediaPasoDao,

                onGuardada = {

                    regresarPantallaAnterior()
                },

                onRegresar = {

                    regresarPantallaAnterior()
                }
            )
        }


        Pantalla.DETALLE_RECETA -> {

            recetaSeleccionada?.let {
                    receta ->

                PantallaDetalleReceta(
                    receta =
                        receta,

                    recetaDao =
                        recetaDao,

                    ingredienteDao =
                        ingredienteDao,

                    pasoRecetaDao =
                        pasoRecetaDao,

                    itemCompraDao =
                        itemCompraDao,

                    multimediaPasoDao =
                        multimediaPasoDao,

                    onEditar = {

                        navegarA(
                            Pantalla.EDITAR_RECETA
                        )
                    },

                    onEliminada = {

                        historialPantallas.clear()

                        recetaSeleccionada =
                            null

                        pantallaActual =
                            Pantalla.INICIO
                    },

                    onRegresar = {

                        regresarPantallaAnterior()
                    }
                )
            }
        }


        Pantalla.EDITAR_RECETA -> {

            recetaSeleccionada?.let {
                    receta ->

                PantallaEditarReceta(
                    receta =
                        receta,

                    baseDeDatos =
                        baseDeDatos,

                    recetaDao =
                        recetaDao,

                    ingredienteDao =
                        ingredienteDao,

                    pasoRecetaDao =
                        pasoRecetaDao,

                    multimediaPasoDao =
                        multimediaPasoDao,

                    onGuardado = {
                            recetaActualizada ->

                        recetaSeleccionada =
                            recetaActualizada

                        regresarPantallaAnterior()
                    },

                    onCancelar = {

                        regresarPantallaAnterior()
                    }
                )
            }
        }


        Pantalla.LISTA_COMPRAS -> {

            PantallaListaCompras(
                itemCompraDao =
                    itemCompraDao,

                onRegresar = {

                    regresarPantallaAnterior()
                }
            )
        }
    }
}


/*
 * =========================================================
 * LOGO
 * =========================================================
 */

@Composable
fun LogoRecetario(
    modifier: Modifier = Modifier
) {

    Image(
        painter =
            painterResource(
                id =
                    R.drawable.logo_recetario
            ),

        contentDescription =
            "Logo Mi Recetario",

        contentScale =
            ContentScale.Fit,

        modifier =
            modifier
    )
}


/*
 * =========================================================
 * ENCABEZADO INICIO
 * =========================================================
 */

@Composable
fun EncabezadoDecorativo() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                132.dp
            )
    ) {

        Box(
            modifier = Modifier
                .size(
                    58.dp
                )
                .align(
                    Alignment.TopStart
                )
                .background(
                    NaranjaDecorativo.copy(
                        alpha = 0.38f
                    ),
                    CircleShape
                )
        )


        Box(
            modifier = Modifier
                .size(
                    48.dp
                )
                .align(
                    Alignment.CenterEnd
                )
                .background(
                    VerdeDecorativo.copy(
                        alpha = 0.60f
                    ),
                    CircleShape
                )
        )


        Box(
            modifier = Modifier
                .size(
                    16.dp
                )
                .align(
                    Alignment.TopEnd
                )
                .background(
                    Terracota.copy(
                        alpha = 0.14f
                    ),
                    CircleShape
                )
        )


        Box(
            modifier = Modifier
                .size(
                    7.dp
                )
                .align(
                    Alignment.CenterStart
                )
                .background(
                    VerdeSalvia.copy(
                        alpha = 0.45f
                    ),
                    CircleShape
                )
        )


        LogoRecetario(
            modifier = Modifier
                .size(
                    114.dp
                )
                .align(
                    Alignment.Center
                )
        )
    }
}


/*
 * =========================================================
 * SEPARADOR
 * =========================================================
 */

@Composable
fun SeparadorRecetas() {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .height(
                    1.dp
                )
                .weight(
                    1f
                )
                .background(
                    BordeSuave
                )
        )


        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )


        Box(
            modifier = Modifier
                .size(
                    7.dp
                )
                .background(
                    Terracota,
                    CircleShape
                )
        )


        Spacer(
            modifier =
                Modifier.width(
                    5.dp
                )
        )


        Box(
            modifier = Modifier
                .size(
                    4.dp
                )
                .background(
                    VerdeSalvia,
                    CircleShape
                )
        )


        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )


        Box(
            modifier = Modifier
                .height(
                    1.dp
                )
                .weight(
                    1f
                )
                .background(
                    BordeSuave
                )
        )
    }
}


/*
 * =========================================================
 * INICIO
 * =========================================================
 */

@Composable
fun PantallaInicio(
    recetaDao: RecetaDao,
    onNuevaReceta: () -> Unit,
    onListaCompras: () -> Unit,
    onAbrirReceta: (Receta) -> Unit
) {

    var recetas by remember {
        mutableStateOf<List<Receta>>(
            emptyList()
        )
    }

    var busqueda by remember {
        mutableStateOf("")
    }


    LaunchedEffect(Unit) {

        recetas =
            recetaDao.obtenerTodas()
    }


    val recetasFiltradas =

        recetas.filter {

            it.nombre.contains(
                busqueda.trim(),
                ignoreCase = true
            )
        }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                FondoApp
            )
            .statusBarsPadding(),

        contentPadding =
            PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 8.dp,
                bottom = 28.dp
            )
    ) {

        item {

            EncabezadoDecorativo()


            Text(
                text =
                    "Mi Recetario",

                style =
                    MaterialTheme
                        .typography
                        .headlineMedium,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextoOscuro
            )


            Text(
                text =
                    "Tus recetas, siempre contigo",

                style =
                    MaterialTheme
                        .typography
                        .bodyLarge,

                color =
                    TextoSecundario
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            OutlinedTextField(
                value =
                    busqueda,

                onValueChange = {

                    busqueda =
                        it
                },

                leadingIcon = {

                    Icon(
                        imageVector =
                            Icons.Default.Search,

                        contentDescription =
                            null,

                        tint =
                            TerracotaOscuro
                    )
                },

                placeholder = {

                    Text(
                        "Buscar receta..."
                    )
                },

                singleLine =
                    true,

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedBorderColor =
                            Terracota,

                        unfocusedBorderColor =
                            BordeSuave,

                        focusedContainerColor =
                            Color.White,

                        unfocusedContainerColor =
                            Color.White
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            Button(
                onClick =
                    onNuevaReceta,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Terracota,

                        contentColor =
                            Color.White
                    ),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            7.dp
                        )
                )

                Text(
                    text =
                        "Agregar receta",

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        9.dp
                    )
            )


            OutlinedButton(
                onClick =
                    onListaCompras,

                border =
                    BorderStroke(
                        1.5.dp,
                        Terracota
                    ),

                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                TerracotaOscuro
                        ),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ShoppingCart,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            7.dp
                        )
                )

                Text(
                    "Lista de compras"
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        22.dp
                    )
            )


            SeparadorRecetas()


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            "Mis recetas",

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextoOscuro
                    )


                    Text(
                        text =
                            "Tu colección personal",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            TextoSecundario
                    )
                }


                Text(
                    text =
                        if (
                            recetas.size == 1
                        ) {
                            "1 receta"
                        } else {
                            "${recetas.size} recetas"
                        },

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        TerracotaOscuro
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )
        }


        if (
            recetasFiltradas.isEmpty()
        ) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            22.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                TarjetaClara
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            BordeSuave
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                24.dp
                            ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.MenuBook,

                            contentDescription =
                                null,

                            tint =
                                Terracota,

                            modifier =
                                Modifier.size(
                                    38.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )


                        Text(
                            text =
                                if (
                                    busqueda.isBlank()
                                ) {
                                    "Aún no tienes recetas"
                                } else {
                                    "No encontramos recetas"
                                },

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoOscuro
                        )
                    }
                }
            }


        } else {

            items(
                items =
                    recetasFiltradas,

                key = {
                    it.id
                }
            ) {
                    receta ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical =
                                5.dp
                        )
                        .clickable {

                            onAbrirReceta(
                                receta
                            )
                        },

                    shape =
                        RoundedCornerShape(
                            20.dp
                        ),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                TarjetaClara
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            BordeSuave
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation =
                                1.dp
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                14.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .width(
                                    5.dp
                                )
                                .height(
                                    50.dp
                                )
                                .background(
                                    Terracota,
                                    RoundedCornerShape(
                                        10.dp
                                    )
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.width(
                                    12.dp
                                )
                        )


                        Box(
                            modifier = Modifier
                                .size(
                                    45.dp
                                )
                                .background(
                                    Terracota.copy(
                                        alpha =
                                            0.10f
                                    ),
                                    CircleShape
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.MenuBook,

                                contentDescription =
                                    null,

                                tint =
                                    TerracotaOscuro
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    11.dp
                                )
                        )


                        Column(
                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        ) {

                            Text(
                                text =
                                    receta.nombre,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextoOscuro
                            )


                            Text(
                                text =
                                    "Toca para ver la receta",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,

                                color =
                                    TextoSecundario
                            )
                        }


                        Text(
                            text =
                                "Abrir",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,

                            fontWeight =
                                FontWeight.SemiBold,

                            color =
                                TerracotaOscuro
                        )
                    }
                }
            }
        }
    }
}


/*
 * =========================================================
 * NUEVA RECETA
 * =========================================================
 */

@Composable
fun PantallaNuevaReceta(
    recetaDao: RecetaDao,
    ingredienteDao: IngredienteDao,
    pasoRecetaDao: PasoRecetaDao,
    multimediaPasoDao: MultimediaPasoDao,
    onGuardada: () -> Unit,
    onRegresar: () -> Unit
) {

    val context =
        LocalContext.current

    val scope =
        rememberCoroutineScope()


    var nombre by remember {
        mutableStateOf("")
    }


    var ingredienteActual by remember {
        mutableStateOf("")
    }


    val ingredientes =
        remember {
            mutableStateListOf<String>()
        }


    var pasoActual by remember {
        mutableStateOf("")
    }


    val pasos =
        remember {
            mutableStateListOf<PasoTemporal>()
        }


    /*
     * Multimedia que pertenece al paso
     * que todavía estamos escribiendo.
     */
    val multimediaActual =
        remember {
            mutableStateListOf<MultimediaTemporal>()
        }


    var tipoSolicitado by remember {
        mutableStateOf<String?>(null)
    }


    var pasoAEliminar by remember {
        mutableStateOf<Int?>(null)
    }

    var multimediaTemporalAEliminar by remember {
        mutableStateOf<Int?>(null)
    }

    var multimediaDePasoAEliminar by remember {
        mutableStateOf<Pair<Int, Int>?>(null)
    }

    var mensaje by remember {
        mutableStateOf("")
    }


    /*
     * =====================================================
     * SELECTOR FOTO / VIDEO
     * =====================================================
     */
    val selectorMultimedia =

        rememberLauncherForActivityResult(
            contract =
                PickVisualMedia()
        ) {
                uri ->

            if (
                uri != null
            ) {

                try {

                    context
                        .contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                } catch (_: Exception) {
                }


                if (
                    multimediaActual.size < 5 &&
                    tipoSolicitado != null
                ) {

                    multimediaActual.add(

                        MultimediaTemporal(
                            uri =
                                uri.toString(),

                            tipo =
                                tipoSolicitado!!
                        )
                    )

                } else if (
                    multimediaActual.size >= 5
                ) {

                    Toast.makeText(
                        context,
                        "Máximo 5 archivos multimedia por paso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            tipoSolicitado =
                null
        }

    /*
     * =====================================================
     * CONFIRMACIÓN ELIMINAR MULTIMEDIA TEMPORAL
     * =====================================================
     */
    if (
        multimediaTemporalAEliminar != null
    ) {

        val indice =
            multimediaTemporalAEliminar!!

        val multimedia =
            multimediaActual
                .getOrNull(
                    indice
                )

        val nombreTipo =
            if (
                multimedia?.tipo == "video"
            ) {
                "video"
            } else {
                "foto"
            }

        AlertDialog(
            onDismissRequest = {

                multimediaTemporalAEliminar =
                    null
            },

            title = {

                Text(
                    "Eliminar multimedia"
                )
            },

            text = {

                Text(
                    "¿Seguro que deseas eliminar esta $nombreTipo?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        if (
                            indice in multimediaActual.indices
                        ) {

                            multimediaActual.removeAt(
                                indice
                            )
                        }

                        multimediaTemporalAEliminar =
                            null
                    }
                ) {

                    Text(
                        text =
                            "Eliminar",

                        color =
                            RojoEliminar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        multimediaTemporalAEliminar =
                            null
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }


    /*
 * =====================================================
 * CONFIRMACIÓN ELIMINAR MULTIMEDIA DE PASO AGREGADO
 * =====================================================
 */
    if (
        multimediaDePasoAEliminar != null
    ) {

        val (
            indicePaso,
            indiceMultimedia
        ) = multimediaDePasoAEliminar!!

        val multimedia =
            pasos
                .getOrNull(
                    indicePaso
                )
                ?.multimedia
                ?.getOrNull(
                    indiceMultimedia
                )

        val nombreTipo =
            if (
                multimedia?.tipo == "video"
            ) {
                "video"
            } else {
                "foto"
            }

        AlertDialog(
            onDismissRequest = {

                multimediaDePasoAEliminar =
                    null
            },

            title = {

                Text(
                    "Eliminar multimedia"
                )
            },

            text = {

                Text(
                    "¿Seguro que deseas eliminar esta $nombreTipo del paso ${indicePaso + 1}?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        if (
                            indicePaso in pasos.indices
                        ) {

                            val paso =
                                pasos[indicePaso]

                            val nuevaMultimedia =
                                paso.multimedia
                                    .toMutableList()

                            if (
                                indiceMultimedia
                                in nuevaMultimedia.indices
                            ) {

                                nuevaMultimedia.removeAt(
                                    indiceMultimedia
                                )
                            }

                            pasos[indicePaso] =
                                paso.copy(
                                    multimedia =
                                        nuevaMultimedia
                                )
                        }

                        multimediaDePasoAEliminar =
                            null
                    }
                ) {

                    Text(
                        text =
                            "Eliminar",

                        color =
                            RojoEliminar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        multimediaDePasoAEliminar =
                            null
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }




    /*
     * =====================================================
     * CONFIRMACIÓN ELIMINAR PASO
     * =====================================================
     */
    if (
        pasoAEliminar != null
    ) {

        val indice =
            pasoAEliminar!!


        AlertDialog(
            onDismissRequest = {

                pasoAEliminar =
                    null
            },

            title = {

                Text(
                    "Eliminar paso"
                )
            },

            text = {

                Text(
                    "¿Estás seguro de que deseas eliminar el paso ${indice + 1}?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        if (
                            indice in pasos.indices
                        ) {

                            pasos.removeAt(
                                indice
                            )
                        }

                        pasoAEliminar =
                            null
                    }
                ) {

                    Text(
                        text =
                            "Eliminar",

                        color =
                            RojoEliminar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        pasoAEliminar =
                            null
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                FondoApp
            )
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        /*
         * =================================================
         * CABECERA
         * =================================================
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    92.dp
                )
        ) {

            Box(
                modifier = Modifier
                    .size(
                        58.dp
                    )
                    .align(
                        Alignment.TopStart
                    )
                    .background(
                        Terracota.copy(
                            alpha =
                                0.08f
                        ),
                        CircleShape
                    )
            )


            Box(
                modifier = Modifier
                    .size(
                        40.dp
                    )
                    .align(
                        Alignment.BottomEnd
                    )
                    .background(
                        VerdeSalvia.copy(
                            alpha =
                                0.09f
                        ),
                        CircleShape
                    )
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(
                        Alignment.Center
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick =
                        onRegresar,

                    modifier = Modifier
                        .size(
                            46.dp
                        )
                        .background(
                            TarjetaClara,
                            RoundedCornerShape(
                                15.dp
                            )
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Regresar",

                        tint =
                            TerracotaOscuro
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            14.dp
                        )
                )


                Box(
                    modifier = Modifier
                        .size(
                            44.dp
                        )
                        .background(
                            Terracota.copy(
                                alpha =
                                    0.12f
                            ),
                            CircleShape
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Add,

                        contentDescription =
                            null,

                        tint =
                            TerracotaOscuro
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            10.dp
                        )
                )


                Column {

                    Text(
                        text =
                            "Nueva receta",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextoOscuro
                    )


                    Text(
                        text =
                            "Crea tu receta paso a paso",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            TextoSecundario
                    )
                }
            }
        }


        /*
         * =================================================
         * NOMBRE
         * =================================================
         */
        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    24.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaClara
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeSuave
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        18.dp
                    )
            ) {

                Text(
                    text =
                        "Nombre de la receta",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextoOscuro
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                OutlinedTextField(
                    value =
                        nombre,

                    onValueChange = {

                        nombre =
                            it
                    },

                    placeholder = {

                        Text(
                            "Ej. Pasta cremosa"
                        )
                    },

                    singleLine =
                        true,

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =
                                Terracota,

                            unfocusedBorderColor =
                                BordeSuave,

                            focusedContainerColor =
                                Color.White,

                            unfocusedContainerColor =
                                Color.White
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        /*
         * =================================================
         * INGREDIENTES
         * =================================================
         */
        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    24.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaClara
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeSuave
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        18.dp
                    )
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(
                                40.dp
                            )
                            .background(
                                VerdeMuyClaro,
                                CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Add,

                            contentDescription =
                                null,

                            tint =
                                VerdeSalvia
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                10.dp
                            )
                    )


                    Column(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(
                            text =
                                "Ingredientes",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoOscuro
                        )


                        Text(
                            text =
                                "Agrega todo lo que necesitas",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                TextoSecundario
                        )
                    }


                    Text(
                        text =
                            "${ingredientes.size}",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            VerdeSalvia
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )


                OutlinedTextField(
                    value =
                        ingredienteActual,

                    onValueChange = {

                        ingredienteActual =
                            it
                    },

                    placeholder = {

                        Text(
                            "Ej. 2 tomates"
                        )
                    },

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =
                                VerdeSalvia,

                            unfocusedBorderColor =
                                BordeSuave,

                            focusedContainerColor =
                                Color.White,

                            unfocusedContainerColor =
                                Color.White
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            9.dp
                        )
                )


                OutlinedButton(
                    onClick = {

                        if (
                            ingredienteActual.isNotBlank()
                        ) {

                            ingredientes.add(
                                ingredienteActual.trim()
                            )

                            ingredienteActual =
                                ""
                        }
                    },

                    border =
                        BorderStroke(
                            1.5.dp,
                            VerdeSalvia
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    VerdeSalvia
                            ),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Add,

                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                7.dp
                            )
                    )

                    Text(
                        "Agregar ingrediente"
                    )
                }


                ingredientes.forEachIndexed {
                        indice,
                        ingrediente ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top =
                                    9.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    FondoApp
                            )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 12.dp,
                                    top = 6.dp,
                                    bottom = 6.dp,
                                    end = 4.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(
                                        30.dp
                                    )
                                    .background(
                                        VerdeSalvia.copy(
                                            alpha =
                                                0.12f
                                        ),
                                        CircleShape
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        "${indice + 1}",

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        VerdeSalvia
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(
                                        10.dp
                                    )
                            )


                            Text(
                                text =
                                    ingrediente,

                                color =
                                    TextoOscuro,

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )


                            IconButton(
                                onClick = {

                                    ingredientes.removeAt(
                                        indice
                                    )
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,

                                    contentDescription =
                                        "Eliminar ingrediente",

                                    tint =
                                        RojoEliminar
                                )
                            }
                        }
                    }
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        /*
         * =================================================
         * PREPARACIÓN
         * =================================================
         */
        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    24.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaClara
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeSuave
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        18.dp
                    )
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(
                                40.dp
                            )
                            .background(
                                Terracota.copy(
                                    alpha =
                                        0.11f
                                ),
                                CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.MenuBook,

                            contentDescription =
                                null,

                            tint =
                                TerracotaOscuro
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                10.dp
                            )
                    )


                    Column(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(
                            text =
                                "Preparación",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoOscuro
                        )


                        Text(
                            text =
                                "Describe cada paso",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                TextoSecundario
                        )
                    }


                    Text(
                        text =
                            "${pasos.size}",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TerracotaOscuro
                    )
                }


                /*
                 * PASOS YA AGREGADOS
                 */
                pasos.forEachIndexed {
                        indice,
                        paso ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top =
                                    12.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    FondoApp
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(
                                    14.dp
                                )
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(
                                            34.dp
                                        )
                                        .background(
                                            Terracota,
                                            CircleShape
                                        ),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            "${indice + 1}",

                                        color =
                                            Color.White,

                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }


                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            9.dp
                                        )
                                )


                                Text(
                                    text =
                                        "Paso ${indice + 1}",

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        TextoOscuro,

                                    modifier =
                                        Modifier.weight(
                                            1f
                                        )
                                )


                                IconButton(
                                    onClick = {

                                        pasoAEliminar =
                                            indice
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Delete,

                                        contentDescription =
                                            "Eliminar paso",

                                        tint =
                                            RojoEliminar
                                    )
                                }
                            }


                            Text(
                                text =
                                    paso.descripcion,

                                color =
                                    TextoOscuro
                            )


                            if (
                                paso.multimedia.isNotEmpty()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            10.dp
                                        )
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(
                                            rememberScrollState()
                                        )
                                ) {

                                    paso.multimedia.forEachIndexed {
                                            indiceMultimedia,
                                            multimedia ->

                                        Card(
                                            modifier = Modifier
                                                .width(
                                                    145.dp
                                                )
                                                .padding(
                                                    end =
                                                        9.dp
                                                ),

                                            shape =
                                                RoundedCornerShape(
                                                    16.dp
                                                ),

                                            colors =
                                                CardDefaults.cardColors(
                                                    containerColor =
                                                        TarjetaClara
                                                )
                                        ) {

                                            Column {

                                                if (
                                                    multimedia.tipo ==
                                                    "imagen"
                                                ) {

                                                    AsyncImage(
                                                        model =
                                                            Uri.parse(
                                                                multimedia.uri
                                                            ),

                                                        contentDescription =
                                                            "Foto del paso ${indice + 1}",

                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(
                                                                100.dp
                                                            ),

                                                        contentScale =
                                                            ContentScale.Crop
                                                    )

                                                } else {

                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(
                                                                100.dp
                                                            )
                                                            .background(
                                                                VerdeMuyClaro
                                                            ),

                                                        contentAlignment =
                                                            Alignment.Center
                                                    ) {

                                                        Icon(
                                                            imageVector =
                                                                Icons.Default.PlayArrow,

                                                            contentDescription =
                                                                "Video",

                                                            tint =
                                                                VerdeSalvia,

                                                            modifier =
                                                                Modifier.size(
                                                                    40.dp
                                                                )
                                                        )
                                                    }
                                                }


                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            start = 8.dp,
                                                            end = 2.dp,
                                                            top = 2.dp,
                                                            bottom = 2.dp
                                                        ),

                                                    verticalAlignment =
                                                        Alignment.CenterVertically
                                                ) {

                                                    Text(
                                                        text =
                                                            if (
                                                                multimedia.tipo ==
                                                                "imagen"
                                                            ) {
                                                                "Foto"
                                                            } else {
                                                                "Video"
                                                            },

                                                        style =
                                                            MaterialTheme
                                                                .typography
                                                                .bodySmall,

                                                        color =
                                                            TextoSecundario,

                                                        modifier =
                                                            Modifier.weight(
                                                                1f
                                                            )
                                                    )


                                                    IconButton(
                                                        onClick = {

                                                            multimediaDePasoAEliminar =
                                                                Pair(
                                                                    indice,
                                                                    indiceMultimedia
                                                                )
                                                        },

                                                        modifier =
                                                            Modifier.size(
                                                                36.dp
                                                            )
                                                    ) {

                                                        Icon(
                                                            imageVector =
                                                                Icons.Default.Delete,

                                                            contentDescription =
                                                                "Eliminar multimedia",

                                                            tint =
                                                                RojoEliminar,

                                                            modifier =
                                                                Modifier.size(
                                                                    18.dp
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                /*
                 * NUEVO PASO
                 */
                OutlinedTextField(
                    value =
                        pasoActual,

                    onValueChange = {

                        pasoActual =
                            it
                    },

                    placeholder = {

                        Text(
                            "Escribe qué debe hacerse..."
                        )
                    },

                    minLines =
                        3,

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    colors =
                        OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =
                                Terracota,

                            unfocusedBorderColor =
                                BordeSuave,

                            focusedContainerColor =
                                Color.White,

                            unfocusedContainerColor =
                                Color.White
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                /*
                 * CONTADOR MULTIMEDIA
                 */
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "Fotos y videos",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            TextoOscuro,

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    Text(
                        text =
                            "${multimediaActual.size}/5",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            TextoSecundario
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {

                            if (
                                multimediaActual.size < 5
                            ) {

                                tipoSolicitado =
                                    "imagen"


                                selectorMultimedia.launch(

                                    PickVisualMediaRequest(
                                        PickVisualMedia.ImageOnly
                                    )
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    "Máximo 5 archivos multimedia por paso",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },

                        border =
                            BorderStroke(
                                1.4.dp,
                                Terracota
                            ),

                        colors =
                            ButtonDefaults
                                .outlinedButtonColors(
                                    contentColor =
                                        TerracotaOscuro
                                ),

                        shape =
                            RoundedCornerShape(
                                15.dp
                            ),

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.PhotoCamera,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    5.dp
                                )
                        )

                        Text(
                            "Foto"
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                8.dp
                            )
                    )


                    OutlinedButton(
                        onClick = {

                            if (
                                multimediaActual.size < 5
                            ) {

                                tipoSolicitado =
                                    "video"


                                selectorMultimedia.launch(

                                    PickVisualMediaRequest(
                                        PickVisualMedia.VideoOnly
                                    )
                                )

                            } else {

                                Toast.makeText(
                                    context,
                                    "Máximo 5 archivos multimedia por paso",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },

                        border =
                            BorderStroke(
                                1.4.dp,
                                VerdeSalvia
                            ),

                        colors =
                            ButtonDefaults
                                .outlinedButtonColors(
                                    contentColor =
                                        VerdeSalvia
                                ),

                        shape =
                            RoundedCornerShape(
                                15.dp
                            ),

                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Videocam,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(
                                    5.dp
                                )
                        )

                        Text(
                            "Video"
                        )
                    }
                }


                /*
                 * =================================================
                 * GALERÍA HORIZONTAL TEMPORAL
                 * =================================================
                 */
                if (
                    multimediaActual.isNotEmpty()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            )
                    ) {

                        multimediaActual.forEachIndexed {
                                indice,
                                multimedia ->

                            Card(
                                modifier = Modifier
                                    .width(
                                        145.dp
                                    )
                                    .padding(
                                        end =
                                            9.dp
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        16.dp
                                    ),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            FondoApp
                                    )
                            ) {

                                Column {

                                    if (
                                        multimedia.tipo ==
                                        "imagen"
                                    ) {

                                        AsyncImage(
                                            model =
                                                Uri.parse(
                                                    multimedia.uri
                                                ),

                                            contentDescription =
                                                "Foto ${indice + 1}",

                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    100.dp
                                                ),

                                            contentScale =
                                                ContentScale.Crop
                                        )

                                    } else {

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    100.dp
                                                )
                                                .background(
                                                    VerdeMuyClaro
                                                ),

                                            contentAlignment =
                                                Alignment.Center
                                        ) {

                                            Icon(
                                                imageVector =
                                                    Icons.Default.PlayArrow,

                                                contentDescription =
                                                    null,

                                                tint =
                                                    VerdeSalvia,

                                                modifier =
                                                    Modifier.size(
                                                        40.dp
                                                    )
                                            )
                                        }
                                    }


                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                start = 8.dp,
                                                end = 2.dp,
                                                top = 2.dp,
                                                bottom = 2.dp
                                            ),

                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text =
                                                if (
                                                    multimedia.tipo ==
                                                    "imagen"
                                                ) {
                                                    "Foto"
                                                } else {
                                                    "Video"
                                                },

                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .bodySmall,

                                            color =
                                                TextoSecundario,

                                            modifier =
                                                Modifier.weight(
                                                    1f
                                                )
                                        )


                                        IconButton(
                                            onClick = {

                                                multimediaTemporalAEliminar =
                                                    indice
                                            },

                                            modifier =
                                                Modifier.size(
                                                    36.dp
                                                )
                                        ) {

                                            Icon(
                                                imageVector =
                                                    Icons.Default.Delete,

                                                contentDescription =
                                                    "Quitar multimedia",

                                                tint =
                                                    RojoEliminar,

                                                modifier =
                                                    Modifier.size(
                                                        18.dp
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                OutlinedButton(
                    onClick = {

                        if (
                            pasoActual.isNotBlank()
                        ) {

                            pasos.add(

                                PasoTemporal(
                                    descripcion =
                                        pasoActual.trim(),

                                    multimedia =
                                        multimediaActual.toList()
                                )
                            )


                            pasoActual =
                                ""

                            multimediaActual.clear()
                        }
                    },

                    border =
                        BorderStroke(
                            1.5.dp,
                            VerdeSalvia
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    VerdeSalvia
                            ),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Add,

                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                7.dp
                            )
                    )

                    Text(
                        "Agregar paso"
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        /*
         * =================================================
         * GUARDAR RECETA
         * =================================================
         */
        Button(
            onClick = {

                if (
                    nombre.isBlank()
                ) {

                    mensaje =
                        "Escribe el nombre de la receta"

                } else if (
                    ingredientes.isEmpty()
                ) {

                    mensaje =
                        "Agrega al menos un ingrediente"

                } else if (
                    pasos.isEmpty()
                ) {

                    mensaje =
                        "Agrega al menos un paso"

                } else {

                    scope.launch {

                        val recetaId =

                            recetaDao.insertar(

                                Receta(
                                    nombre =
                                        nombre.trim()
                                )
                            ).toInt()


                        /*
                         * INGREDIENTES
                         */
                        ingredientes.forEachIndexed {
                                indice,
                                descripcion ->

                            ingredienteDao.insertar(

                                Ingrediente(
                                    recetaId =
                                        recetaId,

                                    descripcion =
                                        descripcion,

                                    orden =
                                        indice + 1
                                )
                            )
                        }


                        /*
                         * PASOS Y MULTIMEDIA
                         */
                        pasos.forEachIndexed {
                                indicePaso,
                                pasoTemporal ->

                            pasoRecetaDao.insertar(

                                PasoReceta(
                                    recetaId =
                                        recetaId,

                                    numeroPaso =
                                        indicePaso + 1,

                                    descripcion =
                                        pasoTemporal.descripcion,

                                    multimediaUri =
                                        null,

                                    tipoMultimedia =
                                        null
                                )
                            )


                            /*
                             * Obtenemos el paso recién guardado.
                             */
                            val pasosGuardados =
                                pasoRecetaDao.obtenerPorReceta(
                                    recetaId
                                )


                            val pasoGuardado =

                                pasosGuardados.firstOrNull {

                                    it.numeroPaso ==
                                            indicePaso + 1
                                }


                            if (
                                pasoGuardado != null
                            ) {

                                pasoTemporal.multimedia
                                    .forEachIndexed {
                                            indiceMultimedia,
                                            multimedia ->

                                        multimediaPasoDao.insertar(

                                            MultimediaPaso(
                                                pasoId =
                                                    pasoGuardado.id,

                                                uri =
                                                    multimedia.uri,

                                                tipo =
                                                    multimedia.tipo,

                                                orden =
                                                    indiceMultimedia + 1
                                            )
                                        )
                                    }
                            }
                        }


                        onGuardada()
                    }
                }
            },

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        Terracota,

                    contentColor =
                        Color.White
                ),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector =
                    Icons.Default.Save,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
                    )
            )

            Text(
                text =
                    "Guardar receta",

                fontWeight =
                    FontWeight.Bold
            )
        }


        if (
            mensaje.isNotEmpty()
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            Text(
                text =
                    mensaje,

                color =
                    RojoEliminar,

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        OutlinedButton(
            onClick =
                onRegresar,

            border =
                BorderStroke(
                    1.5.dp,
                    Terracota
                ),

            colors =
                ButtonDefaults
                    .outlinedButtonColors(
                        contentColor =
                            TerracotaOscuro
                    ),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.width(
                        7.dp
                    )
            )

            Text(
                "Regresar"
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    30.dp
                )
        )
    }
}


/*
 * =========================================================
 * DETALLE
 * =========================================================
 */

@Composable
fun PantallaDetalleReceta(
    receta: Receta,
    recetaDao: RecetaDao,
    ingredienteDao: IngredienteDao,
    pasoRecetaDao: PasoRecetaDao,
    itemCompraDao: ItemCompraDao,
    multimediaPasoDao: MultimediaPasoDao,
    onEditar: () -> Unit,
    onEliminada: () -> Unit,
    onRegresar: () -> Unit
) {

    val context =
        LocalContext.current

    val scope =
        rememberCoroutineScope()


    var ingredientes by remember {
        mutableStateOf<List<Ingrediente>>(
            emptyList()
        )
    }


    var pasos by remember {
        mutableStateOf<List<PasoReceta>>(
            emptyList()
        )
    }


    val multimediaPorPaso =
        remember {
            mutableStateMapOf<Int, List<MultimediaPaso>>()
        }


    var mostrarConfirmacionEliminar by remember {
        mutableStateOf(false)
    }


    var mensajeCompra by remember {
        mutableStateOf("")
    }


    LaunchedEffect(
        receta.id
    ) {

        ingredientes =
            ingredienteDao.obtenerPorReceta(
                receta.id
            )


        pasos =
            pasoRecetaDao.obtenerPorReceta(
                receta.id
            )


        multimediaPorPaso.clear()


        pasos.forEach {
                paso ->

            val nuevaMultimedia =

                multimediaPasoDao.obtenerPorPaso(
                    paso.id
                ).toMutableList()


            /*
             * Compatibilidad con recetas antiguas
             * que tenían una sola multimedia
             * guardada directamente en PasoReceta.
             */
            if (
                nuevaMultimedia.isEmpty() &&
                paso.multimediaUri != null &&
                paso.tipoMultimedia != null
            ) {

                nuevaMultimedia.add(

                    MultimediaPaso(
                        id =
                            0,

                        pasoId =
                            paso.id,

                        uri =
                            paso.multimediaUri,

                        tipo =
                            paso.tipoMultimedia,

                        orden =
                            1
                    )
                )
            }


            multimediaPorPaso[paso.id] =
                nuevaMultimedia
        }
    }


    if (
        mostrarConfirmacionEliminar
    ) {

        AlertDialog(
            onDismissRequest = {

                mostrarConfirmacionEliminar =
                    false
            },

            title = {

                Text(
                    "Eliminar receta"
                )
            },

            text = {

                Text(
                    "¿Seguro que deseas eliminar \"${receta.nombre}\"? Esta acción no se puede deshacer."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        mostrarConfirmacionEliminar =
                            false


                        scope.launch {

                            recetaDao.eliminar(
                                receta
                            )

                            onEliminada()
                        }
                    }
                ) {

                    Text(
                        text =
                            "Eliminar",

                        color =
                            RojoEliminar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostrarConfirmacionEliminar =
                            false
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                FondoApp
            )
            .statusBarsPadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        /*
         * CABECERA
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onRegresar,

                modifier = Modifier
                    .size(
                        46.dp
                    )
                    .background(
                        TarjetaClara,
                        RoundedCornerShape(
                            15.dp
                        )
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Regresar",

                    tint =
                        TerracotaOscuro
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        14.dp
                    )
            )


            Box(
                modifier = Modifier
                    .size(
                        44.dp
                    )
                    .background(
                        Terracota.copy(
                            alpha =
                                0.12f
                        ),
                        CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.MenuBook,

                    contentDescription =
                        null,

                    tint =
                        TerracotaOscuro
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )


            Column {

                Text(
                    text =
                        "Detalle de receta",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextoOscuro
                )


                Text(
                    text =
                        "Ingredientes y preparación",

                    color =
                        TextoSecundario
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    22.dp
                )
        )


        /*
         * NOMBRE
         */
        Text(
            text =
                receta.nombre,

            style =
                MaterialTheme
                    .typography
                    .headlineMedium,

            fontWeight =
                FontWeight.Bold,

            color =
                TextoOscuro
        )


        Spacer(
            modifier =
                Modifier.height(
                    4.dp
                )
        )


        Text(
            text =
                "${ingredientes.size} ${
                    if (
                        ingredientes.size == 1
                    ) {
                        "ingrediente"
                    } else {
                        "ingredientes"
                    }
                } · ${pasos.size} ${
                    if (
                        pasos.size == 1
                    ) {
                        "paso"
                    } else {
                        "pasos"
                    }
                }",

            color =
                TextoSecundario
        )


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        /*
         * INGREDIENTES
         */
        Card(
            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(
                    24.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaClara
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeSuave
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(
                        18.dp
                    )
            ) {

                Text(
                    text =
                        "Ingredientes",

                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextoOscuro
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                ingredientes.forEachIndexed {
                        indice,
                        ingrediente ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 12.dp,
                                top = 3.dp,
                                bottom = 3.dp,
                                end = 4.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    VerdeMuyClaro,
                                    CircleShape
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "${indice + 1}",

                                color =
                                    VerdeSalvia,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    10.dp
                                )
                        )


                        Text(
                            text =
                                ingrediente.descripcion,

                            color =
                                TextoOscuro
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                OutlinedButton(
                    onClick = {

                        scope.launch {

                            var agregados =
                                0

                            var repetidos =
                                0


                            ingredientes.forEach {
                                    ingrediente ->

                                val existe =

                                    itemCompraDao
                                        .existeDescripcion(
                                            ingrediente.descripcion
                                        )


                                if (
                                    existe == 0
                                ) {

                                    itemCompraDao.insertar(

                                        ItemCompra(
                                            descripcion =
                                                ingrediente.descripcion
                                        )
                                    )

                                    agregados++

                                } else {

                                    repetidos++
                                }
                            }


                            mensajeCompra =

                                if (
                                    repetidos == 0
                                ) {
                                    "$agregados ingredientes agregados a la lista"
                                } else {
                                    "$agregados agregados y $repetidos ya estaban en la lista"
                                }
                        }
                    },

                    border =
                        BorderStroke(
                            1.5.dp,
                            VerdeSalvia
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    VerdeSalvia
                            ),

                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ShoppingCart,

                        contentDescription =
                            null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                7.dp
                            )
                    )

                    Text(
                        "Agregar a lista de compras"
                    )
                }


                if (
                    mensajeCompra.isNotBlank()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            mensajeCompra,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            VerdeSalvia
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        /*
         * PREPARACIÓN
         */
        Row(
            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    "Preparación",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold,

                color =
                    TextoOscuro,

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            Text(
                text =
                    if (
                        pasos.size == 1
                    ) {
                        "1 paso"
                    } else {
                        "${pasos.size} pasos"
                    },

                color =
                    TerracotaOscuro
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        pasos.forEach {
                paso ->

            val archivos =

                multimediaPorPaso[
                    paso.id
                ] ?: emptyList()


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical =
                            6.dp
                    ),

                shape =
                    RoundedCornerShape(
                        22.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            TarjetaClara
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        BordeSuave
                    )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            16.dp
                        )
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(
                                    38.dp
                                )
                                .background(
                                    Terracota,
                                    CircleShape
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text =
                                    "${paso.numeroPaso}",

                                color =
                                    Color.White,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    10.dp
                                )
                        )


                        Text(
                            text =
                                "Paso ${paso.numeroPaso}",

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoOscuro
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    Text(
                        text =
                            paso.descripcion,

                        color =
                            TextoOscuro
                    )


                    /*
                     * GALERÍA HORIZONTAL
                     */
                    if (
                        archivos.isNotEmpty()
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                )
                        ) {

                            archivos.forEach {
                                    multimedia ->

                                if (
                                    multimedia.tipo ==
                                    "imagen"
                                ) {

                                    AsyncImage(
                                        model =
                                            Uri.parse(
                                                multimedia.uri
                                            ),

                                        contentDescription =
                                            "Foto del paso",

                                        modifier = Modifier
                                            .width(
                                                190.dp
                                            )
                                            .height(
                                                125.dp
                                            )
                                            .padding(
                                                end =
                                                    9.dp
                                            ),

                                        contentScale =
                                            ContentScale.Crop
                                    )

                                } else {

                                    Card(
                                        modifier = Modifier
                                            .width(
                                                170.dp
                                            )
                                            .height(
                                                125.dp
                                            )
                                            .padding(
                                                end =
                                                    9.dp
                                            )
                                            .clickable {

                                                try {

                                                    val uri =
                                                        Uri.parse(
                                                            multimedia.uri
                                                        )


                                                    val tipoMime =

                                                        context
                                                            .contentResolver
                                                            .getType(
                                                                uri
                                                            )
                                                            ?: "video/*"


                                                    val intent =

                                                        Intent(
                                                            Intent.ACTION_VIEW
                                                        ).apply {

                                                            setDataAndType(
                                                                uri,
                                                                tipoMime
                                                            )

                                                            addFlags(
                                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                            )
                                                        }


                                                    context.startActivity(

                                                        Intent.createChooser(
                                                            intent,
                                                            "Reproducir video"
                                                        )
                                                    )

                                                } catch (_: Exception) {

                                                    Toast.makeText(
                                                        context,
                                                        "No se pudo abrir el video",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },

                                        shape =
                                            RoundedCornerShape(
                                                16.dp
                                            ),

                                        colors =
                                            CardDefaults.cardColors(
                                                containerColor =
                                                    VerdeMuyClaro
                                            )
                                    ) {

                                        Box(
                                            modifier =
                                                Modifier.fillMaxSize(),

                                            contentAlignment =
                                                Alignment.Center
                                        ) {

                                            Column(
                                                horizontalAlignment =
                                                    Alignment.CenterHorizontally
                                            ) {

                                                Icon(
                                                    imageVector =
                                                        Icons.Default.PlayArrow,

                                                    contentDescription =
                                                        null,

                                                    tint =
                                                        VerdeSalvia,

                                                    modifier =
                                                        Modifier.size(
                                                            40.dp
                                                        )
                                                )


                                                Text(
                                                    text =
                                                        "Reproducir",

                                                    color =
                                                        VerdeSalvia
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    22.dp
                )
        )


        /*
         * OPCIONES
         */
        OutlinedButton(
            onClick =
                onEditar,

            border =
                BorderStroke(
                    1.5.dp,
                    Terracota
                ),

            colors =
                ButtonDefaults
                    .outlinedButtonColors(
                        contentColor =
                            TerracotaOscuro
                    ),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector =
                    Icons.Default.Edit,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.width(
                        7.dp
                    )
            )

            Text(
                "Editar receta"
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    9.dp
                )
        )


        OutlinedButton(
            onClick = {

                mostrarConfirmacionEliminar =
                    true
            },

            border =
                BorderStroke(
                    1.4.dp,
                    RojoEliminar
                ),

            colors =
                ButtonDefaults
                    .outlinedButtonColors(
                        contentColor =
                            RojoEliminar
                    ),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector =
                    Icons.Default.Delete,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.width(
                        7.dp
                    )
            )

            Text(
                "Eliminar receta"
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    30.dp
                )
        )
    }
}


/*
 * =========================================================
 * LISTA DE COMPRAS
 * =========================================================
 */

@Composable
fun PantallaListaCompras(
    itemCompraDao: ItemCompraDao,
    onRegresar: () -> Unit
) {

    var itemsCompra by remember {
        mutableStateOf<List<ItemCompra>>(
            emptyList()
        )
    }


    var mostrarConfirmacionVaciar by remember {
        mutableStateOf(false)
    }


    var itemAEliminar by remember {
        mutableStateOf<ItemCompra?>(null)
    }


    val scope =
        rememberCoroutineScope()


    suspend fun actualizarLista() {

        itemsCompra =
            itemCompraDao.obtenerTodos()
    }


    LaunchedEffect(Unit) {

        actualizarLista()
    }


    val pendientes =

        itemsCompra.filter {
            !it.comprado
        }


    val comprados =

        itemsCompra.filter {
            it.comprado
        }


    /*
     * CONFIRMACIÓN PRODUCTO
     */
    if (
        itemAEliminar != null
    ) {

        val item =
            itemAEliminar!!


        AlertDialog(
            onDismissRequest = {

                itemAEliminar =
                    null
            },

            title = {

                Text(
                    "Eliminar producto"
                )
            },

            text = {

                Text(
                    "¿Seguro que deseas eliminar \"${item.descripcion}\" de la lista de compras?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        scope.launch {

                            itemCompraDao.eliminar(
                                item
                            )

                            actualizarLista()

                            itemAEliminar =
                                null
                        }
                    }
                ) {

                    Text(
                        text =
                            "Eliminar",

                        color =
                            RojoEliminar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        itemAEliminar =
                            null
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }


    /*
     * CONFIRMACIÓN VACIAR
     */
    if (
        mostrarConfirmacionVaciar
    ) {

        AlertDialog(
            onDismissRequest = {

                mostrarConfirmacionVaciar =
                    false
            },

            title = {

                Text(
                    "Vaciar lista"
                )
            },

            text = {

                Text(
                    "¿Seguro que deseas eliminar todos los productos de la lista de compras?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        mostrarConfirmacionVaciar =
                            false


                        scope.launch {

                            itemCompraDao.eliminarTodos()

                            actualizarLista()
                        }
                    }
                ) {

                    Text(
                        text =
                            "Vaciar lista",

                        color =
                            RojoEliminar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostrarConfirmacionVaciar =
                            false
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                FondoApp
            )
            .statusBarsPadding()
            .padding(
                horizontal = 20.dp,
                vertical = 12.dp
            )
    ) {

        /*
         * CABECERA
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    82.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick =
                    onRegresar,

                modifier = Modifier
                    .size(
                        46.dp
                    )
                    .background(
                        TarjetaClara,
                        RoundedCornerShape(
                            15.dp
                        )
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ArrowBack,

                    contentDescription =
                        "Regresar",

                    tint =
                        TerracotaOscuro
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        14.dp
                    )
            )


            Box(
                modifier = Modifier
                    .size(
                        44.dp
                    )
                    .background(
                        VerdeMuyClaro,
                        CircleShape
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.ShoppingCart,

                    contentDescription =
                        null,

                    tint =
                        VerdeSalvia
                )
            }


            Spacer(
                modifier =
                    Modifier.width(
                        10.dp
                    )
            )


            Column {

                Text(
                    text =
                        "Lista de compras",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextoOscuro
                )


                Text(
                    text =
                        "Organiza lo que necesitas",

                    color =
                        TextoSecundario
                )
            }
        }


        /*
         * CONTADORES
         */
        Text(
            text =
                "${pendientes.size} pendientes · ${comprados.size} comprados",

            color =
                TextoSecundario
        )


        Spacer(
            modifier =
                Modifier.height(
                    14.dp
                )
        )


        if (
            itemsCompra.isEmpty()
        ) {

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        24.dp
                    ),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            TarjetaClara
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        BordeSuave
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            28.dp
                        ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.ShoppingCart,

                        contentDescription =
                            null,

                        tint =
                            VerdeSalvia,

                        modifier =
                            Modifier.size(
                                44.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    Text(
                        text =
                            "Tu lista está vacía",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextoOscuro
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )

                    Text(
                        text =
                            "Agrega ingredientes desde una receta",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            TextoSecundario
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )


        } else {

            LazyColumn(
                modifier = Modifier
                    .weight(
                        1f
                    )
                    .fillMaxWidth(),

                contentPadding =
                    PaddingValues(
                        bottom =
                            12.dp
                    )
            ) {

                item {

                    Text(
                        text =
                            "Pendientes",

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextoOscuro
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )
                }


                items(
                    items =
                        pendientes,

                    key = {
                        it.id
                    }
                ) {
                        item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical =
                                    4.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    TarjetaClara
                            ),

                        border =
                            BorderStroke(
                                1.dp,
                                BordeSuave
                            )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    6.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked =
                                    false,

                                onCheckedChange = {

                                    scope.launch {

                                        itemCompraDao.actualizar(

                                            item.copy(
                                                comprado =
                                                    true
                                            )
                                        )

                                        actualizarLista()
                                    }
                                }
                            )


                            Text(
                                text =
                                    item.descripcion,

                                color =
                                    TextoOscuro,

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )


                            IconButton(
                                onClick = {

                                    itemAEliminar =
                                        item
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,

                                    contentDescription =
                                        "Eliminar producto",

                                    tint =
                                        RojoEliminar
                                )
                            }
                        }
                    }
                }


                item {

                    Spacer(
                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )


                    Text(
                        text =
                            "Comprados",

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextoOscuro
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )
                }


                items(
                    items =
                        comprados,

                    key = {
                        it.id
                    }
                ) {
                        item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical =
                                    4.dp
                            ),

                        shape =
                            RoundedCornerShape(
                                18.dp
                            ),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    TarjetaClara
                            ),

                        border =
                            BorderStroke(
                                1.dp,
                                BordeSuave
                            )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    6.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked =
                                    true,

                                onCheckedChange = {

                                    scope.launch {

                                        itemCompraDao.actualizar(

                                            item.copy(
                                                comprado =
                                                    false
                                            )
                                        )

                                        actualizarLista()
                                    }
                                }
                            )


                            Text(
                                text =
                                    item.descripcion,

                                color =
                                    TextoSecundario,

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )


                            IconButton(
                                onClick = {

                                    itemAEliminar =
                                        item
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Delete,

                                    contentDescription =
                                        "Eliminar producto",

                                    tint =
                                        RojoEliminar
                                )
                            }
                        }
                    }
                }
            }


            OutlinedButton(
                onClick = {

                    mostrarConfirmacionVaciar =
                        true
                },

                border =
                    BorderStroke(
                        1.4.dp,
                        RojoEliminar
                    ),

                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                RojoEliminar
                        ),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Delete,

                    contentDescription =
                        null
                )

                Spacer(
                    modifier =
                        Modifier.width(
                            7.dp
                        )
                )

                Text(
                    "Vaciar lista"
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    9.dp
                )
        )


        OutlinedButton(
            onClick =
                onRegresar,

            border =
                BorderStroke(
                    1.5.dp,
                    Terracota
                ),

            colors =
                ButtonDefaults
                    .outlinedButtonColors(
                        contentColor =
                            TerracotaOscuro
                    ),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Icon(
                imageVector =
                    Icons.Default.ArrowBack,

                contentDescription =
                    null
            )

            Spacer(
                modifier =
                    Modifier.width(
                        7.dp
                    )
            )

            Text(
                "Regresar"
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )
    }
}