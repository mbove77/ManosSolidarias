package com.bove.martin.manossolidarias.adapters

import android.app.Activity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.model.Institucion
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.ong_item.view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
class InstitucionesAdapter(instituciones: MutableList<Institucion>, layout: Int, activity: Activity, listener: OnItemClickListener) : RecyclerView.Adapter<InstitucionesAdapter.ViewHolder>(), Filterable {
    private val instituciones: List<Institucion>
    private var institucionesFull: List<Institucion>? = null
    private val layout: Int
    private val listener: OnItemClickListener
    private val activity: Activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflamos la vista
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(instituciones[position], listener)
    }

    override fun getItemCount(): Int {
        return instituciones.size
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    // Filter logic
    private val nameFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterList: MutableList<Institucion> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filterList.addAll(institucionesFull!!)
            } else {
                val filterString = constraint.toString().toLowerCase().trim { it <= ' ' }
                for (item in institucionesFull!!) {
                    if (item.nombre!!.toLowerCase().contains(filterString)) {
                        filterList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filterList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            instituciones.clear()
            instituciones.addAll(results.values as List<Institucion>)
            notifyDataSetChanged()
        }
    }

    fun setInstitucionesFull(instituciones: List<Institucion>?) {
        institucionesFull = ArrayList(instituciones)
    }

    // ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Aca es donde se cargan las datos reales
        fun bind(institucion: Institucion, listener: OnItemClickListener) {
           itemView.textViewOngName.text = institucion.nombre
            Glide.with(activity).load(institucion.logo_url).circleCrop().placeholder(R.drawable.oval_place_holder_dark).into(itemView.imageViewOngLogo)
            if (!institucion.isEspecial) {
                itemView.textViewOngDireccion.text = institucion.direccion
                itemView.ongContainer.background = itemView.resources.getDrawable(R.drawable.background_list_item)

                // si no hay dirección ocultamos el icono place
                if (TextUtils.isEmpty(institucion.direccion)) {
                    itemView.imageViewPlace.visibility = View.GONE
                }
                if (institucion.distancia > 0 && institucion.distancia != BaseActivity.NO_DISTANCIA) {
                    itemView.imageViewDistancia.visibility = View.VISIBLE
                    itemView.textViewDistance.visibility = View.VISIBLE
                    val df = DecimalFormat("#.##")
                    df.roundingMode = RoundingMode.CEILING

                    // Metros o kilometros
                    if (institucion.distancia < 1000) {
                        itemView.textViewDistance.text = Math.round(institucion.distancia).toString() + " m"
                    } else {
                        itemView.textViewDistance.text = df.format(institucion.distancia / 1000.toDouble()) + " km"
                    }
                } else {
                    itemView.imageViewDistancia.visibility = View.INVISIBLE
                    itemView.textViewDistance.visibility = View.INVISIBLE
                }
            } else {
                // Resaltamos la opción de sugerir una nueva ONG
                itemView.imageViewPlace.visibility = View.GONE
                itemView.imageViewDistancia.visibility = View.GONE
                itemView.textViewOngDireccion.setText(R.string.suggest_ong)
                itemView.ongContainer.setBackgroundColor(itemView.resources.getColor(R.color.colorAcentlight))
            }
            // Añadimos el onClick
            itemView.setOnClickListener { listener.onItemClick(institucion, adapterPosition) }
        }


    }

    // Interfaz que define el onClick del adapter
    interface OnItemClickListener {
        fun onItemClick(institucion: Institucion?, posicion: Int)
    }

    init {
        this.instituciones = instituciones
        this.layout = layout
        this.activity = activity
        this.listener = listener
    }
}