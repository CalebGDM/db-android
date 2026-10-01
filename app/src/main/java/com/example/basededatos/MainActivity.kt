package com.example.basededatos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basededatos.databinding.ActivityMainBinding
import com.example.basededatos.databinding.DialogAlumnoBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var adapter: AlumnoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DBHelperAlumno(this)

        setupRecyclerView()
        setupListeners()
        cargarAlumnos()
    }

    private fun setupRecyclerView() {
        adapter = AlumnoAdapter(
            listaAlumnos = emptyList(),
            onEditarClick = { alumno -> mostrarDialogoEditar(alumno) },
            onEliminarClick = { alumno -> mostrarDialogoConfirmarEliminar(alumno) }
        )
        binding.rvAlumnos.layoutManager = LinearLayoutManager(this)
        binding.rvAlumnos.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    private fun cargarAlumnos() {
        val lista = dbHelper.obtenerAlumnos()
        adapter.actualizarLista(lista)

        if (lista.isEmpty()) {
            binding.tvSinAlumnos.visibility = View.VISIBLE
            binding.rvAlumnos.visibility = View.GONE
        } else {
            binding.tvSinAlumnos.visibility = View.GONE
            binding.rvAlumnos.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoAgregar() {
        val dialogBinding = DialogAlumnoBinding.inflate(LayoutInflater.from(this))

        AlertDialog.Builder(this)
            .setTitle("Agregar Alumno")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { dialog, _ ->
                val cuenta = dialogBinding.etCuenta.text.toString().trim()
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val correo = dialogBinding.etCorreo.text.toString().trim()
                val imagen = dialogBinding.etImagen.text.toString().trim()

                if (cuenta.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor completa Cuenta, Nombre y Correo",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val nuevoAlumno = Alumno(
                    nombre = nombre,
                    cuenta = cuenta,
                    correo = correo,
                    imagen = imagen
                )

                val resultado = dbHelper.insertarAlumno(nuevoAlumno)

                if (resultado != -1L) {
                    Toast.makeText(this, "Alumno agregado con éxito", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(
                        this,
                        "Error al agregar: número de cuenta ya existe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun mostrarDialogoEditar(alumno: Alumno) {
        val dialogBinding = DialogAlumnoBinding.inflate(LayoutInflater.from(this))

        // Pre-fill fields with current student data
        dialogBinding.etCuenta.setText(alumno.cuenta)
        dialogBinding.etCuenta.isEnabled = false // Primary key should not be edited
        dialogBinding.etNombre.setText(alumno.nombre)
        dialogBinding.etCorreo.setText(alumno.correo)
        dialogBinding.etImagen.setText(alumno.imagen)

        AlertDialog.Builder(this)
            .setTitle("Editar Alumno")
            .setView(dialogBinding.root)
            .setPositiveButton("Actualizar") { dialog, _ ->
                val nombre = dialogBinding.etNombre.text.toString().trim()
                val correo = dialogBinding.etCorreo.text.toString().trim()
                val imagen = dialogBinding.etImagen.text.toString().trim()

                if (nombre.isEmpty() || correo.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Nombre y Correo no pueden estar vacíos",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val alumnoActualizado = Alumno(
                    nombre = nombre,
                    cuenta = alumno.cuenta,
                    correo = correo,
                    imagen = imagen
                )

                val filasAfectadas = dbHelper.actualizarAlumno(alumnoActualizado)

                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Alumno actualizado", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(this, "Error al actualizar alumno", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun mostrarDialogoConfirmarEliminar(alumno: Alumno) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Alumno")
            .setMessage("¿Deseas eliminar a ${alumno.nombre} (${alumno.cuenta})?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                val filasAfectadas = dbHelper.eliminarAlumno(alumno.cuenta)
                if (filasAfectadas > 0) {
                    Toast.makeText(this, "Alumno eliminado", Toast.LENGTH_SHORT).show()
                    cargarAlumnos()
                } else {
                    Toast.makeText(this, "Error al eliminar alumno", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}