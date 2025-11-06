package com.ulp.appinmobiliaria.ui.contrato;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.helpers.ValidationHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.PagoModel;

import java.util.List;

public class ListaPagosAdapter extends RecyclerView.Adapter<ListaPagosAdapter.ViewHolderPagos> {
    private List<PagoModel> listaPagos;
    private Context context;
    private LayoutInflater layoutInflater;

    public ListaPagosAdapter(List<PagoModel> listaPagos, Context context, LayoutInflater layoutInflater) {
        this.listaPagos = listaPagos;
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public ListaPagosAdapter.ViewHolderPagos onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = layoutInflater.inflate(R.layout.item_pago, parent, false);
        return new ListaPagosAdapter.ViewHolderPagos(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPagosAdapter.ViewHolderPagos holder, int position){
        PagoModel pagoActual = listaPagos.get(position);

        // Fecha formateada
        holder.tvFechaPago.setText("Fecha: " + ValidationHelper.formatearFecha(pagoActual.getFechaPago()));
        holder.tvMontoPago.setText(String.format("Monto: $ %.2f", pagoActual.getMonto()));
        holder.tvDetallePago.setText("Detalle: " + (pagoActual.getDetalle() != null ? pagoActual.getDetalle() : "-"));
        // Estado con color
        String estadoTexto = pagoActual.isEstado() ? "Registrado" : "Pendiente";
        holder.tvEstadoPago.setText("Estado: " + estadoTexto);

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("pago", pagoActual);
            Navigation.findNavController(v).navigate(R.id.action_detalleContratos_to_DetallePagos, args);
        });
    }

    @Override
    public int getItemCount() {
        return listaPagos.size();
    }

    public class ViewHolderPagos extends RecyclerView.ViewHolder{
        TextView tvFechaPago, tvMontoPago, tvDetallePago, tvEstadoPago;
        Button btnVerPago;

        public ViewHolderPagos(@NonNull View itemView) {
            super(itemView);
            tvFechaPago = itemView.findViewById(R.id.tvFechaPago);
            tvMontoPago = itemView.findViewById(R.id.tvMontoPago);
            tvDetallePago = itemView.findViewById(R.id.tvDetallePago);
            tvEstadoPago = itemView.findViewById(R.id.tvEstadoPago);
            btnVerPago = itemView.findViewById(R.id.btnVerPago);
        }
    }

}
