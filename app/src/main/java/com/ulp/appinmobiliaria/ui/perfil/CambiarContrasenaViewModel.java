package com.ulp.appinmobiliaria.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarContrasenaViewModel extends AndroidViewModel {
    private final MutableLiveData<UIStateHelper.FormUIState> uiState = new MutableLiveData<>();
    private Context context;
    public CambiarContrasenaViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    /** === Getters LiveData === */
    public LiveData<UIStateHelper.FormUIState> getUiState() {
        return uiState;
    }

    /** === Logica === */
    public void cambiarContrasena(String actual, String nueva, String confirmar) {
        // Validaciones
        if (actual == null || actual.trim().isEmpty()) {
            uiState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La contraseña actual es obligatoria", "actual"));
            return;
        }
        if (nueva == null || nueva.trim().isEmpty()) {
            uiState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La nueva contraseña es obligatoria", "nueva"));
            return;
        }
        if (confirmar == null || confirmar.trim().isEmpty()) {
            uiState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("Debe confirmar la nueva contraseña", "confirmar"));
            return;
        }
        if (!nueva.equals(confirmar)) {
            uiState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("Las contraseñas no coinciden", ""));
            return;
        }
        if (nueva.length() < 6) {
            uiState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La nueva contraseña debe tener al menos 6 caracteres", "nueva"));
            return;
        }

        // Estado de carga
        uiState.setValue(UIStateHelper.PerfilUIStates.guardandoPerfil());

        // Obtener token
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

        if (!validacion.esValido) {
            uiState.setValue(UIStateHelper.PerfilUIStates.error(validacion.mensajeError));
            return;
        }

        // Llamada a la API
        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<Void> call = api.cambiarContrasena(validacion.tokenBearer, actual, nueva);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    uiState.postValue(UIStateHelper.PerfilUIStates.guardandoPerfil());
                } else {
                    uiState.postValue(UIStateHelper.PerfilUIStates.error("Error al cambiar la contraseña"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                uiState.postValue(UIStateHelper.PerfilUIStates.error("Error de conexión: " + t.getMessage()));
            }
        });
    }

    /** === MENSAJE DE ERRORES ===  */


    public void limpiarEstado() {
        //uiState.setValue(UIStateHelper.PerfilUIStates.err
    }

}