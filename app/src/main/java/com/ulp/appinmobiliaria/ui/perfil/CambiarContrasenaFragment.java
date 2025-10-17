package com.ulp.appinmobiliaria.ui.perfil;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentCambiarContrasenaBinding;
import com.ulp.appinmobiliaria.databinding.FragmentPerfilBinding;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;

public class CambiarContrasenaFragment extends Fragment {
    private FragmentCambiarContrasenaBinding binding;
    private CambiarContrasenaViewModel viewModel;
    private final MutableLiveData<UIStateHelper.FormUIState> uiState = new MutableLiveData<>();

    public static CambiarContrasenaFragment newInstance() {
        return new CambiarContrasenaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(CambiarContrasenaViewModel.class);
        binding = FragmentCambiarContrasenaBinding.inflate(inflater, container, false);

        configurarObservers();
        configurarEventos();
        return binding.getRoot();
    }

    private void configurarObservers() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            actualizarUI(uiState);
        });
    }

    private void actualizarUI(UIStateHelper.FormUIState uiState) {
        // Estado de carga
        binding.progressBar.setVisibility(uiState.cargando ? View.VISIBLE : View.GONE);
        binding.btnGuardarContrasena.setEnabled(!uiState.cargando);

        // Mensaje de validación o error
        if (uiState.mostrarMensaje && uiState.mensaje != null && !uiState.mensaje.isEmpty()) {
            Toast.makeText(getContext(), uiState.mensaje, Toast.LENGTH_LONG).show();
        }

        // Cambio exitoso
        if (uiState.tipoMensaje == UIStateHelper.BaseUIState.TipoMensaje.SUCCESS) {
            Navigation.findNavController(binding.getRoot()).popBackStack();
        }
    }

    private void configurarEventos() {
        binding.btnGuardarContrasena.setOnClickListener(v -> {
            String contrasenaActual = binding.etContrasenaActual.getText().toString();
            String contrasenaNueva = binding.etContrasenaNueva.getText().toString();
            String contrasenaConfirmar = binding.etContrasenaConfirmar.getText().toString();

            viewModel.cambiarContrasena(contrasenaActual, contrasenaNueva, contrasenaConfirmar);
        });

        binding.btnCancelar.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}