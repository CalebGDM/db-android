package com.example.basededatos

data class Alumno(
    val nombre: String,
    val cuenta: String,
    val correo: String,
    val imagen: String
) {
    val image: String get() = imagen
}