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

import java.util.HashMap;

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

    /** === OBSERVERS === */

    /* getViewLifecycleOwner()
    * Ddevuelve el ciclo de vida de la vista del Fragment, solo envia actualizaciones mientras la vida del frag este activo.
    * */
    private void configurarObservers() {

        viewModel.getUIState().observe(getViewLifecycleOwner(), uiState -> {
            actualizarUI(uiState);
        });

        viewModel.getmPropietario().observe(getViewLifecycleOwner(), propietario -> {
            llenarCampos();
        });
    }

    /** === EVENTOS === */
    private void configurarEventos() {
        binding.btnEditarPerfil.setOnClickListener(v -> {
            PropietarioModel propietario = new PropietarioModel();
            propietario.setDni(binding.etDni.getText().toString());
            propietario.setNombre(binding.etNombre.getText().toString());
            propietario.setApellido(binding.etApellido.getText().toString());
            propietario.setEmail(binding.etEmail.getText().toString());
            propietario.setTelefono(binding.etTelefono.getText().toString());

            viewModel.manejarAccionBotonPrincipal(propietario);
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

    /** === Manejar IU principal === */

    /*
    configurarCampos: Maneja campos vista / edicion (booelan)
    configurarBotones: Actualiza texto, iconos, visualizacion segun estado
    configurarCarga: Muestra / oculta overlay
    configurarMensaje: Maneja mje error - exito
    * */
    private void actualizarUI(UIStateHelper.FormUIState uiState) {
        configurarCampos(uiState.mostrarCamposEditables);
        configurarBotones(uiState);
        configurarCarga(uiState.cargando);
        configurarMensaje(uiState);
    }

    private void configurarCampos(boolean modoEdicion) {
        if (modoEdicion) {
            mostrarCampos(true);
        } else {
            mostrarCampos(false);
        }
    }

    private void configurarBotones(UIStateHelper.FormUIState uiState) {
        // Botón principal
        binding.btnEditarPerfil.setText(uiState.textoBoton);
        binding.btnEditarPerfil.setEnabled(uiState.botonHabilitado);
        binding.btnEditarPerfil.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), uiState.iconoBoton),
                null, null, null //Evita superposicion de draw
        );

        // Botón secundario
        binding.btnCambiarContrasena.setVisibility(
                uiState.mostrarBotonSecundario ? View.VISIBLE : View.GONE
        );
        binding.btnCambiarContrasena.setEnabled(uiState.habilitarBotonSecundario);

        // Botón cancelar
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



    /** === MENSAJE DE ERRORES === */
    private void configurarMensaje(UIStateHelper.FormUIState uiState){
        if (uiState.mostrarMensaje && uiState.mensaje != null && !uiState.mensaje.isEmpty()) {
            if (uiState.campoError != null && !uiState.campoError.isEmpty()) {
                mostrarErrorEnCampo(uiState.campoError, uiState.mensaje);
            }
        }
    }

    private void mostrarErrorEnCampo(String campo, String mensaje) {
        switch (campo) {
            case "nombre": binding.etNombre.setError(mensaje); break;
            case "apellido": binding.etApellido.setError(mensaje); break;
            case "dni": binding.etDni.setError(mensaje); break;
            case "email": binding.etEmail.setError(mensaje); break;
            case "telefono": binding.etTelefono.setError(mensaje); break;
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

    private void mostrarCampos(boolean modoEdicion) {
        int visibilidadEdit = modoEdicion ? View.VISIBLE : View.GONE;
        int visibilidadText = modoEdicion ? View.GONE : View.VISIBLE;

        // TextViews
        binding.tvDni.setVisibility(visibilidadText);
        binding.tvNombre.setVisibility(visibilidadText);
        binding.tvApellido.setVisibility(visibilidadText);
        binding.tvEmail.setVisibility(visibilidadText);
        binding.tvTelefono.setVisibility(visibilidadText);

        // EditTexts
        binding.etDni.setVisibility(visibilidadEdit);
        binding.etNombre.setVisibility(visibilidadEdit);
        binding.etApellido.setVisibility(visibilidadEdit);
        binding.etEmail.setVisibility(visibilidadEdit);
        binding.etTelefono.setVisibility(visibilidadEdit);

        if (!modoEdicion) {
            limpiarErrores();
        }
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