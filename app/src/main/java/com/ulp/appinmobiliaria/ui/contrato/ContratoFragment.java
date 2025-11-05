package com.ulp.appinmobiliaria.ui.contrato;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentContratoBinding;
import com.ulp.appinmobiliaria.databinding.FragmentInmuebleBinding;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.ui.inmueble.InmuebleViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContratoFragment extends Fragment {
    private FragmentContratoBinding binding;
    private ContratoViewModel viewModel;

    public static ContratoFragment newInstance() {
        return new ContratoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(ContratoViewModel.class);
        binding = FragmentContratoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        configurarObservers();
        configurarEventos();

        viewModel.obtenerInmueblesConContratos();

        return root;
    }

    private void configurarObservers(){
        viewModel.getmListaContrato().observe(getViewLifecycleOwner(), new Observer<List<ContratoModel>>() {
            @Override
            public void onChanged(List<ContratoModel> contratoModels) {
                ListaContratosAdapter listaContratosAdapter = new ListaContratosAdapter(
                        (ArrayList<ContratoModel>) contratoModels,
                        getContext(),
                        getLayoutInflater()
                );

                GridLayoutManager gridLayoutManager = new GridLayoutManager(
                        getContext(),
                        1,
                        GridLayoutManager.VERTICAL,
                        false
                );

                binding.rvContratos.setLayoutManager(gridLayoutManager);
                binding.rvContratos.setAdapter(listaContratosAdapter);
            }
        });

        //Actualizar UI
        viewModel.getmUIStateForm().observe(getViewLifecycleOwner(), uiState -> {
            actualizarUI(uiState);
        });
    }

    private void configurarEventos(){

    }

    private void actualizarUI(UIStateHelper.FormUIState uiState){
        configurarCarga(uiState.cargando, uiState.textoBoton);
    }

    private void configurarCarga(boolean cargando, String texto) {
        // ProgressBar
        if (cargando) {
            // Mostrar overlay completo
            binding.loadingOverlay.setVisibility(View.VISIBLE);
            binding.tvLoadingText.setText("Cargando Contratos");
        } else {
            // Ocultar overlay
            binding.loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}