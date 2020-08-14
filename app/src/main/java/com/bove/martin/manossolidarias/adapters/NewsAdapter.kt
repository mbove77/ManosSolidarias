package com.bove.martin.manossolidarias.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.model.Noticia
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.news_item.view.*

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
class NewsAdapter(private val noticias: List<Noticia>, private val layout: Int, private val activity: Activity, private val listener: OnItemClickListener) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflamos la vista
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noticias[position], listener)
    }

    override fun getItemCount() = noticias.size

    // ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Aca es donde se cargan las datos reales
        fun bind(noticia: Noticia, listener: OnItemClickListener) {
            itemView.textViewNewsTitulo.text = noticia.titulo
            Glide.with(activity)
                    .load(noticia.foto_url)
                    .fitCenter()
                    .placeholder(R.drawable.placeholder)
                    .into(itemView.imageViewNews)
            itemView.textViewNewsDesc.text = noticia.desc

            // Añadimos el onClick
            itemView.setOnClickListener { listener.onItemClick(noticia, adapterPosition) }
        }
    }

    // Interfaz que define el onClick del adapter
    interface OnItemClickListener {
        fun onItemClick(noticia: Noticia?, posicion: Int)
    }

}