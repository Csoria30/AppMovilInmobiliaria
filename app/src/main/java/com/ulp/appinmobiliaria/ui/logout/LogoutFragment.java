package com.ulp.appinmobiliaria.ui.logout;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.databinding.FragmentLogoutBinding;
import com.ulp.appinmobiliaria.ui.login.LoginActivity;

public class LogoutFragment extends Fragment {
    private LogoutViewModel viewModel;
    private FragmentLogoutBinding binding;

    public static LogoutFragment newInstance() {
        return new LogoutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()).create(LogoutViewModel.class);
        binding = FragmentLogoutBinding.inflate(inflater, container, false);

        configurarObservers();
        mostrarDialogoConfirmacion();

        return binding.getRoot();
    }

    private void configurarObservers() {
        // Observer para mostrar/ocultar diálogo
        viewModel.getMostrarDialogo().observe(getViewLifecycleOwner(), mostrar -> {
            if (mostrar != null && mostrar) {
                mostrarDialogoConfirmacion();
            }
        });

        // Observer para resultado del logout
        viewModel.getLogoutExitoso().observe(getViewLifecycleOwner(), exitoso -> {
            irALoginActivity();
        });

        // Observer para mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        //Segundo parametro, define thema personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);

        builder.setTitle("Cerrar Sesión");
        builder.setMessage("¿Está seguro que desea cerrar sesión?");
        builder.setIcon(R.drawable.ic_logout2);

        builder.setPositiveButton("Sí, Cerrar Sesión", (dialog, which) -> {
            dialog.dismiss();
            viewModel.realizarLogout();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
            volverAtras();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnCancelListener(dialogInterface -> volverAtras());
        alertDialog.show();

        try {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.danger, null));

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.primary, null));
        } catch (Exception e) {
            // Colores por defecto
        }

    }



    private void volverAtras() {
        try {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        } catch (Exception e) {
            irALoginActivity();
        }
    }

    private void irALoginActivity() {
        try {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            if (getActivity() != null) {
                getActivity().finish();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al redirigir al login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}