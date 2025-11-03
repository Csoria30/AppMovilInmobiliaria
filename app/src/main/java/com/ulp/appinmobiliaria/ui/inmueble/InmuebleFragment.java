package com.ulp.appinmobiliaria.ui.inmueble;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentInmuebleBinding;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.InmuebleModel;

import java.util.ArrayList;
import java.util.List;

public class InmuebleFragment extends Fragment {
    private FragmentInmuebleBinding binding;
    private InmuebleViewModel viewModel;

    public static InmuebleFragment newInstance() {
        return new InmuebleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(InmuebleViewModel.class);
        binding = FragmentInmuebleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        configurarObservers();
        configurarEventos();

        viewModel.cargarInmuebles();
        return root;
    }

    private void configurarObservers(){
        //Observer
        viewModel.getListaInmuebles().observe(getViewLifecycleOwner(), new Observer<List<InmuebleModel>>() {
            @Override
            public void onChanged(List<InmuebleModel> inmuebleModels) {
                ListaInmueblesAdapter listaInmueblesAdapter = new ListaInmueblesAdapter(
                        (ArrayList<InmuebleModel>) inmuebleModels,
                        getContext(),
                        getLayoutInflater()
                );
                GridLayoutManager gridLayoutManager = new GridLayoutManager(
                        getContext(),
                        1,
                        GridLayoutManager.VERTICAL,
                        false
                );

                binding.rvInmuebles.setLayoutManager(gridLayoutManager);
                binding.rvInmuebles.setAdapter(listaInmueblesAdapter);
            }
        });

        //Actualizar UI
        viewModel.getmUIStateForm().observe(getViewLifecycleOwner(), uiState -> {
            actualizarUI(uiState);
        });
    }

    private void configurarEventos(){
        // Configura el click del botón flotante
        binding.fab.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_inmuebleFragment_to_informacionInmueble);
        });
    }

    private void actualizarUI(UIStateHelper.FormUIState uiState){
        configurarCarga(uiState.cargando, uiState.textoBoton);
    }

    private void configurarCarga(boolean cargando, String texto) {
        // ProgressBar
        if (cargando) {
            // Mostrar overlay completo
            binding.loadingOverlay.setVisibility(View.VISIBLE);
            binding.tvLoadingText.setText("Cargando Inmuebles");
        } else {
            // Ocultar overlay
            binding.loadingOverlay.setVisibility(View.GONE);
        }
    }


    private void mostrarErrorEnCampo(String campo, String mensaje) {
        switch (campo) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}