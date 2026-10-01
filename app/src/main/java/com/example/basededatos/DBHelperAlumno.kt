package com.example.basededatos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperAlumno(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ALUMNOS (
                $COLUMN_CUENTA TEXT PRIMARY KEY,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CORREO TEXT NOT NULL,
                $COLUMN_IMAGEN TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed initial sample data
        insertarAlumnoInicial(
            db,
            Alumno(
                nombre = "Juan Pérez",
                cuenta = "31800100",
                correo = "juan.perez@unam.mx",
                imagen = "https://picsum.photos/200/200?random=1"
            )
        )
        insertarAlumnoInicial(
            db,
            Alumno(
                nombre = "María Gómez",
                cuenta = "31800200",
                correo = "maria.gomez@unam.mx",
                imagen = "https://picsum.photos/200/200?random=2"
            )
        )
        insertarAlumnoInicial(
            db,
            Alumno(
                nombre = "Carlos López",
                cuenta = "31800300",
                correo = "carlos.lopez@unam.mx",
                imagen = "https://picsum.photos/200/200?random=3"
            )
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    private fun insertarAlumnoInicial(db: SQLiteDatabase, alumno: Alumno) {
        val values = ContentValues().apply {
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        db.insert(TABLE_ALUMNOS, null, values)
    }

    fun insertarAlumno(alumno: Alumno): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val result = db.insertWithOnConflict(
            TABLE_ALUMNOS,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
        db.close()
        return result
    }

    fun obtenerAlumnos(): List<Alumno> {
        val listaAlumnos = mutableListOf<Alumno>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ALUMNOS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_NOMBRE ASC"
        )

        if (cursor.moveToFirst()) {
            val idxCuenta = cursor.getColumnIndexOrThrow(COLUMN_CUENTA)
            val idxNombre = cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)
            val idxCorreo = cursor.getColumnIndexOrThrow(COLUMN_CORREO)
            val idxImagen = cursor.getColumnIndexOrThrow(COLUMN_IMAGEN)

            do {
                val cuenta = cursor.getString(idxCuenta)
                val nombre = cursor.getString(idxNombre)
                val correo = cursor.getString(idxCorreo)
                val imagen = cursor.getString(idxImagen) ?: ""
                listaAlumnos.add(
                    Alumno(
                        nombre = nombre,
                        cuenta = cuenta,
                        correo = correo,
                        imagen = imagen
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaAlumnos
    }

    fun actualizarAlumno(alumno: Alumno): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        val rows = db.update(
            TABLE_ALUMNOS,
            values,
            "$COLUMN_CUENTA = ?",
            arrayOf(alumno.cuenta)
        )
        db.close()
        return rows
    }

    fun eliminarAlumno(cuenta: String): Int {
        val db = this.writableDatabase
        val rows = db.delete(
            TABLE_ALUMNOS,
            "$COLUMN_CUENTA = ?",
            arrayOf(cuenta)
        )
        db.close()
        return rows
    }
}