package com.bove.martin.manossolidarias.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.utils.inflate
import com.bove.martin.manossolidarias.activities.utils.loadFromUrl

import com.bove.martin.manossolidarias.domain.model.App
import kotlinx.android.synthetic.main.apps_item.view.*

/**
 * Created by Martín Bove on 03/11/2019.
 * E-mail: mbove77@gmail.com
 */

class OtherAppsAdapter (private val apps: List<App>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OtherAppsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.apps_item))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(apps[position], listener)

    override fun getItemCount(): Int = apps.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(app: App, listener: OnItemClickListener) = with(itemView) {
            textViewAppsDesc.text = app.desc
            imageViewApps.loadFromUrl(app.imagen)

            // Añadimos el onClick
            setOnClickListener { listener.onAppItemClick(app, adapterPosition) }
        }
    }

    // Interfaz que define el onClick del adapter
    interface OnItemClickListener {
        fun onAppItemClick(app: App, posicion: Int)
    }
}