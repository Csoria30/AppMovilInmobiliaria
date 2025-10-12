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
        // Observer para datos del propietario
        viewModel.getmPropietario().observe(getViewLifecycleOwner(), propietario -> {
            actualizarUI(propietario);
        });

        // Observer para modo de edición
        viewModel.getMModoEdicion().observe(getViewLifecycleOwner(), modoEdicion -> {
            actualizarModoUI(modoEdicion);
        });

        // Observer para estado de carga
        viewModel.getMCargando().observe(getViewLifecycleOwner(), cargando -> {
            binding.progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
            binding.btnEditarPerfil.setEnabled(!cargando);
        });

        // Observer para errores de validación
        viewModel.getMErrorValidacion().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                mostrarErrorValidacion(error);
            } else {
                limpiarErrores();
            }
        });

        // Observer para mensajes
        viewModel.getMMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarEventos() {
        binding.btnEditarPerfil.setOnClickListener(v -> {
            Boolean modoEdicion = viewModel.getMModoEdicion().getValue();
            if (modoEdicion != null && modoEdicion) {
                // Está en modo edición - guardar cambios
                String dni = binding.etDni.getText().toString();
                String nombre = binding.etNombre.getText().toString();
                String apellido = binding.etApellido.getText().toString();
                String email = binding.etEmail.getText().toString();
                String telefono = binding.etTelefono.getText().toString();

                viewModel.guardarCambios(dni, nombre, apellido, email, telefono);
            } else {
                // Está en modo vista - cambiar a edición
                viewModel.cambiarModoEdicion();
            }
        });

        //Redirecciona a frag para cambiar pass
        binding.btnCambiarContrasena.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_perfilFragment_to_cambiarContrasenaFragment);
        });
    }

    private void actualizarUI(PropietarioModel propietario) {
        binding.tvCodigo.setText(String.valueOf(propietario.getIdPropietario()));
        binding.tvDni.setText(propietario.getDni());
        binding.tvNombre.setText(propietario.getNombre());
        binding.tvApellido.setText(propietario.getApellido());
        binding.tvEmail.setText(propietario.getEmail());
        binding.tvTelefono.setText(propietario.getTelefono());

        // Actualizar también los EditText si están visibles
        binding.etDni.setText(propietario.getDni());
        binding.etNombre.setText(propietario.getNombre());
        binding.etApellido.setText(propietario.getApellido());
        binding.etEmail.setText(propietario.getEmail());
        binding.etTelefono.setText(propietario.getTelefono());
    }

    private void actualizarModoUI(boolean modoEdicion) {
        if (modoEdicion) {
            // Cambiar a modo edición
            mostrarCamposEditables();
            cambiarBotonAGuardar();
        } else {
            // Cambiar a modo vista
            mostrarCamposVisualizacion();
            cambiarBotonAEditar();
        }
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
    }

    private void cambiarBotonAGuardar() {
        binding.btnEditarPerfil.setText("Guardar Cambios");
        binding.btnEditarPerfil.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_save), null, null, null);
    }

    private void cambiarBotonAEditar() {
        binding.btnEditarPerfil.setText("Editar Perfil");
        binding.btnEditarPerfil.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), R.drawable.ic_edit), null, null, null);
    }

    private void mostrarErrorValidacion(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
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