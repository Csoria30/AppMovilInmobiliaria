package com.ulp.appinmobiliaria.ui.ubicacion;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentUbicacionBinding;

public class UbicacionFragment extends Fragment {

    private FragmentUbicacionBinding binding;
    private UbicacionViewModel viewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(UbicacionViewModel.class);
        binding = FragmentUbicacionBinding.inflate(inflater, container, false);


        viewModel.getmMapa().observe(getViewLifecycleOwner(), mapaInmobiliaria -> {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            supportMapFragment.getMapAsync(mapaInmobiliaria);
        });



        //CargarMapa
        viewModel.cargarMapa();

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}