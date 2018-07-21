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
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Martín Bove on 28/06/2018.
 * E-mail: mbove77@gmail.com
 */
public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {
    private List<Donacion> donaciones;
    private int layout;
    private OnItemClickListener listener;
    private OnLongClickListener longClick;
    private Activity activity;

    public DonationAdapter(List<Donacion> donaciones, int layout, Activity activity, OnItemClickListener listener, OnLongClickListener longListener) {
        this.donaciones = donaciones;
        this.layout = layout;
        this.activity = activity;
        this.listener = listener;
        this.longClick = longListener;
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
        holder.bind(donaciones.get(position), listener, longClick);
    }

    @Override
    public int getItemCount() { return donaciones.size();}

    // ViewHolder
    public  class ViewHolder extends RecyclerView.ViewHolder  {
        private ImageView imageViewIcon;
        private TextView textViewName;

        // Consturctor
        public ViewHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.textViewOngName);
            this.imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
        }

        // Aca es donde se cargan las datos reales
        public void bind(final Donacion donacion, final OnItemClickListener listener, final OnLongClickListener longClickListener) {
            this.textViewName.setText(donacion.getNombre());
            Picasso.with(itemView.getContext()).load(donacion.getIcon_url()).fit().into(this.imageViewIcon);

            // Añadimos el onClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(donacion, getAdapterPosition());
                }
            });
            // Añadimos el LongClick
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onLongClick(donacion, getAdapterPosition());
                    return true;
                }
            });
        }
    }

    // Interfaz que define el onClick del adapter
    public interface OnItemClickListener {
        void onItemClick(Donacion donacion, int posicion);
    }
    public interface  OnLongClickListener {
        void onLongClick(Donacion donacion, int posicion);
    }
}
