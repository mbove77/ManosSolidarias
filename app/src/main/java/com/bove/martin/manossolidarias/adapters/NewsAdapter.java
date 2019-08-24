package com.bove.martin.manossolidarias.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.model.Noticia;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<Noticia> noticias;
    private int layout;
    private OnItemClickListener listener;
    private Activity activity;

    public NewsAdapter(List<Noticia> noticias, int layout, Activity activity, OnItemClickListener listener) {
        this.noticias = noticias;
        this.layout = layout;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflamos la vista
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(noticias.get(position), listener);
    }

    @Override
    public int getItemCount() { return noticias.size();}

    // ViewHolder
    public  class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView textViewTitulo;
        private ImageView imageViewNews;
        private TextView textViewDesc;

        // Consturctor
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewTitulo = itemView.findViewById(R.id.textViewNewsTitulo);
            this.imageViewNews = itemView.findViewById(R.id.imageViewNews);
            this.textViewDesc = itemView.findViewById(R.id.textViewNewsDesc);
        }

        // Aca es donde se cargan las datos reales
        public void bind(final Noticia noticia, final OnItemClickListener listener) {
            this.textViewTitulo.setText(noticia.getTitulo());
            Picasso.with(itemView.getContext()).load(noticia.getFoto_url()).fit().placeholder(R.drawable.placeholder).into(this.imageViewNews);
            this.textViewDesc.setText(noticia.getDesc());


            // Añadimos el onClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(noticia, getAdapterPosition());
                }
            });
        }
    }

    // Interfaz que define el onClick del adapter
    public interface OnItemClickListener {
        void onItemClick(Noticia noticia, int posicion);
    }
}
