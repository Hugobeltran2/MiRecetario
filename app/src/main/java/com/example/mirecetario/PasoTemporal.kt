package com.example.mirecetario

data class PasoTemporal(
    val descripcion: String,
    val multimedia: List<MultimediaTemporal> = emptyList()
)