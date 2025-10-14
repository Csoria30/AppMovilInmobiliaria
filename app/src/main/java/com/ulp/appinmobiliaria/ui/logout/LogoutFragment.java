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

        configurarObservers();
        mostrarDialogoConfirmacion();

        return binding.getRoot();
    }

    private void configurarObservers() {
        // Observer para logout exitoso
        viewModel.getLogoutExitoso().observe(getViewLifecycleOwner(), exitoso -> {
            if (exitoso != null) {
                if (exitoso) {
                    // Logout exitoso - ir a login
                    irALoginActivity();
                } else {
                    // Error en logout - mostrar mensaje pero permitir salir
                    Toast.makeText(getContext(), "Hubo un problema al cerrar sesión", Toast.LENGTH_SHORT).show();
                    irALoginActivity(); // Aún así cerrar por seguridad
                }
            }
        });

        // Observer para mensajes
        viewModel.getMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Cerrar Sesión");
        builder.setMessage("¿Está seguro que desea cerrar sesión?");
        builder.setIcon(R.drawable.ic_logout2);

        // Botón Confirmar
        builder.setPositiveButton("Sí, Cerrar Sesión", (dialog, which) -> {
            dialog.dismiss();
            // ✅ Usar ViewModel para la lógica
            viewModel.realizarLogout();
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
            volverAtras();
        });

        AlertDialog alertDialog = builder.create();

        // Si toca fuera del diálogo
        alertDialog.setOnCancelListener(dialogInterface -> volverAtras());

        alertDialog.show();

        // Personalizar colores de botones
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(getResources().getColor(R.color.primary));
    }

    private void volverAtras() {
        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void irALoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }



}