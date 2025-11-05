package com.ulp.appinmobiliaria.ui.contrato;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentContratoFormBinding;
import com.ulp.appinmobiliaria.databinding.FragmentInmuebleFormBinding;
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