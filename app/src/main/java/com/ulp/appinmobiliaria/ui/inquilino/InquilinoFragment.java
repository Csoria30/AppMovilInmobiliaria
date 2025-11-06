package com.ulp.appinmobiliaria.ui.inquilino;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentContratoFormBinding;
import com.ulp.appinmobiliaria.databinding.FragmentInquilinoBinding;
import com.ulp.appinmobiliaria.ui.contrato.ContratoFormViewModel;

public class InquilinoFragment extends Fragment {
    private FragmentInquilinoBinding binding;
    private InquilinoViewModel viewModel;

    public static InquilinoFragment newInstance() {
        return new InquilinoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(InquilinoViewModel.class);
        binding = FragmentInquilinoBinding.inflate(inflater, container, false);

        configurarObservers();
        configurarEventos();

        viewModel.obtenerInquilinoActual(getArguments());

        return binding.getRoot();
    }

    private void configurarObservers(){
        viewModel.getmInquilino().observe(getViewLifecycleOwner(), inq -> {
            binding.tvNombreInquilino.setText(viewModel.getInquilinoNombreCompleto());
            binding.tvDni.setText(viewModel.getInquilinoDni());
            binding.tvTelefono.setText(viewModel.getInquilinoTelefono());
            binding.tvEmail.setText(viewModel.getInquilinoEmail());
        });
    }

    private void configurarEventos(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}