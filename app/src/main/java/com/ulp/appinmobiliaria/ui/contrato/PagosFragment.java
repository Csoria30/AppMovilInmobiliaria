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
import com.ulp.appinmobiliaria.databinding.FragmentPagosBinding;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.PagoModel;

import java.util.ArrayList;
import java.util.List;

public class PagosFragment extends Fragment {
    private FragmentPagosBinding binding;
    private PagosViewModel viewModel;

    public static PagosFragment newInstance() {
        return new PagosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PagosViewModel.class);
        binding = FragmentPagosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        configurarObservers();
        configurarEventos();
        viewModel.obtenerPagosActuales(getArguments());


        return root;

    }

    private void configurarObservers(){
        viewModel.getmListaPagos().observe(getViewLifecycleOwner(), new Observer<List<PagoModel>>() {
            @Override
            public void onChanged(List<PagoModel> pagoModels) {
                ListaPagosAdapter listaPagosAdapter = new ListaPagosAdapter(
                        (ArrayList<PagoModel>) pagoModels,
                        getContext(),
                        getLayoutInflater()
                );

                GridLayoutManager gridLayoutManager = new GridLayoutManager(
                        getContext(),
                        1,
                        GridLayoutManager.VERTICAL,
                        false
                );

                binding.rvPagos.setLayoutManager(gridLayoutManager);
                binding.rvPagos.setAdapter(listaPagosAdapter);
                llenarCampos();
            }
        });

        viewModel.getmPagoDTO().observe(getViewLifecycleOwner(), dto -> {
            viewModel.obtenerPagos();
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
            binding.tvLoadingText.setText("Cargando Pagos");
        } else {
            // Ocultar overlay
            binding.loadingOverlay.setVisibility(View.GONE);
        }
    }

    public void llenarCampos(){
        binding.tvContratoResumen.setText(viewModel.getDireccionDto());
        binding.tvMontoResumen.setText(viewModel.getMontoContratoDto());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}