package com.ulp.appinmobiliaria.ui.perfil;

import androidx.core.content.ContextCompat;
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
import com.ulp.appinmobiliaria.databinding.FragmentPerfilBinding;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.PropietarioModel;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;
    private PerfilViewModel viewModel;

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(PerfilViewModel.class);
        binding = FragmentPerfilBinding.inflate(inflater, container, false);

        configurarObservers();
        configurarEventos();
        viewModel.obtenerPerfil();

        return binding.getRoot();
    }


    private void configurarObservers() {
        viewModel.getUIState().observe(getViewLifecycleOwner(), uiState -> {
            actualizarUI(uiState);
        });

        viewModel.getmPropietario().observe(getViewLifecycleOwner(), propietario -> {
            llenarCampos();
        });

        // Observer para éxito
        /*
        viewModel.getMActualizacionExitosa().observe(getViewLifecycleOwner(), exitoso -> {
            Toast.makeText(getContext(), "✅ Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
        });
        */
    }

    private void configurarEventos() {
        binding.btnEditarPerfil.setOnClickListener(v -> {
            String dni = binding.etDni.getText().toString();
            String nombre = binding.etNombre.getText().toString();
            String apellido = binding.etApellido.getText().toString();
            String email = binding.etEmail.getText().toString();
            String telefono = binding.etTelefono.getText().toString();

            // ViewModel maneja todo
            viewModel.manejarAccionBotonPrincipal(dni, nombre, apellido, email, telefono);
        });

        // Botón cancelar
        binding.btnCancelar.setOnClickListener(v -> {
            viewModel.cancelarEdicion();
        });

        //Redirecciona a frag para cambiar pass
        binding.btnCambiarContrasena.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_perfilFragment_to_cambiarContrasenaFragment);
        });
    }

    /** Manejar IU principal*/
    private void actualizarUI(UIStateHelper.FormUIState uiState) {
        configurarCampos(uiState.mostrarCamposEditables);
        configurarBotonPrincipal(uiState);
        configurarBotonSecundario(uiState);
        configurarBotonCancelar(uiState);
        configurarCarga(uiState.cargando);
        configurarMensaje(uiState);
        llenarCampos();
    }

    private void configurarCampos(boolean modoEdicion) {
        if (modoEdicion) {
            mostrarCamposEditables();
        } else {
            mostrarCamposVisualizacion();
        }
    }

    private void configurarBotonPrincipal(UIStateHelper.FormUIState uiState) {
        binding.btnEditarPerfil.setText(uiState.textoBoton);
        binding.btnEditarPerfil.setEnabled(uiState.botonHabilitado);

        binding.btnEditarPerfil.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), uiState.iconoBoton),
                null, null, null
        );
    }

    private void configurarBotonSecundario(UIStateHelper.FormUIState uiState) {
        binding.btnCambiarContrasena.setVisibility(
                uiState.mostrarBotonSecundario ? View.VISIBLE : View.GONE
        );
        binding.btnCambiarContrasena.setEnabled(uiState.habilitarBotonSecundario);
    }

    private void configurarBotonCancelar(UIStateHelper.FormUIState uiState) {
        binding.btnCancelar.setVisibility(
                uiState.mostrarBotonCancelar ? View.VISIBLE : View.GONE
        );
    }

    private void configurarCarga(boolean cargando) {
        // ProgressBar
        if (cargando) {
            // Mostrar overlay completo
            binding.loadingOverlay.setVisibility(View.VISIBLE);
        } else {
            // Ocultar overlay
            binding.loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void configurarMensaje(UIStateHelper.FormUIState uiState) {
        if (uiState.mostrarMensaje && uiState.mensaje != null && !uiState.mensaje.isEmpty()) {
            Toast.makeText(getContext(), uiState.mensaje, Toast.LENGTH_LONG).show();
        }
    }

    // Llenar campos usando getters seguros
    private void llenarCampos() {
        // TextViews (modo vista)
        //binding.tvCodigo.setText(viewModel.getCodigoPropietario());
        binding.tvDni.setText(viewModel.getDni());
        binding.tvNombre.setText(viewModel.getNombre());
        binding.tvApellido.setText(viewModel.getApellido());
        binding.tvEmail.setText(viewModel.getEmail());
        binding.tvTelefono.setText(viewModel.getTelefono());

        // EditTexts (modo edición)
        binding.etDni.setText(viewModel.getDni());
        binding.etNombre.setText(viewModel.getNombre());
        binding.etApellido.setText(viewModel.getApellido());
        binding.etEmail.setText(viewModel.getEmail());
        binding.etTelefono.setText(viewModel.getTelefono());
    }

    private void mostrarCamposEditables() {
        // Ocultar TextViews
        binding.tvDni.setVisibility(View.GONE);
        binding.tvNombre.setVisibility(View.GONE);
        binding.tvApellido.setVisibility(View.GONE);
        binding.tvEmail.setVisibility(View.GONE);
        binding.tvTelefono.setVisibility(View.GONE);

        // Mostrar EditTexts
        binding.etDni.setVisibility(View.VISIBLE);
        binding.etNombre.setVisibility(View.VISIBLE);
        binding.etApellido.setVisibility(View.VISIBLE);
        binding.etEmail.setVisibility(View.VISIBLE);
        binding.etTelefono.setVisibility(View.VISIBLE);
    }

    private void mostrarCamposVisualizacion() {
        // Mostrar TextViews
        binding.tvDni.setVisibility(View.VISIBLE);
        binding.tvNombre.setVisibility(View.VISIBLE);
        binding.tvApellido.setVisibility(View.VISIBLE);
        binding.tvEmail.setVisibility(View.VISIBLE);
        binding.tvTelefono.setVisibility(View.VISIBLE);

        // Ocultar EditTexts
        binding.etDni.setVisibility(View.GONE);
        binding.etNombre.setVisibility(View.GONE);
        binding.etApellido.setVisibility(View.GONE);
        binding.etEmail.setVisibility(View.GONE);
        binding.etTelefono.setVisibility(View.GONE);

        limpiarErrores();
    }

    private void limpiarErrores() {
        binding.etDni.setError(null);
        binding.etNombre.setError(null);
        binding.etApellido.setError(null);
        binding.etEmail.setError(null);
        binding.etTelefono.setError(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}