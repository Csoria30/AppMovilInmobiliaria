package com.ulp.appinmobiliaria.ui.inmueble;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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


    public static InmuebleFormFragment newInstance(){
        return new InmuebleFormFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(InmuebleFormViewModel.class);
        binding = FragmentInmuebleFormBinding.inflate(inflater, container, false);

        configurarObservers();
        configurarEventos();

        viewModel.obtenerInmuebleActual(getArguments());
        return binding.getRoot();
    }



    private void configurarObservers(){
        viewModel.getmInmueble().observe( getViewLifecycleOwner(), i ->{
            binding.tvDireccion.setText(i.getDireccion());
            binding.tvPrecio.setText(i.getValor() + "");
            binding.tvTipo.setText(i.getTipo());
            binding.tvAmbientes.setText(i.getAmbientes() + "");

            String urlImagen = ApiClient.URLBASE + i.getImagen().replace("\\", "/");

            Glide.with(this)
                    .load(urlImagen)
                    .placeholder(R.drawable.ic_home_placeholder)
                    //.error(R.drawable.ic_home_placeholder)
                    .into(binding.ivAvatar);
        });
    }
    private void configurarEventos() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}