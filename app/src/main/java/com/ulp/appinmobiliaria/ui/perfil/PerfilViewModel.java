package com.ulp.appinmobiliaria.ui.perfil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.ValidationHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.helpers.ErrorHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper.FormUIState;
import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private MutableLiveData<PropietarioModel> mPropietario = new MutableLiveData<>(new PropietarioModel());
    private MutableLiveData<Boolean> mActualizacionExitosa = new MutableLiveData<>();
    private MutableLiveData<FormUIState> mUIState = new MutableLiveData<>();

    private PropietarioModel propietarioActual;
    private boolean modoEdicionActual = false;
    private final Context context;

    // ===== CONSTRUCTOR =====
    public PerfilViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
        mActualizacionExitosa.setValue(false);
        actualizarUIState();
    }

    // ===== GETTERS PARA LIVEDATA =====
    public LiveData<PropietarioModel> getmPropietario() {
        return mPropietario;
    }

    public LiveData<Boolean> getMActualizacionExitosa() {
        return mActualizacionExitosa;
    }

    public LiveData<FormUIState> getUIState() {
        return mUIState;
    }

    /**  ===== METODOS PÚBLICOS PRINCIPALES ===== */

    public void obtenerPerfil() {
        //progessBar cargando perfil
        mUIState.setValue(UIStateHelper.PerfilUIStates.cargandoPerfil());

        //Obteniedo informacion del Token
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

        if (!validacion.esValido) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.error(validacion.mensajeError));
            return;
        }

        //Intancia de API
        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.obtenerPerfil(validacion.tokenBearer);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    propietarioActual = response.body();
                    mPropietario.setValue(propietarioActual);
                    mUIState.setValue(UIStateHelper.PerfilUIStates.modoVista(false));
                } else {
                    String mensaje = ErrorHelper.obtenerMensajeError(response.code());
                    mUIState.setValue(UIStateHelper.PerfilUIStates.error(mensaje));
                    Log.e("PerfilViewModel", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                String mensaje = ErrorHelper.obtenerMensajeConexion(t);
                mUIState.setValue(UIStateHelper.PerfilUIStates.error(mensaje));
                Log.e("PerfilViewModel", "Error de conexión: " + t.getMessage());
            }
        });
    } //.ObtenerPerfil

    /** Si esta en modo edicion, guarda cambios | si no pasa a modo edicion */
    public void manejarAccionBotonPrincipal(PropietarioModel propietario) {
        if (modoEdicionActual) {
            guardarCambios(propietario);
        } else {
            cambiarModoEdicion();
        }
    }

    public void cambiarModoEdicion() {
        modoEdicionActual = !modoEdicionActual;
        actualizarUIState();
        Log.d("PerfilViewModel", modoEdicionActual ? "Activando modo edición" : "Activando modo vista");
    }

    public void cancelarEdicion() {
        if (modoEdicionActual) {
            modoEdicionActual = false;
            actualizarUIState();
            Log.d("PerfilViewModel", "Cancelando edición");
        }
    }

    public void guardarCambios(PropietarioModel propietario) {
        if (!validarCamposConHelpers(propietario)) {
            return;
        }
        actualizarPerfil(propietario);
    }

    /** ===== GETTERS PARA DATOS DEL PROPIETARIO =====
     *  Nota: Evitamos retornos nulos
     * */

     public String getCodigoPropietario() {
        return propietarioActual != null ? String.valueOf(propietarioActual.getIdPropietario()) : "";
    }

    public String getDni() {
        return propietarioActual != null ? propietarioActual.getDni() : "";
    }

    public String getNombre() {
        return propietarioActual != null ? propietarioActual.getNombre() : "";
    }

    public String getApellido() {
        return propietarioActual != null ? propietarioActual.getApellido() : "";
    }

    public String getEmail() {
        return propietarioActual != null ? propietarioActual.getEmail() : "";
    }

    public String getTelefono() {
        return propietarioActual != null ? propietarioActual.getTelefono() : "";
    }

    /** =====  VALIDACIONES ===== */
    private boolean validarCamposConHelpers(PropietarioModel propietario) {
        // Helper Validacion: nombre
        if (!ValidationHelper.esCampoValido(propietario.getNombre())) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El nombre es obligatorio", "nombre"));
            return false;
        }

        if (propietario.getNombre().trim().length() < 2) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El nombre debe tener al menos 2 caracteres", "nombre"));
            return false;
        }

        // Helper Validacion: apellido
        if (!ValidationHelper.esCampoValido(propietario.getApellido())) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El apellido es obligatorio", "apellido"));
            return false;
        }
        if (propietario.getApellido().trim().length() < 2) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El apellido debe tener al menos 2 caracteres", "apellido"));
            return false;
        }

        // Helper Validacion: Dni
        if (!ValidationHelper.esDniValido(propietario.getDni())) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El DNI debe tener entre 7 y 8 dígitos", "dni"));
            return false;
        }

        // Helper Validacion: Email
        if (!ValidationHelper.esEmailValido(propietario.getEmail())) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El formato del email no es válido", "email"));
            return false;
        }

        // Helper Validacion: telefono
        if (!ValidationHelper.esTelefonoValido(propietario.getTelefono())) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El teléfono debe tener entre 8 y 15 dígitos", "telefono"));
            return false;
        }

        // Validacion OK - volver al modo edición normal
        actualizarUIState();
        return true;
    }

    // ===== MÉTODOS PRIVADOS - UTILIDADES =====
    private void actualizarUIState() {
        if (modoEdicionActual) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.modoEdicion(false));
        } else {
            mUIState.setValue(UIStateHelper.PerfilUIStates.modoVista(false));
        }
    }

    private PropietarioModel crearPropietarioActualizado(PropietarioModel nuevosDatos) {
        if (propietarioActual == null) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("Error: No se pudo obtener los datos actuales del propietario", "general"));
            return null;
        }

        PropietarioModel propietarioActualizado = new PropietarioModel();
        propietarioActualizado.setIdPropietario(propietarioActual.getIdPropietario());
        propietarioActualizado.setDni(nuevosDatos.getDni().trim());
        propietarioActualizado.setNombre(nuevosDatos.getNombre().trim());
        propietarioActualizado.setApellido(nuevosDatos.getApellido().trim());
        propietarioActualizado.setEmail(nuevosDatos.getEmail().trim());
        propietarioActualizado.setTelefono(nuevosDatos.getTelefono().trim());
        propietarioActualizado.setClave(null); // Para evitar doble hash

        return propietarioActualizado;
    }

    /**  ===== METODOS PRIVADOS  ===== */
    private void actualizarPerfil(PropietarioModel propietario) {
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(getApplication());

        if (!validacion.esValido) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.error(validacion.mensajeError));
            return;
        }

        if (propietario == null) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion(
                    "Error: Datos del propietario inválidos", "propietario"
            ));
            return;
        }

        mUIState.setValue(UIStateHelper.PerfilUIStates.guardandoCambios());

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.actualizarPerfil(validacion.tokenBearer, propietario);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    propietarioActual = response.body();
                    mPropietario.setValue(propietarioActual);
                    mActualizacionExitosa.setValue(true);

                    // Cambiar a modo vista con éxito
                    modoEdicionActual = false;
                    mUIState.setValue(UIStateHelper.PerfilUIStates.exitoGuardado());
                } else {
                    Log.e("PerfilViewModel", "Error al actualizar: " + response.code());
                    String mensaje = ErrorHelper.obtenerMensajeError(response.code());
                    mUIState.setValue(UIStateHelper.PerfilUIStates.error(mensaje));
                    mActualizacionExitosa.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                String mensaje = ErrorHelper.obtenerMensajeConexion(t);
                mUIState.setValue(UIStateHelper.PerfilUIStates.error(mensaje));
                mActualizacionExitosa.setValue(false);
                Log.e("PerfilViewModel", "Error de conexión: " + t.getMessage());
            }
        });
    }

    public void limpiarMensajeExito() {
        mActualizacionExitosa.setValue(false);
    }
}