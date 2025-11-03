package com.ulp.appinmobiliaria.ui.inmueble;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentInmuebleBinding;
import com.ulp.appinmobiliaria.databinding.FragmentInmuebleFormBinding;
import com.ulp.appinmobiliaria.databinding.FragmentPerfilBinding;
import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;
import com.ulp.appinmobiliaria.ui.perfil.PerfilViewModel;

import retrofit2.Call;

public class InmuebleFormFragment extends Fragment {
    private FragmentInmuebleFormBinding binding;
    private InmuebleFormViewModel viewModel;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Intent intent;


    public static InmuebleFormFragment newInstance(){
        return new InmuebleFormFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(InmuebleFormViewModel.class);
        binding = FragmentInmuebleFormBinding.inflate(inflater, container, false);

        abrirGaleria();
        configurarObservers();
        configurarEventos();

        viewModel.obtenerInmuebleActual(getArguments());
        return binding.getRoot();
    }



    private void configurarObservers(){
        viewModel.getmInmueble().observe( getViewLifecycleOwner(), i ->{
            llenarCampos();

            if(i != null && i.getImagen() != null){
                String urlImagen = ApiClient.URLBASE + i.getImagen().replace("\\", "/");
                Glide.with(this)
                        .load(urlImagen)
                        .placeholder(R.drawable.ic_home_placeholder)
                        //.error(R.drawable.ic_home_placeholder)
                        .into(binding.ivAvatar);
            }else{
                Glide.with(this)
                        .load(R.drawable.ic_home_placeholder)
                        .into(binding.ivAvatar);
            }

        });

        viewModel.getmUIState().observe(getViewLifecycleOwner(), uiState -> {
            actualizarUI(uiState);
        });

        viewModel.getmURI().observe(getViewLifecycleOwner(), uri -> {
            binding.ivAvatar.setImageURI(uri);
        });

    }
    private void configurarEventos() {
        binding.btnEditarInmueble.setOnClickListener( v ->{
            InmuebleModel inmuebleActual = viewModel.getmInmueble().getValue();

            inmuebleActual.setDireccion(binding.etDireccion.getText().toString());
            inmuebleActual.setTipo(binding.etTipo.getText().toString());

            int ambientes = Integer.parseInt(binding.etAmbientes.getText().toString());
            inmuebleActual.setAmbientes(ambientes);

            Double precio = Double.parseDouble(binding.etPrecio.getText().toString());
            inmuebleActual.setValor(precio);

            inmuebleActual.setDisponible(binding.cbDisponible.isChecked());

            viewModel.manejarAccionBotonPrincipal(inmuebleActual);
        });

        binding.btnCancelar.setOnClickListener(v -> {
            viewModel.cancelarEdicion();
        });

        binding.btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityResultLauncher.launch(intent);
            }
        });

        binding.btnGuardar.setOnClickListener( i -> {
            viewModel.cargarInmueble(
                    binding.etDireccion.getText().toString(),
                    binding.etUso.getText().toString(),
                    binding.etTipo.getText().toString(),
                    binding.etAmbientes.getText().toString(),
                    binding.etPrecio.getText().toString()
            );
        });

    }

    /** === Manejar IU principal === */
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
        binding.btnEditarInmueble.setText(uiState.textoBoton);
        binding.btnEditarInmueble.setEnabled(uiState.botonHabilitado);
        binding.btnEditarInmueble.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(getContext(), uiState.iconoBoton),
                null, null, null //Evita superposicion de draw
        );

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

    private void configurarMensaje(UIStateHelper.FormUIState uiState){
        if (uiState.mostrarMensaje && uiState.mensaje != null && !uiState.mensaje.isEmpty()) {
            if (uiState.campoError != null && !uiState.campoError.isEmpty()) {
                mostrarErrorEnCampo(uiState.campoError, uiState.mensaje);
            }
        }
    }

    private void mostrarErrorEnCampo(String campo, String mensaje) {
        switch (campo) {
            case "ambiente": binding.etAmbientes.setError(mensaje); break;
            case "tipo": binding.etTipo.setError(mensaje); break;
            case "precio": binding.etPrecio.setError(mensaje); break;
            case "direccion": binding.etDireccion.setError(mensaje); break;
        }
    }

    private void mostrarCampos(boolean modoEdicion) {
        int visibilidadEdit = modoEdicion ? View.VISIBLE : View.GONE;
        int visibilidadText = modoEdicion ? View.GONE : View.VISIBLE;


        // TextViews
        binding.tvAmbientes.setVisibility(visibilidadText);
        binding.tvTipo.setVisibility(visibilidadText);
        binding.tvUso.setVisibility(visibilidadText);
        binding.tvPrecio.setVisibility(visibilidadText);
        binding.tvDireccion.setVisibility(visibilidadText);


        // EditTexts
        binding.etAmbientes.setVisibility(visibilidadEdit);
        binding.etTipo.setVisibility(visibilidadEdit);
        binding.etUso.setVisibility(visibilidadEdit);
        binding.etPrecio.setVisibility(visibilidadEdit);
        binding.etDireccion.setVisibility(visibilidadEdit);

        // CheackBox
        binding.cbDisponible.setEnabled(modoEdicion);

        boolean esNuevoInmueble = getArguments() == null;
        binding.btnCargarImagen.setVisibility(esNuevoInmueble && modoEdicion ? View.VISIBLE : View.GONE);

        if (!modoEdicion) {
            limpiarErrores();
        }
    }

    private void llenarCampos(){
        String direccion = viewModel.getDireccion();
        String uso = viewModel.getUso();
        String tipo = viewModel.getTipo();
        String ambientes = viewModel.getAmbientes();
        String precio = viewModel.getValor();
        boolean disponible = viewModel.getDisponible();

        binding.tvDireccion.setText(direccion);
        binding.tvTipo.setText(tipo);
        binding.tvUso.setText(uso);
        binding.tvAmbientes.setText(ambientes);
        binding.tvPrecio.setText(precio);
        binding.cbDisponible.setChecked(disponible);

        binding.etDireccion.setText(direccion);
        binding.etUso.setText(uso);
        binding.etTipo.setText(tipo);
        binding.etAmbientes.setText(ambientes);
        binding.etPrecio.setText(precio);
    }


    private void limpiarErrores() {
        binding.etAmbientes.setError(null);
        binding.etTipo.setError(null);
        binding.etPrecio.setError(null);
        binding.etDireccion.setError(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void abrirGaleria(){
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                viewModel.recibirFoto(result);
            }
        });
    }

}