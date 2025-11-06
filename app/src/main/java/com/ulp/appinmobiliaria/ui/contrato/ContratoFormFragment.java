package com.ulp.appinmobiliaria.ui.contrato;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentContratoFormBinding;
import com.ulp.appinmobiliaria.databinding.FragmentInmuebleFormBinding;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.InquilinoModel;
import com.ulp.appinmobiliaria.model.PagoDetalleDTO;
import com.ulp.appinmobiliaria.request.ApiClient;
import com.ulp.appinmobiliaria.ui.inmueble.InmuebleFormViewModel;

public class ContratoFormFragment extends Fragment {
    private FragmentContratoFormBinding binding;
    private ContratoFormViewModel viewModel;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Intent intent;

    public static ContratoFormFragment newInstance() {
        return new ContratoFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ContratoFormViewModel.class);
        binding = FragmentContratoFormBinding.inflate(inflater, container, false);

        configurarObservers();
        configurarEventos();

        viewModel.obtenerContratoActual(getArguments());
        return binding.getRoot();
    }

    private void configurarObservers(){
        viewModel.getmContrato().observe(getViewLifecycleOwner(), c -> {
            llenarCampos();

            if(c != null && c.getInmueble().getImagen() != null){
                String urlImagen = ApiClient.URLBASE + c.getInmueble().getImagen().replace("\\", "/");
                Glide.with(this)
                        .load(urlImagen)
                        .placeholder(R.drawable.ic_home_placeholder)
                        //.error(R.drawable.ic_home_placeholder)
                        .into(binding.ivInmueble);
            }else{
                Glide.with(this)
                        .load(R.drawable.ic_home_placeholder)
                        .into(binding.ivInmueble);
            }
        });
    }

    private void configurarEventos(){
        binding.btnVerPagos.setOnClickListener( c -> {
            PagoDetalleDTO pagoDetalleDTO = new PagoDetalleDTO();
            ContratoModel contratoActual = viewModel.getmContrato().getValue();

            pagoDetalleDTO.setIdContrato(contratoActual.getIdContrato());
            pagoDetalleDTO.setDireccionInmueble(contratoActual.getInmueble().getDireccion());
            pagoDetalleDTO.setMontoContrato(contratoActual.getMontoAlquiler());

            Bundle args = new Bundle();
            args.putSerializable("pagoDetalleDTO", pagoDetalleDTO); // Serializable para enviar Obj
            Navigation.findNavController(c).navigate(R.id.action_detalleContratos_to_DetallePagos, args);
        });

        binding.btnVerInquilino.setOnClickListener( inquilino -> {
            InquilinoModel inquilinoActual = new InquilinoModel();
            inquilinoActual = viewModel.getmContrato().getValue().getInquilino();

            Bundle args = new Bundle();
            args.putSerializable("inquilino", inquilinoActual);
            Navigation.findNavController(inquilino).navigate(R.id.action_detalleContratos_to_DetalleInquilino, args);
        });

    }

    private void llenarCampos(){
        String direccion = viewModel.getDireccion();
        String tipo = viewModel.getTipo();
        String fechaInicio = viewModel.getFechaInicio();
        String fechaFin = viewModel.getFechaFin();
        String monto = viewModel.getMontoAlquiler();
        String estado = viewModel.getEstadoTexto();
        String inquilinoResumen = viewModel.getInquilinoResumen();

        binding.tvDireccionHead.setText(direccion);
        binding.tvTipo.setText(tipo);
        binding.tvFechaInicio.setText(fechaInicio);
        binding.tvFechaFin.setText(fechaFin);
        binding.tvMonto.setText(monto);
        binding.tvEstado.setText(estado);
        binding.tvInquilinoResumen.setText(inquilinoResumen);

    }

}