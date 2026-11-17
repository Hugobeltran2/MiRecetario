package com.example.mirecetario

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.withTransaction
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch


private val FondoEditar = Color(0xFFF8F3EC)
private val TerracotaEditar = Color(0xFFD97957)
private val TerracotaOscuroEditar = Color(0xFFB65F43)

private val VerdeSalviaEditar = Color(0xFF7F9868)
private val VerdeClaroEditar = Color(0xFFEAF0E6)

private val TarjetaEditar = Color(0xFFFFFCF8)
private val BordeEditar = Color(0xFFE8D7CC)

private val TextoEditar = Color(0xFF3E2F2A)
private val TextoSecundarioEditar = Color(0xFF7B6A64)

private val RojoEditar = Color(0xFFB54A43)


private data class SolicitudEliminarMultimedia(
    val indicePaso: Int?,
    val indiceMultimedia: Int,
    val esPasoNuevo: Boolean
)


@Composable
fun PantallaEditarReceta(
    receta: Receta,
    baseDeDatos: RecetarioDatabase,
    recetaDao: RecetaDao,
    ingredienteDao: IngredienteDao,
    pasoRecetaDao: PasoRecetaDao,
    multimediaPasoDao: MultimediaPasoDao,
    onGuardado: (Receta) -> Unit,
    onCancelar: () -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember(receta.id) {
        mutableStateOf(receta.nombre)
    }

    var ingredienteActual by remember {
        mutableStateOf("")
    }

    val ingredientes = remember {
        mutableStateListOf<String>()
    }

    val pasos = remember {
        mutableStateListOf<PasoTemporal>()
    }

    var pasoActual by remember {
        mutableStateOf("")
    }

    /*
     * Multimedia para el NUEVO paso que todavía
     * no ha sido agregado a la lista de pasos.
     */
    val multimediaActual = remember {
        mutableStateListOf<MultimediaTemporal>()
    }

    var tipoSolicitado by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * null = multimedia para paso nuevo.
     * número = índice de un paso existente.
     */
    var indicePasoMultimedia by remember {
        mutableStateOf<Int?>(null)
    }

    var pasoAEliminar by remember {
        mutableStateOf<Int?>(null)
    }

    var multimediaAEliminar by remember {
        mutableStateOf<SolicitudEliminarMultimedia?>(null)
    }

    var mostrarSalirSinGuardar by remember {
        mutableStateOf(false)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    /*
     * Copias originales para detectar cambios.
     */
    var datosCargados by remember {
        mutableStateOf(false)
    }

    var nombreOriginal by remember {
        mutableStateOf("")
    }

    var ingredientesOriginales by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var pasosOriginales by remember {
        mutableStateOf<List<PasoTemporal>>(emptyList())
    }


    /*
     * =====================================================
     * CARGAR DATOS EXISTENTES
     * =====================================================
     */
    LaunchedEffect(receta.id) {

        datosCargados = false

        nombre = receta.nombre

        val ingredientesGuardados =
            ingredienteDao.obtenerPorReceta(receta.id)

        ingredientes.clear()

        ingredientes.addAll(
            ingredientesGuardados.map {
                it.descripcion
            }
        )

        val pasosGuardados =
            pasoRecetaDao.obtenerPorReceta(receta.id)

        val pasosTemporales =
            mutableListOf<PasoTemporal>()

        for (paso in pasosGuardados) {

            val multimediaNueva =
                multimediaPasoDao.obtenerPorPaso(
                    paso.id
                )

            val listaMultimedia =
                mutableListOf<MultimediaTemporal>()

            /*
             * Multimedia guardada en la tabla nueva.
             */
            multimediaNueva.forEach { multimedia ->

                listaMultimedia.add(
                    MultimediaTemporal(
                        uri = multimedia.uri,
                        tipo = multimedia.tipo
                    )
                )
            }

            /*
             * Compatibilidad con recetas antiguas.
             *
             * Si la tabla nueva está vacía pero el paso
             * todavía tiene multimediaUri/tipoMultimedia,
             * la recuperamos.
             */
            if (
                listaMultimedia.isEmpty() &&
                paso.multimediaUri != null &&
                paso.tipoMultimedia != null
            ) {

                listaMultimedia.add(
                    MultimediaTemporal(
                        uri = paso.multimediaUri,
                        tipo = paso.tipoMultimedia
                    )
                )
            }

            pasosTemporales.add(
                PasoTemporal(
                    descripcion = paso.descripcion,
                    multimedia = listaMultimedia
                )
            )
        }

        pasos.clear()
        pasos.addAll(pasosTemporales)

        /*
         * Guardamos fotografía original de los datos.
         */
        nombreOriginal = nombre
        ingredientesOriginales =
            ingredientes.toList()

        pasosOriginales =
            pasos.map { paso ->

                paso.copy(
                    multimedia =
                        paso.multimedia.toList()
                )
            }

        datosCargados = true
    }


    /*
     * =====================================================
     * DETECTAR CAMBIOS SIN GUARDAR
     * =====================================================
     */
    val hayCambiosSinGuardar =

        datosCargados && (

                nombre != nombreOriginal ||

                        ingredientes.toList() !=
                        ingredientesOriginales ||

                        pasos.toList() !=
                        pasosOriginales ||

                        ingredienteActual.isNotBlank() ||

                        pasoActual.isNotBlank() ||

                        multimediaActual.isNotEmpty()
                )


    fun intentarSalir() {

        if (hayCambiosSinGuardar) {

            mostrarSalirSinGuardar = true

        } else {

            onCancelar()
        }
    }


    /*
     * =====================================================
     * BOTÓN / GESTO ATRÁS DE ANDROID
     * =====================================================
     */
    BackHandler {

        intentarSalir()
    }


    /*
     * =====================================================
     * REPRODUCIR VIDEO
     * =====================================================
     */
    fun reproducirVideo(
        uriTexto: String
    ) {

        try {

            val uri =
                Uri.parse(uriTexto)

            val tipoMime =
                context
                    .contentResolver
                    .getType(uri)
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
    }


    /*
     * =====================================================
     * SELECTOR FOTO / VIDEO
     * =====================================================
     */
    val selectorMultimedia =
        rememberLauncherForActivityResult(
            contract = PickVisualMedia()
        ) { uri ->

            if (
                uri != null &&
                tipoSolicitado != null
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

                val nuevaMultimedia =
                    MultimediaTemporal(
                        uri = uri.toString(),
                        tipo = tipoSolicitado!!
                    )

                /*
                 * Multimedia para paso existente.
                 */
                if (
                    indicePasoMultimedia != null
                ) {

                    val indicePaso =
                        indicePasoMultimedia!!

                    if (
                        indicePaso in pasos.indices
                    ) {

                        val paso =
                            pasos[indicePaso]

                        if (
                            paso.multimedia.size < 5
                        ) {

                            pasos[indicePaso] =
                                paso.copy(
                                    multimedia =
                                        paso.multimedia +
                                                nuevaMultimedia
                                )

                        } else {

                            Toast.makeText(
                                context,
                                "Máximo 5 archivos multimedia por paso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {

                    /*
                     * Multimedia para paso nuevo.
                     */
                    if (
                        multimediaActual.size < 5
                    ) {

                        multimediaActual.add(
                            nuevaMultimedia
                        )

                    } else {

                        Toast.makeText(
                            context,
                            "Máximo 5 archivos multimedia por paso",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            indicePasoMultimedia = null
            tipoSolicitado = null
        }


    /*
     * =====================================================
     * CONFIRMAR SALIR SIN GUARDAR
     * =====================================================
     */
    if (mostrarSalirSinGuardar) {

        AlertDialog(
            onDismissRequest = {

                mostrarSalirSinGuardar = false
            },

            title = {

                Text(
                    "Cambios sin guardar"
                )
            },

            text = {

                Text(
                    "Has realizado cambios en la receta. ¿Seguro que deseas salir sin guardarlos?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        mostrarSalirSinGuardar = false

                        onCancelar()
                    }
                ) {

                    Text(
                        text = "Salir sin guardar",
                        color = RojoEditar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        mostrarSalirSinGuardar = false
                    }
                ) {

                    Text(
                        "Seguir editando"
                    )
                }
            }
        )
    }


    /*
     * =====================================================
     * CONFIRMAR ELIMINAR PASO
     * =====================================================
     */
    if (pasoAEliminar != null) {

        val indice =
            pasoAEliminar!!

        AlertDialog(
            onDismissRequest = {

                pasoAEliminar = null
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

                            pasos.removeAt(indice)
                        }

                        pasoAEliminar = null
                    }
                ) {

                    Text(
                        text = "Eliminar",
                        color = RojoEditar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        pasoAEliminar = null
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
     * CONFIRMAR ELIMINAR MULTIMEDIA
     * =====================================================
     */
    if (multimediaAEliminar != null) {

        val solicitud =
            multimediaAEliminar!!

        val multimedia =

            if (solicitud.esPasoNuevo) {

                multimediaActual
                    .getOrNull(
                        solicitud.indiceMultimedia
                    )

            } else {

                solicitud.indicePaso
                    ?.let { indicePaso ->

                        pasos
                            .getOrNull(indicePaso)
                            ?.multimedia
                            ?.getOrNull(
                                solicitud.indiceMultimedia
                            )
                    }
            }

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

                multimediaAEliminar = null
            },

            title = {

                Text(
                    "Eliminar multimedia"
                )
            },

            text = {

                Text(
                    "¿Seguro que deseas eliminar esta $nombreTipo del paso?"
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        if (
                            solicitud.esPasoNuevo
                        ) {

                            if (
                                solicitud.indiceMultimedia
                                in multimediaActual.indices
                            ) {

                                multimediaActual.removeAt(
                                    solicitud.indiceMultimedia
                                )
                            }

                        } else {

                            val indicePaso =
                                solicitud.indicePaso

                            if (
                                indicePaso != null &&
                                indicePaso in pasos.indices
                            ) {

                                val paso =
                                    pasos[indicePaso]

                                val nuevaLista =
                                    paso.multimedia
                                        .toMutableList()

                                if (
                                    solicitud.indiceMultimedia
                                    in nuevaLista.indices
                                ) {

                                    nuevaLista.removeAt(
                                        solicitud.indiceMultimedia
                                    )
                                }

                                pasos[indicePaso] =
                                    paso.copy(
                                        multimedia =
                                            nuevaLista
                                    )
                            }
                        }

                        multimediaAEliminar = null
                    }
                ) {

                    Text(
                        text = "Eliminar",
                        color = RojoEditar
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {

                        multimediaAEliminar = null
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
     * PANTALLA
     * =====================================================
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoEditar)
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
                .height(92.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .align(
                        Alignment.TopStart
                    )
                    .background(
                        TerracotaEditar.copy(
                            alpha = 0.08f
                        ),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(
                        Alignment.BottomEnd
                    )
                    .background(
                        VerdeSalviaEditar.copy(
                            alpha = 0.09f
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
                    onClick = {

                        intentarSalir()
                    },

                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            TarjetaEditar,
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
                            TerracotaOscuroEditar
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            TerracotaEditar.copy(
                                alpha = 0.12f
                            ),
                            CircleShape
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Edit,

                        contentDescription =
                            null,

                        tint =
                            TerracotaOscuroEditar
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Column {

                    Text(
                        text =
                            "Editar receta",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TextoEditar
                    )

                    Text(
                        text =
                            "Actualiza los datos de tu receta",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        color =
                            TextoSecundarioEditar
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
                RoundedCornerShape(24.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaEditar
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeEditar
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                TerracotaEditar.copy(
                                    alpha = 0.10f
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
                                TerracotaOscuroEditar
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(9.dp)
                    )

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
                            TextoEditar
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value =
                        nombre,

                    onValueChange = {

                        nombre = it
                    },

                    placeholder = {

                        Text(
                            "Nombre de la receta"
                        )
                    },

                    singleLine = true,

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                TerracotaEditar,

                            unfocusedBorderColor =
                                BordeEditar,

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
                Modifier.height(16.dp)
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
                RoundedCornerShape(24.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaEditar
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeEditar
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                VerdeClaroEditar,
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
                                VerdeSalviaEditar
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
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
                                TextoEditar
                        )

                        Text(
                            text =
                                "Modifica lo que necesitas",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                TextoSecundarioEditar
                        )
                    }

                    Text(
                        text =
                            "${ingredientes.size}",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            VerdeSalviaEditar
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )


                OutlinedTextField(
                    value =
                        ingredienteActual,

                    onValueChange = {

                        ingredienteActual = it
                    },

                    placeholder = {

                        Text(
                            "Ej. 2 tomates"
                        )
                    },

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                VerdeSalviaEditar,

                            unfocusedBorderColor =
                                BordeEditar,

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
                        Modifier.height(9.dp)
                )


                OutlinedButton(
                    onClick = {

                        if (
                            ingredienteActual.isNotBlank()
                        ) {

                            ingredientes.add(
                                ingredienteActual.trim()
                            )

                            ingredienteActual = ""
                        }
                    },

                    border =
                        BorderStroke(
                            1.5.dp,
                            VerdeSalviaEditar
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    VerdeSalviaEditar
                            ),

                    shape =
                        RoundedCornerShape(16.dp),

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
                            Modifier.width(7.dp)
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
                                top = 9.dp
                            ),

                        shape =
                            RoundedCornerShape(16.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    FondoEditar
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
                                    .size(30.dp)
                                    .background(
                                        VerdeSalviaEditar.copy(
                                            alpha = 0.12f
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
                                        VerdeSalviaEditar
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Text(
                                text =
                                    ingrediente,

                                color =
                                    TextoEditar,

                                modifier =
                                    Modifier.weight(1f)
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
                                        RojoEditar
                                )
                            }
                        }
                    }
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(16.dp)
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
                RoundedCornerShape(24.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        TarjetaEditar
                ),

            border =
                BorderStroke(
                    1.dp,
                    BordeEditar
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(18.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                TerracotaEditar.copy(
                                    alpha = 0.11f
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
                                TerracotaOscuroEditar
                        )
                    }

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
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
                                TextoEditar
                        )

                        Text(
                            text =
                                "Edita o agrega pasos",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                TextoSecundarioEditar
                        )
                    }

                    Text(
                        text =
                            "${pasos.size}",

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            TerracotaOscuroEditar
                    )
                }


                /*
                 * =================================================
                 * PASOS EXISTENTES
                 * =================================================
                 */
                pasos.forEachIndexed {
                        indicePaso,
                        paso ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 12.dp
                            ),

                        shape =
                            RoundedCornerShape(18.dp),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    TarjetaEditar
                            ),

                        border =
                            BorderStroke(
                                1.dp,
                                BordeEditar.copy(
                                    alpha = 0.75f
                                )
                            ),

                        elevation =
                            CardDefaults.cardElevation(
                                defaultElevation = 0.dp
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(16.dp)
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            TerracotaEditar,
                                            CircleShape
                                        ),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            "${indicePaso + 1}",

                                        color =
                                            Color.White,

                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }

                                Spacer(
                                    modifier =
                                        Modifier.width(10.dp)
                                )

                                Text(
                                    text =
                                        "Paso ${indicePaso + 1}",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        TextoEditar,

                                    modifier =
                                        Modifier.weight(1f)
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )


                            OutlinedTextField(
                                value =
                                    paso.descripcion,

                                onValueChange = {
                                        nuevaDescripcion ->

                                    pasos[indicePaso] =
                                        paso.copy(
                                            descripcion =
                                                nuevaDescripcion
                                        )
                                },

                                placeholder = {

                                    Text(
                                        "Descripción del paso"
                                    )
                                },

                                minLines = 3,

                                shape =
                                    RoundedCornerShape(16.dp),

                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor =
                                            TerracotaEditar,

                                        unfocusedBorderColor =
                                            BordeEditar,

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
                                    Modifier.height(12.dp)
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

                                    fontWeight =
                                        FontWeight.SemiBold,

                                    color =
                                        TextoEditar,

                                    modifier =
                                        Modifier.weight(1f)
                                )

                                Text(
                                    text =
                                        "${paso.multimedia.size}/5",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,

                                    color =
                                        TextoSecundarioEditar
                                )
                            }


                            /*
                             * GALERÍA
                             */
                            if (
                                paso.multimedia.isNotEmpty()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(9.dp)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(
                                            rememberScrollState()
                                        )
                                ) {

                                    paso.multimedia
                                        .forEachIndexed {
                                                indiceMultimedia,
                                                multimedia ->

                                            Card(
                                                modifier = Modifier
                                                    .width(145.dp)
                                                    .padding(
                                                        end = 9.dp
                                                    ),

                                                shape =
                                                    RoundedCornerShape(
                                                        16.dp
                                                    ),

                                                colors =
                                                    CardDefaults.cardColors(
                                                        containerColor =
                                                            TarjetaEditar
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
                                                                "Foto",

                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(100.dp),

                                                            contentScale =
                                                                ContentScale.Crop
                                                        )

                                                    } else {

                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(100.dp)
                                                                .background(
                                                                    VerdeClaroEditar
                                                                ),

                                                            contentAlignment =
                                                                Alignment.Center
                                                        ) {

                                                            IconButton(
                                                                onClick = {

                                                                    reproducirVideo(
                                                                        multimedia.uri
                                                                    )
                                                                }
                                                            ) {

                                                                Icon(
                                                                    imageVector =
                                                                        Icons.Default.PlayArrow,

                                                                    contentDescription =
                                                                        "Reproducir video",

                                                                    tint =
                                                                        VerdeSalviaEditar,

                                                                    modifier =
                                                                        Modifier.size(
                                                                            42.dp
                                                                        )
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                start = 8.dp,
                                                                end = 2.dp
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
                                                                TextoSecundarioEditar,

                                                            modifier =
                                                                Modifier.weight(
                                                                    1f
                                                                )
                                                        )

                                                        IconButton(
                                                            onClick = {

                                                                multimediaAEliminar =
                                                                    SolicitudEliminarMultimedia(
                                                                        indicePaso =
                                                                            indicePaso,

                                                                        indiceMultimedia =
                                                                            indiceMultimedia,

                                                                        esPasoNuevo =
                                                                            false
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
                                                                    RojoEditar,

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
                                    Modifier.height(10.dp)
                            )


                            /*
                             * AGREGAR FOTO / VIDEO
                             */
                            Row(
                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                OutlinedButton(
                                    onClick = {

                                        if (
                                            paso.multimedia.size < 5
                                        ) {

                                            indicePasoMultimedia =
                                                indicePaso

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
                                            1.3.dp,
                                            TerracotaEditar
                                        ),

                                    colors =
                                        ButtonDefaults
                                            .outlinedButtonColors(
                                                contentColor =
                                                    TerracotaOscuroEditar
                                            ),

                                    shape =
                                        RoundedCornerShape(14.dp),

                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.PhotoCamera,

                                        contentDescription =
                                            null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(5.dp)
                                    )

                                    Text(
                                        "Foto"
                                    )
                                }


                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )


                                OutlinedButton(
                                    onClick = {

                                        if (
                                            paso.multimedia.size < 5
                                        ) {

                                            indicePasoMultimedia =
                                                indicePaso

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
                                            1.3.dp,
                                            VerdeSalviaEditar
                                        ),

                                    colors =
                                        ButtonDefaults
                                            .outlinedButtonColors(
                                                contentColor =
                                                    VerdeSalviaEditar
                                            ),

                                    shape =
                                        RoundedCornerShape(14.dp),

                                    modifier =
                                        Modifier.weight(1f)
                                ) {

                                    Icon(
                                        imageVector =
                                            Icons.Default.Videocam,

                                        contentDescription =
                                            null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(5.dp)
                                    )

                                    Text(
                                        "Video"
                                    )
                                }
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )


                            OutlinedButton(
                                onClick = {

                                    pasoAEliminar =
                                        indicePaso
                                },

                                border =
                                    BorderStroke(
                                        1.2.dp,
                                        RojoEditar
                                    ),

                                colors =
                                    ButtonDefaults
                                        .outlinedButtonColors(
                                            contentColor =
                                                RojoEditar
                                        ),

                                shape =
                                    RoundedCornerShape(14.dp),

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
                                        Modifier.width(6.dp)
                                )

                                Text(
                                    "Eliminar paso"
                                )
                            }
                        }
                    }
                }


                /*
                 * =================================================
                 * AGREGAR PASO NUEVO
                 * =================================================
                 */
                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                Text(
                    text =
                        "Agregar nuevo paso",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        TextoEditar
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                OutlinedTextField(
                    value =
                        pasoActual,

                    onValueChange = {

                        pasoActual = it
                    },

                    placeholder = {

                        Text(
                            "Escribe qué debe hacerse..."
                        )
                    },

                    minLines = 3,

                    shape =
                        RoundedCornerShape(16.dp),

                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor =
                                TerracotaEditar,

                            unfocusedBorderColor =
                                BordeEditar,

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
                        Modifier.height(10.dp)
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text =
                            "Fotos y videos",

                        fontWeight =
                            FontWeight.SemiBold,

                        color =
                            TextoEditar,

                        modifier =
                            Modifier.weight(1f)
                    )

                    Text(
                        text =
                            "${multimediaActual.size}/5",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            TextoSecundarioEditar
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
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

                                indicePasoMultimedia =
                                    null

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
                                1.3.dp,
                                TerracotaEditar
                            ),

                        colors =
                            ButtonDefaults
                                .outlinedButtonColors(
                                    contentColor =
                                        TerracotaOscuroEditar
                                ),

                        shape =
                            RoundedCornerShape(14.dp),

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.PhotoCamera,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(
                            "Foto"
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )


                    OutlinedButton(
                        onClick = {

                            if (
                                multimediaActual.size < 5
                            ) {

                                indicePasoMultimedia =
                                    null

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
                                1.3.dp,
                                VerdeSalviaEditar
                            ),

                        colors =
                            ButtonDefaults
                                .outlinedButtonColors(
                                    contentColor =
                                        VerdeSalviaEditar
                                ),

                        shape =
                            RoundedCornerShape(14.dp),

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Videocam,

                            contentDescription =
                                null
                        )

                        Spacer(
                            modifier =
                                Modifier.width(5.dp)
                        )

                        Text(
                            "Video"
                        )
                    }
                }


                /*
                 * GALERÍA DEL PASO NUEVO
                 */
                if (
                    multimediaActual.isNotEmpty()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            )
                    ) {

                        multimediaActual
                            .forEachIndexed {
                                    indiceMultimedia,
                                    multimedia ->

                                Card(
                                    modifier = Modifier
                                        .width(145.dp)
                                        .padding(
                                            end = 9.dp
                                        ),

                                    shape =
                                        RoundedCornerShape(16.dp),

                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor =
                                                FondoEditar
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
                                                    "Foto",

                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp),

                                                contentScale =
                                                    ContentScale.Crop
                                            )

                                        } else {

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp)
                                                    .background(
                                                        VerdeClaroEditar
                                                    ),

                                                contentAlignment =
                                                    Alignment.Center
                                            ) {

                                                IconButton(
                                                    onClick = {

                                                        reproducirVideo(
                                                            multimedia.uri
                                                        )
                                                    }
                                                ) {

                                                    Icon(
                                                        imageVector =
                                                            Icons.Default.PlayArrow,

                                                        contentDescription =
                                                            "Reproducir",

                                                        tint =
                                                            VerdeSalviaEditar,

                                                        modifier =
                                                            Modifier.size(
                                                                42.dp
                                                            )
                                                    )
                                                }
                                            }
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 8.dp,
                                                    end = 2.dp
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
                                                    TextoSecundarioEditar,

                                                modifier =
                                                    Modifier.weight(1f)
                                            )

                                            IconButton(
                                                onClick = {

                                                    multimediaAEliminar =
                                                        SolicitudEliminarMultimedia(
                                                            indicePaso =
                                                                null,

                                                            indiceMultimedia =
                                                                indiceMultimedia,

                                                            esPasoNuevo =
                                                                true
                                                        )
                                                },

                                                modifier =
                                                    Modifier.size(36.dp)
                                            ) {

                                                Icon(
                                                    imageVector =
                                                        Icons.Default.Delete,

                                                    contentDescription =
                                                        "Eliminar multimedia",

                                                    tint =
                                                        RojoEditar,

                                                    modifier =
                                                        Modifier.size(18.dp)
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
                        Modifier.height(12.dp)
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

                            pasoActual = ""

                            multimediaActual.clear()
                        }
                    },

                    border =
                        BorderStroke(
                            1.5.dp,
                            VerdeSalviaEditar
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    VerdeSalviaEditar
                            ),

                    shape =
                        RoundedCornerShape(16.dp),

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
                            Modifier.width(7.dp)
                    )

                    Text(
                        "Agregar paso"
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(20.dp)
        )


        /*
         * =================================================
         * GUARDAR CAMBIOS
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
                        "La receta necesita al menos un ingrediente"

                } else if (
                    pasos.isEmpty()
                ) {

                    mensaje =
                        "La receta necesita al menos un paso"

                } else {

                    scope.launch {

                        val recetaActualizada =
                            receta.copy(
                                nombre =
                                    nombre.trim()
                            )

                        baseDeDatos.withTransaction {

                            /*
                             * RECETA
                             */
                            recetaDao.actualizar(
                                recetaActualizada
                            )


                            /*
                             * INGREDIENTES
                             */
                            ingredienteDao
                                .eliminarPorReceta(
                                    receta.id
                                )

                            ingredientes
                                .forEachIndexed {
                                        indice,
                                        descripcion ->

                                    ingredienteDao.insertar(
                                        Ingrediente(
                                            recetaId =
                                                receta.id,

                                            descripcion =
                                                descripcion.trim(),

                                            orden =
                                                indice + 1
                                        )
                                    )
                                }


                            /*
                             * Al borrar pasos, Room elimina
                             * también multimedia_paso por CASCADE.
                             */
                            pasoRecetaDao
                                .eliminarPorReceta(
                                    receta.id
                                )


                            /*
                             * REINSERTAR PASOS Y MULTIMEDIA
                             */
                            pasos.forEachIndexed {
                                    indicePaso,
                                    pasoTemporal ->

                                pasoRecetaDao.insertar(
                                    PasoReceta(
                                        recetaId =
                                            receta.id,

                                        numeroPaso =
                                            indicePaso + 1,

                                        descripcion =
                                            pasoTemporal.descripcion.trim(),

                                        multimediaUri =
                                            null,

                                        tipoMultimedia =
                                            null
                                    )
                                )


                                /*
                                 * Recuperamos el paso que
                                 * acabamos de crear.
                                 */
                                val pasosGuardados =
                                    pasoRecetaDao
                                        .obtenerPorReceta(
                                            receta.id
                                        )

                                val pasoGuardado =
                                    pasosGuardados
                                        .firstOrNull {

                                            it.numeroPaso ==
                                                    indicePaso + 1
                                        }


                                if (
                                    pasoGuardado != null
                                ) {

                                    pasoTemporal
                                        .multimedia
                                        .take(5)
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
                        }


                        onGuardado(
                            recetaActualizada
                        )
                    }
                }
            },

            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        TerracotaEditar,

                    contentColor =
                        Color.White
                ),

            shape =
                RoundedCornerShape(18.dp),

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
                    Modifier.width(8.dp)
            )

            Text(
                text =
                    "Guardar cambios",

                fontWeight =
                    FontWeight.Bold
            )
        }


        if (
            mensaje.isNotBlank()
        ) {

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text =
                    mensaje,

                color =
                    RojoEditar
            )
        }


        Spacer(
            modifier =
                Modifier.height(10.dp)
        )


        /*
         * =================================================
         * CANCELAR
         * =================================================
         */
        OutlinedButton(
            onClick = {

                intentarSalir()
            },

            border =
                BorderStroke(
                    1.5.dp,
                    TerracotaEditar
                ),

            colors =
                ButtonDefaults
                    .outlinedButtonColors(
                        contentColor =
                            TerracotaOscuroEditar
                    ),

            shape =
                RoundedCornerShape(18.dp),

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
                    Modifier.width(7.dp)
            )

            Text(
                "Cancelar edición"
            )
        }


        Spacer(
            modifier =
                Modifier.height(30.dp)
        )
    }
}