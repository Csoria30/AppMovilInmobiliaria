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
    private MutableLiveData<FormUIState> mUIState = new MutableLiveData<>();
    private PropietarioModel propietarioActual;
    private final Context context;

    // ===== CONSTRUCTOR =====
    public PerfilViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
        actualizarUIState();
    }

    // ===== GETTERS PARA LIVEDATA =====
    public LiveData<PropietarioModel> getmPropietario() {
        return mPropietario;
    }


    public LiveData<FormUIState> getUIState() {
        return mUIState;
    }

    //  ===== METODOS  PRINCIPALES =====

    public void obtenerPerfil() {
        //progessBar cargando perfil
        mUIState.setValue(UIStateHelper.FormUIState.cargando());

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
                    mPropietario.postValue(propietarioActual);

                    mUIState.postValue(UIStateHelper.PerfilUIStates.inicial()); // Modo lectura
                } else {
                    String mensaje = ErrorHelper.obtenerMensajeError(response.code());
                    mUIState.postValue(UIStateHelper.PerfilUIStates.error(mensaje));
                    Log.e("PerfilViewModel", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                String mensaje = ErrorHelper.obtenerMensajeConexion(t);
                mUIState.postValue(UIStateHelper.PerfilUIStates.error(mensaje));
                Log.e("PerfilViewModel", "Error de conexión: " + t.getMessage());
            }
        });
    } //.ObtenerPerfil

    /** Si esta en modo edicion, guarda cambios | si no pasa a modo edicion */
    public void manejarAccionBotonPrincipal(PropietarioModel propietario) {
        FormUIState uiState = mUIState.getValue();
        boolean modoEdicion = uiState != null && uiState.mostrarCamposEditables;

        if (modoEdicion) {
            guardarCambios(propietario);
        } else {
            cambiarModoEdicion();
        }
    }

    public void cambiarModoEdicion() {
        FormUIState state = UIStateHelper.FormUIState.modoEdicion();

        state.textoBoton = "Guardar Cambios";
        state.mostrarBotonCancelar = true;
        state.conBotonSecundario(false, false);
        state.mostrarCamposEditables = true;
        mUIState.setValue(state);
    }

    public void cancelarEdicion() {
        FormUIState state = UIStateHelper.PerfilUIStates.inicial();

        state.mostrarCamposEditables = false;
        state.mostrarBotonCancelar = false;
        state.conBotonSecundario(true, true);

        mUIState.setValue(state);
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
        if (propietario == null) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.error("Datos del propietario inválidos"));
            return false;
        }

        // Sanitizar y reutilizar
        String nombre = ValidationHelper.sanitizar(propietario.getNombre());
        String apellido = ValidationHelper.sanitizar(propietario.getApellido());
        String dni = ValidationHelper.sanitizar(propietario.getDni());
        String email = ValidationHelper.sanitizar(propietario.getEmail());
        String telefono = ValidationHelper.sanitizar(propietario.getTelefono());

        // Nombre
        if (!ValidationHelper.tieneLongitudMinima(nombre, 2)) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El nombre debe tener al menos 2 caracteres", "nombre"));
            return false;
        }

        // Apellido
        if (!ValidationHelper.tieneLongitudMinima(apellido, 2)) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El apellido debe tener al menos 2 caracteres", "apellido"));
            return false;
        }

        // DNI
        if (!ValidationHelper.esDniValido(dni)) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El DNI debe tener entre 7 y 8 dígitos", "dni"));
            return false;
        }

        // Email
        if (!ValidationHelper.esEmailValido(email)) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El formato del email no es válido", "email"));
            return false;
        }

        // Teléfono
        if (!ValidationHelper.esTelefonoValido(telefono)) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El teléfono debe tener entre 8 y 15 dígitos", "telefono"));
            return false;
        }

        // Validacion OK - volver al modo edición normal
        actualizarUIState();
        return true;
    }

    // ===== MÉTODOS PRIVADOS - UTILIDADES =====
    private void actualizarUIState() {
        FormUIState uiState = mUIState.getValue();
        boolean modoEdicion = uiState != null && uiState.mostrarCamposEditables;

        if (modoEdicion) {
            mUIState.setValue(UIStateHelper.FormUIState.modoVista());
        } else {
            mUIState.setValue(UIStateHelper.FormUIState.modoEdicion());
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

        mUIState.setValue(UIStateHelper.PerfilUIStates.guardandoPerfil());

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.actualizarPerfil(validacion.tokenBearer, propietario);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    propietarioActual = response.body();
                    mPropietario.postValue(propietarioActual);

                    // Cambiar a modo vista con éxito
                    FormUIState state = UIStateHelper.PerfilUIStates.perfilGuardado();
                    state.actualizacionExitosa = true;
                    state.mostrarCamposEditables = false;
                    state.conCarga(false);
                    state.conBotonSecundario(true, true);
                    mUIState.postValue(state);
                } else {
                    Log.e("PerfilViewModel", "Error al actualizar: " + response.code());
                    String mensaje = ErrorHelper.obtenerMensajeError(response.code());
                    FormUIState state = UIStateHelper.PerfilUIStates.error(mensaje);
                    state.actualizacionExitosa = false;
                    mUIState.postValue(state);
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                String mensaje = ErrorHelper.obtenerMensajeConexion(t);
                FormUIState state = UIStateHelper.PerfilUIStates.error(mensaje);
                state.actualizacionExitosa = false;
                mUIState.postValue(state);
                Log.e("PerfilViewModel", "Error de conexión: " + t.getMessage());
            }
        });
    }


}