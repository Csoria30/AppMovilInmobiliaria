package com.ulp.appinmobiliaria.ui.logout;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;

public class LogoutViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mLogoutExitoso = new MutableLiveData<>();
    private MutableLiveData<String> mMensaje = new MutableLiveData<>();
    private Context context;

    public LogoutViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    public LiveData<Boolean> getLogoutExitoso() {
        return mLogoutExitoso;
    }

    public LiveData<String> getMensaje() {
        return mMensaje;
    }

    public void realizarLogout() {
        try {
            // ✅ NUEVO: Usar TokenHelper para verificar sesión activa
            if (!TokenHelper.tieneSesionActiva(context)) {
                mMensaje.setValue("No hay sesión activa");
                mLogoutExitoso.setValue(true);
                return;
            }

            // ✅ NUEVO: Usar TokenHelper para limpiar sesión completa
            boolean sesionLimpiada = TokenHelper.limpiarSesion(context);

            if (sesionLimpiada) {
                mMensaje.setValue("Sesión cerrada exitosamente");
                mLogoutExitoso.setValue(true);
            } else {
                mMensaje.setValue("Error al cerrar sesión. Intente nuevamente.");
                mLogoutExitoso.setValue(false);
            }

        } catch (Exception e) {
            mMensaje.setValue("Error inesperado al cerrar sesión");
            mLogoutExitoso.setValue(false);
        }
    }

    public boolean tieneTokenActivo() {
        try {
            return TokenHelper.tieneSesionActiva(context);
        } catch (Exception e) {
            return false;
        }
    }
}