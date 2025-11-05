package com.ulp.appinmobiliaria.ui.contrato;

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
import androidx.recyclerview.widget.RecyclerView;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.InquilinoModel;
import com.ulp.appinmobiliaria.ui.inmueble.ListaInmueblesAdapter;

import java.util.List;

public class ListaContratosAdapter extends RecyclerView.Adapter<ListaContratosAdapter.ViewHolderContrato> {
    private List<ContratoModel> listaContratos;
    private Context context;
    private LayoutInflater layoutInflater;

    public ListaContratosAdapter(List<ContratoModel> listaContratos, Context context, LayoutInflater layoutInflater) {
        this.listaContratos = listaContratos;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ListaContratosAdapter.ViewHolderContrato onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_contrato, parent, false);
        return new ListaContratosAdapter.ViewHolderContrato(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderContrato holder, int position) {
        ContratoModel contratoActual = listaContratos.get(position);
        InquilinoModel inquilino = contratoActual.getInquilino();

        holder.tvDireccion.setText("Dirección: " + contratoActual.getInmueble().getDireccion());
        holder.tvTipo.setText("Tipo: " + contratoActual.getInmueble().getTipo());
        holder.tvFechaInicio.setText("Fecha inicio: " + contratoActual.getFechaInicio());
        holder.tvFechaFin.setText("Fecha finalización: " + contratoActual.getFechaInicio());
        holder.tvMontoAlquiler.setText("Precio: " + contratoActual.getMontoAlquiler() + "");

        String estado = "Vencido";
        if(contratoActual.isEstado())
            estado = "Vigente";

        holder.tvEstado.setText("Estado Contrato: " + estado);

        //String nombreInquilino = contratoActual.getInquilino().getApellido() + "" + contratoActual.getInquilino().getNombre();
        //holder.tvInquilino.setText(nombreInquilino);

        holder.btnMasInfoContrato.setOnClickListener( v -> {
            Bundle args = new Bundle();
            args.putSerializable("contrato", contratoActual);
        });

    }

    @Override
    public int getItemCount() {
        return listaContratos.size();
    }

    public class ViewHolderContrato extends RecyclerView.ViewHolder {
        TextView tvDireccion, tvTipo, tvFechaInicio, tvFechaFin, tvMontoAlquiler, tvEstado, tvInquilino;
        Button btnMasInfoContrato;

        public ViewHolderContrato(@NonNull View itemView) {
            super(itemView);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicio);
            tvFechaFin = itemView.findViewById(R.id.tvFechaFin);
            tvMontoAlquiler = itemView.findViewById(R.id.tvMontoAlquiler);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnMasInfoContrato = itemView.findViewById(R.id.btnMasInfoContrato);
        }
    }
}
