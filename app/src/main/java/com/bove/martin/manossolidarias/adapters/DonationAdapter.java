package com.bove.martin.manossolidarias.adapters;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.model.Donacion;
import com.bumptech.glide.Glide;

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

            Glide.with(activity).
                    load(donacion.getIcon_url()).
                    fitCenter().
                    placeholder(R.drawable.ic_place_holder).
                    into(this.imageViewIcon);

            // Destacamos el item de agregar donación
            if(donacion.getEspecial()) {
                ImageView imageViewBack = itemView.findViewById(R.id.viewBackground);
                imageViewBack.setImageDrawable(itemView.getResources().getDrawable(R.drawable.button_donation_new));
            }

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
