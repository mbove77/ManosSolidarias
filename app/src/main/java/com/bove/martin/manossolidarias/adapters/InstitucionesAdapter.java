package com.bove.martin.manossolidarias.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.model.Donacion;
import com.bove.martin.manossolidarias.model.Institucion;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
public class InstitucionesAdapter extends RecyclerView.Adapter<InstitucionesAdapter.ViewHolder> {
    private List<Institucion> instituciones;
    private int layout;
    private OnItemClickListener listener;
    private Activity activity;

    public InstitucionesAdapter(List<Institucion> instituciones, int layout, Activity activity, OnItemClickListener listener) {
        this.instituciones = instituciones;
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
        holder.bind(instituciones.get(position), listener);
    }

    @Override
    public int getItemCount() { return instituciones.size();}

    // ViewHolder
    public  class ViewHolder extends RecyclerView.ViewHolder  {
        private ImageView imageViewLogo;
        private TextView textViewName;
        private TextView textViewDire;
        private TextView textViewDistancia;
        private ImageView imageViewDistancia;

        // Consturctor
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.textViewOngName);
            this.imageViewLogo = itemView.findViewById(R.id.imageViewOngLogo);
            this.textViewDire = itemView.findViewById(R.id.textViewOngDireccion);
            this.textViewDistancia = itemView.findViewById(R.id.textViewDistance);
            this.imageViewDistancia = itemView.findViewById(R.id.imageViewDistancia);
        }

        // Aca es donde se cargan las datos reales
        public void bind(final Institucion institucion, final OnItemClickListener listener) {
            this.textViewName.setText(institucion.getNombre());
            Picasso.get().load(institucion.getLogo_url()).fit().into(this.imageViewLogo);
            textViewDire.setText(institucion.getDireccion());

            if(institucion.getDistancia() > 0) {
                imageViewDistancia.setVisibility(View.VISIBLE);
                textViewDistancia.setVisibility(View.VISIBLE);

                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);

                // Metros o kilometros
                if(institucion.getDistancia() < 1000) {
                    textViewDistancia.setText(Math.round(institucion.getDistancia()) + " m");
                } else {
                    textViewDistancia.setText(df.format(institucion.getDistancia() / 1000) + " km");
                }
            } else {
                imageViewDistancia.setVisibility(View.INVISIBLE);
                textViewDistancia.setVisibility(View.INVISIBLE);
            }

            // Añadimos el onClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(institucion, getAdapterPosition());
                }
            });

        }
    }

    // Interfaz que define el onClick del adapter
    public interface OnItemClickListener {
        void onItemClick(Institucion institucion, int posicion);
    }
}
