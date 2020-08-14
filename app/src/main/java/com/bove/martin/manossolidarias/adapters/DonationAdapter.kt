package com.bove.martin.manossolidarias.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.model.Donacion
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.donation_item.view.*

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
class DonationAdapter(private val donaciones: List<Donacion>, private val layout: Int, private val activity: Activity, private val listener: OnItemClickListener, private val longClick: OnLongClickListener) : RecyclerView.Adapter<DonationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflamos la vista
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(donaciones[position], listener, longClick)
    }

    override fun getItemCount() = donaciones.size

    // ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Aca es donde se cargan las datos reales
        fun bind(donacion: Donacion, listener: OnItemClickListener, longClickListener: OnLongClickListener) {
            itemView.textViewOngName.text = donacion.nombre
            Glide.with(activity).load(donacion.icon_url).fitCenter().placeholder(R.drawable.ic_place_holder).into(itemView.imageViewIcon)

            // Destacamos el item de agregar donación
            if (donacion.especial) {
                itemView.viewBackground.setImageDrawable(itemView.resources.getDrawable(R.drawable.button_donation_new))
            }

            // Añadimos el onClick
            itemView.setOnClickListener { listener.onItemClick(donacion, adapterPosition) }
            // Añadimos el LongClick
            itemView.setOnLongClickListener {
                longClickListener.onLongClick(donacion, adapterPosition)
                true
            }
        }


    }

    // Interfaz que define el onClick del adapter
    interface OnItemClickListener {
        fun onItemClick(donacion: Donacion?, posicion: Int)
    }

    interface OnLongClickListener {
        fun onLongClick(donacion: Donacion?, posicion: Int)
    }

}