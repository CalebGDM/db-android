package com.example.basededatos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.basededatos.databinding.ItemPersonaBinding

class AlumnoAdapter(
    private var listaAlumnos: List<Alumno>,
    private val onEditarClick: (Alumno) -> Unit,
    private val onEliminarClick: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    inner class AlumnoViewHolder(val binding: ItemPersonaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemPersonaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        with(holder.binding) {
            tvNombre.text = alumno.nombre
            tvCuenta.text = "Cuenta: ${alumno.cuenta}"
            tvCorreo.text = alumno.correo

            if (alumno.imagen.isNotBlank()) {
                Glide.with(root.context)
                    .load(alumno.imagen)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .centerCrop()
                    .into(ivFoto)
            } else {
                ivFoto.setImageResource(R.drawable.ic_person)
            }

            btnEditar.setOnClickListener {
                onEditarClick(alumno)
            }

            btnEliminar.setOnClickListener {
                onEliminarClick(alumno)
            }
        }
    }

    override fun getItemCount(): Int = listaAlumnos.size

    fun actualizarLista(nuevaLista: List<Alumno>) {
        listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}