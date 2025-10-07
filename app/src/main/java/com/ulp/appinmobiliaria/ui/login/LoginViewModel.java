package com.ulp.appinmobiliaria.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Boolean> loginExitoso = new MutableLiveData<>();
    private MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private LoginRepository loginRepository = new LoginRepository();

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }
    public LiveData<Boolean> getLoginExitoso() {
        return loginExitoso;
    }

    public void login(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            mensajeError.setValue("El email es requerido");
            return;
        }

        // Validar formato de email
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            mensajeError.setValue("El formato del email no es válido");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            mensajeError.setValue("La contraseña es requerida");
            return;
        }

        boolean resultado = loginRepository.validarCredenciales(email.trim(), password);

        if (resultado) {
            loginExitoso.setValue(true);
        } else {
            mensajeError.setValue("Credenciales o correo incorrecto");
        }
    }

}
