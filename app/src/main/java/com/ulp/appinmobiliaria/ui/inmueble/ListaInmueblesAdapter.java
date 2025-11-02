package com.ulp.appinmobiliaria.ui.inmueble;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import java.util.List;

public class ListaInmueblesAdapter extends RecyclerView.Adapter<ListaInmueblesAdapter.ViewHolderInmueble> {
    private List<InmuebleModel> listaInmuebles;
    private Context context;
    private LayoutInflater layoutInflater;

    public ListaInmueblesAdapter(List<InmuebleModel> listaInmuebles, Context context, LayoutInflater layoutInflater) {
        this.listaInmuebles = listaInmuebles;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ViewHolderInmueble onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_inmueble, parent, false);
        return new ViewHolderInmueble(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderInmueble holder, int position) {
        InmuebleModel inmuebleActual = listaInmuebles.get(position);

        holder.tvDireccion.setText(inmuebleActual.getDireccion());
        holder.tvTipo.setText(inmuebleActual.getTipo());
        holder.tvPrecio.setText("$ " + inmuebleActual.getValor());
        holder.tvHabitaciones.setText("Ambientes: " + inmuebleActual.getAmbientes());
        holder.cbDisponible.setChecked(inmuebleActual.getDisponible());

        String urlImagen = ApiClient.URLBASE + inmuebleActual.getImagen().replace("\\", "/");

        Glide.with(holder.ivImagen.getContext())
                .load(urlImagen)
                .placeholder(R.drawable.ic_home_placeholder)
                //.error(R.drawable.ic_home_placeholder)
                .into(holder.ivImagen);


        holder.btnInformacion.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("inmueble", inmuebleActual);
            Navigation.findNavController(v).navigate(R.id.action_inmuebleFragment_to_informacionInmueble, args);
        });
    }

    @Override
    public int getItemCount() {
        return listaInmuebles.size();
    }

    public class ViewHolderInmueble extends RecyclerView.ViewHolder{
        ImageView ivImagen;
        TextView tvDireccion, tvTipo, tvPrecio, tvHabitaciones;
        Button btnInformacion;
        CheckBox cbDisponible;
        public ViewHolderInmueble(@NonNull View itemView){
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivImagen);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvHabitaciones = itemView.findViewById(R.id.tvHabitaciones);
            btnInformacion = itemView.findViewById(R.id.btnMasInfo);
            cbDisponible = itemView.findViewById(R.id.cbDisponible);
        }

    }
}
