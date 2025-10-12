package com.ulp.appinmobiliaria.ui.perfil;

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

public class CambiarContrasenaFragment extends Fragment {

    private FragmentCambiarContrasenaBinding binding;
    private CambiarContrasenaViewModel viewModel;

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
        // Observer para estado de carga
        viewModel.getMCargando().observe(getViewLifecycleOwner(), cargando -> {
            binding.progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
            binding.btnGuardarContrasena.setEnabled(!cargando);
        });

        // Observer para errores de validación
        viewModel.getMErrorValidacion().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // Observer para mensajes
        viewModel.getMMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        // Observer para cambio exitoso
        viewModel.getMCambioExitoso().observe(getViewLifecycleOwner(), exitoso -> {
            if (exitoso != null && exitoso) {
                // Volver al perfil
                Navigation.findNavController(getView()).popBackStack();
            }
        });
    }

    private void configurarEventos() {
        // Botón guardar
        binding.btnGuardarContrasena.setOnClickListener(v -> {
            String contrasenaActual = binding.etContrasenaActual.getText().toString();
            String contrasenaNueva = binding.etContrasenaNueva.getText().toString();
            String contrasenaConfirmar = binding.etContrasenaConfirmar.getText().toString();

            viewModel.cambiarContrasena(contrasenaActual, contrasenaNueva, contrasenaConfirmar);
        });

        // Botón cancelar
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