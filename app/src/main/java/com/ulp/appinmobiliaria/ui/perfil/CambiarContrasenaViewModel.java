package com.ulp.appinmobiliaria.ui.perfil;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarContrasenaViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mCargando = new MutableLiveData<>();
    private MutableLiveData<String> mErrorValidacion = new MutableLiveData<>();
    private MutableLiveData<String> mMensaje = new MutableLiveData<>();
    private MutableLiveData<Boolean> mCambioExitoso = new MutableLiveData<>();
    public CambiarContrasenaViewModel(@NonNull Application application) {
        super(application);
    }

    //Getters LiveData

    public LiveData<Boolean> getMCargando() {
        return mCargando;
    }

    public LiveData<String> getMErrorValidacion() {
        return mErrorValidacion;
    }

    public LiveData<String> getMMensaje() {
        return mMensaje;
    }

    public LiveData<Boolean> getMCambioExitoso() {
        return mCambioExitoso;
    }

    //Cambiar contraseña
    public void cambiarContrasena(String contrasenaActual, String contrasenaNueva, String contrasenaConfirmar) {
        // Validar campos
        if (!validarCampos(contrasenaActual, contrasenaNueva, contrasenaConfirmar)) {
            return;
        }
        // obtener datos actuales del propietario
        obtenerPerfilYActualizarContrasena(contrasenaActual, contrasenaNueva);
    }

    private boolean validarCampos(String actual, String nueva, String confirmar) {
        if (actual == null || actual.trim().isEmpty()) {
            mErrorValidacion.setValue("La contraseña actual es obligatoria");
            return false;
        }

        if (nueva == null || nueva.trim().isEmpty()) {
            mErrorValidacion.setValue("La nueva contraseña es obligatoria");
            return false;
        }

        if (nueva.length() < 6) {
            mErrorValidacion.setValue("La nueva contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (!nueva.equals(confirmar)) {
            mErrorValidacion.setValue("Las contraseñas nuevas no coinciden");
            return false;
        }

        if (actual.equals(nueva)) {
            mErrorValidacion.setValue("La nueva contraseña debe ser diferente a la actual");
            return false;
        }

        mErrorValidacion.setValue(null);
        return true;
    }

    private void obtenerPerfilYActualizarContrasena(String contrasenaActual, String contrasenaNueva) {
        String token = TokenHelper.obtenerToken(getApplication());

        if (token == null || token.isEmpty()) {
            mMensaje.setValue("Token no encontrado. Inicie sesión nuevamente.");
            return;
        }

        mCargando.setValue(true);

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.obtenerPerfil("Bearer " + token);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PropietarioModel propietario = response.body();

                    // Verificar contraseña actual
                    if (!propietario.getClave().equals(contrasenaActual)) {
                        mCargando.setValue(false);
                        mErrorValidacion.setValue("La contraseña actual es incorrecta");
                        return;
                    }

                    // Actualizar solo la contraseña
                    actualizarSoloContrasena(propietario, contrasenaNueva);
                } else {
                    mCargando.setValue(false);
                    mMensaje.setValue("Error al obtener datos del perfil");
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                mCargando.setValue(false);
                mMensaje.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void actualizarSoloContrasena(PropietarioModel propietario, String contrasenaNueva) {
        PropietarioModel propietarioActualizado = new PropietarioModel();

        propietarioActualizado.setIdPropietario(propietario.getIdPropietario());
        propietarioActualizado.setDni(propietario.getDni());
        propietarioActualizado.setNombre(propietario.getNombre());
        propietarioActualizado.setApellido(propietario.getApellido());
        propietarioActualizado.setEmail(propietario.getEmail());
        propietarioActualizado.setTelefono(propietario.getTelefono());
        propietarioActualizado.setClave(contrasenaNueva); // Cambiar Solo el Password

        String token = TokenHelper.obtenerToken(getApplication());
        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.actualizarPerfil("Bearer " + token, propietarioActualizado);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                mCargando.setValue(false);

                if (response.isSuccessful()) {
                    Log.d("CambiarContrasenaVM", "Contraseña actualizada correctamente");
                    mMensaje.setValue("Contraseña cambiada exitosamente");
                    mCambioExitoso.setValue(true);
                } else {
                    mMensaje.setValue("Error al cambiar la contraseña: " + response.code());
                    mCambioExitoso.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                mCargando.setValue(false);
                mMensaje.setValue("Error de conexión: " + t.getMessage());
                mCambioExitoso.setValue(false);
            }
        });
    }
}