package com.ulp.appinmobiliaria.ui.logout;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
            // Verificar si existe token
            String token = ApiClient.leerToken(context);

            if (token == null || token.isEmpty()) {
                Log.d("LogoutViewModel", "No hay token para eliminar");
                mMensaje.setValue("No hay sesión activa");
                mLogoutExitoso.setValue(true);
                return;
            }

            // Eliminar token usando la función de ApiClient
            boolean eliminado = ApiClient.eliminarToken(context);

            if (eliminado) {
                Log.d("LogoutViewModel", "Token eliminado correctamente");
                mMensaje.setValue("Sesión cerrada correctamente");
                mLogoutExitoso.setValue(true);
            } else {
                Log.e("LogoutViewModel", "Error al eliminar token");
                mMensaje.setValue("Error al cerrar sesión");
                mLogoutExitoso.setValue(false);
            }

        } catch (Exception e) {
            Log.e("LogoutViewModel", "Excepción en logout: " + e.getMessage());
            mMensaje.setValue("Error inesperado al cerrar sesión");
            mLogoutExitoso.setValue(false);
        }
    }

    public boolean tieneTokenActivo() {
        String token = ApiClient.leerToken(context);
        return token != null && !token.isEmpty();
    }
}