package com.ulp.appinmobiliaria.ui.perfil;

import androidx.lifecycle.AndroidViewModel_androidKt;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        //Observers
        viewModel.getmPropietario().observe(getViewLifecycleOwner(), propietario  -> {
            actualizarUI(propietario);
        });

        viewModel.obtenerPerfil();

        return binding.getRoot();
    }

    //Cargando datos del perfil
    private void actualizarUI(PropietarioModel propietario){
        binding.tvDni.setText(propietario.getDni());
        binding.tvNombre.setText(propietario.getNombre());
        binding.tvApellido.setText(propietario.getApellido());
        binding.tvEmail.setText(propietario.getEmail());
        binding.tvTelefono.setText(propietario.getTelefono());
    }

}