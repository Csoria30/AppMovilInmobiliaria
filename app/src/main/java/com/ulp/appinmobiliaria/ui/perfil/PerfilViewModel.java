package com.ulp.appinmobiliaria.ui.perfil;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private MutableLiveData<PropietarioModel> mPropietario = new MutableLiveData<>();
    private MutableLiveData<Boolean> mCargando = new MutableLiveData<>();
    private MutableLiveData<Boolean> mActualizacionExitosa = new MutableLiveData<>();
    private MutableLiveData<Boolean> mModoEdicion = new MutableLiveData<>();
    private MutableLiveData<String> mErrorValidacion = new MutableLiveData<>();
    private MutableLiveData<String> mMensaje = new MutableLiveData<>();

    private PropietarioModel propietarioActual;

    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    // Getters para LiveData
    public LiveData<PropietarioModel> getmPropietario() {
        return mPropietario;
    }

    public LiveData<Boolean> getMCargando() {
        return mCargando;
    }

    public LiveData<Boolean> getMActualizacionExitosa() {
        return mActualizacionExitosa;
    }

    public LiveData<Boolean> getMModoEdicion() {
        return mModoEdicion;
    }

    public LiveData<String> getMErrorValidacion() {
        return mErrorValidacion;
    }

    public LiveData<String> getMMensaje() {
        return mMensaje;
    }


    // Obtener Perfil
    public void obtenerPerfil() {
        String token = ApiClient.leerToken(getApplication());

        if (!validarToken(token)) {
            return;
        }

        mCargando.setValue(true);

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.obtenerPerfil("Bearer " + token); //Importante el espacio despues de Bearer

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                mCargando.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    propietarioActual = response.body();
                    mPropietario.setValue(propietarioActual);
                    Log.d("PerfilViewModel", "Perfil obtenido: " + propietarioActual.getNombre());
                } else {
                    Log.e("PerfilViewModel", "Error en respuesta: " + response.code() + " - " + response.message());
                    mMensaje.setValue("Error al obtener el perfil: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                mCargando.setValue(false);
                Log.e("PerfilViewModel", "Error de conexión: " + t.getMessage());
                mMensaje.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    // Lógica para cambiar modo de edición
    public void cambiarModoEdicion() {
        Boolean modoActual = mModoEdicion.getValue();
        boolean esModoEdicion = modoActual != null && modoActual;
        boolean nuevoModo = !esModoEdicion;

        mModoEdicion.setValue(nuevoModo);

        if (nuevoModo) {
            Log.d("PerfilViewModel", "Activando modo edición");
        } else {
            Log.d("PerfilViewModel", "Activando modo vista");
            // Limpiar errores al salir del modo edición
            mErrorValidacion.setValue(null);
        }
    }

    // Guardar cambios con validaciones
    public void guardarCambios(String dni, String nombre, String apellido, String email, String telefono) {
        // Validar todos los campos
        if (!validarCampos(dni, nombre, apellido, email, telefono)) {
            return;
        }

        // Crear propietario actualizado
        PropietarioModel propietarioActualizado = crearPropietarioActualizado(dni, nombre, apellido, email, telefono);

        // Enviar actualización
        actualizarPerfil(propietarioActualizado);
    }

    // Validaciones
    private boolean validarToken(String token) {
        if (token == null || token.isEmpty()) {
            mMensaje.setValue("Token no encontrado. Inicie sesión nuevamente.");
            return false;
        }
        return true;
    }

    private boolean validarCampos(String dni, String nombre, String apellido, String email, String telefono) {
        // Validar nombre
        if (!validarCampoNoVacio(nombre, "nombre")) {
            mErrorValidacion.setValue("El nombre es obligatorio");
            return false;
        }
        if (!validarLongitudMinima(nombre, 2, "nombre")) {
            mErrorValidacion.setValue("El nombre debe tener al menos 2 caracteres");
            return false;
        }

        // Validar apellido
        if (!validarCampoNoVacio(apellido, "apellido")) {
            mErrorValidacion.setValue("El apellido es obligatorio");
            return false;
        }
        if (!validarLongitudMinima(apellido, 2, "apellido")) {
            mErrorValidacion.setValue("El apellido debe tener al menos 2 caracteres");
            return false;
        }

        // Validar DNI
        if (!validarCampoNoVacio(dni, "DNI")) {
            mErrorValidacion.setValue("El DNI es obligatorio");
            return false;
        }
        if (!validarDNI(dni)) {
            mErrorValidacion.setValue("El DNI debe tener entre 7 y 8 dígitos");
            return false;
        }

        // Validar email
        if (!validarCampoNoVacio(email, "email")) {
            mErrorValidacion.setValue("El email es obligatorio");
            return false;
        }
        if (!validarFormatoEmail(email)) {
            mErrorValidacion.setValue("El formato del email no es válido");
            return false;
        }

        // Validar teléfono
        if (!validarCampoNoVacio(telefono, "teléfono")) {
            mErrorValidacion.setValue("El teléfono es obligatorio");
            return false;
        }
        if (!validarTelefono(telefono)) {
            mErrorValidacion.setValue("El teléfono debe tener al menos 8 dígitos");
            return false;
        }

        // Limpiar errores si todo está correcto
        mErrorValidacion.setValue(null);
        return true;
    }

    private boolean validarCampoNoVacio(String campo, String nombreCampo) {
        boolean esValido = campo != null && !campo.trim().isEmpty();
        if (!esValido) {
            Log.w("PerfilViewModel", "Campo " + nombreCampo + " está vacío");
        }
        return esValido;
    }

    private boolean validarLongitudMinima(String campo, int longitudMinima, String nombreCampo) {
        boolean esValido = campo != null && campo.trim().length() >= longitudMinima;
        if (!esValido) {
            Log.w("PerfilViewModel", "Campo " + nombreCampo + " no cumple longitud mínima: " + longitudMinima);
        }
        return esValido;
    }

    private boolean validarDNI(String dni) {
        // Remover puntos, espacios y guiones para validar solo números
        String dniLimpio = dni.replaceAll("[^0-9]", "");
        boolean esValido = dniLimpio.length() >= 7 && dniLimpio.length() <= 8;
        if (!esValido) {
            Log.w("PerfilViewModel", "DNI inválido: " + dni + " (limpio: " + dniLimpio + ")");
        }
        return esValido;
    }

    private boolean validarFormatoEmail(String email) {
        boolean esValido = email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        if (!esValido) {
            Log.w("PerfilViewModel", "Email con formato inválido: " + email);
        }
        return esValido;
    }

    private boolean validarTelefono(String telefono) {
        // Remover espacios, guiones y paréntesis para validar solo números
        String telefonoLimpio = telefono.replaceAll("[^0-9]", "");
        boolean esValido = telefonoLimpio.length() >= 8;
        if (!esValido) {
            Log.w("PerfilViewModel", "Teléfono inválido: " + telefono + " (limpio: " + telefonoLimpio + ")");
        }
        return esValido;
    }

    // Lógica para crear propietario actualizado
    // Actualizar el método crearPropietarioActualizado en PerfilViewModel
    private PropietarioModel crearPropietarioActualizado(String dni, String nombre, String apellido, String email, String telefono) {
        if (propietarioActual == null) {
            Log.e("PerfilViewModel", "Error: propietarioActual es null");
            mMensaje.setValue("Error: No se pudo obtener los datos actuales del propietario");
            return null;
        }

        PropietarioModel propietarioActualizado = new PropietarioModel();
        propietarioActualizado.setIdPropietario(propietarioActual.getIdPropietario());
        propietarioActualizado.setDni(dni.trim());
        propietarioActualizado.setNombre(nombre.trim());
        propietarioActualizado.setApellido(apellido.trim());
        propietarioActualizado.setEmail(email.trim());
        propietarioActualizado.setTelefono(telefono.trim());

        // Preservar la contraseña actual
        propietarioActualizado.setClave(propietarioActual.getClave());

        return propietarioActualizado;
    }

    // Lógica para actualizar perfil en la API
    private void actualizarPerfil(PropietarioModel propietario) {
        String token = ApiClient.leerToken(getApplication());

        if (!validarToken(token)) {
            return;
        }

        if (propietario == null) {
            mMensaje.setValue("Error: Datos del propietario inválidos");
            return;
        }

        if (propietario.getClave() == null || propietario.getClave().isEmpty()) {
            mMensaje.setValue("Error: No se pudo conservar la contraseña actual");
            return;
        }

        mCargando.setValue(true);

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.actualizarPerfil("Bearer " + token, propietario);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                mCargando.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PerfilViewModel", "Perfil actualizado correctamente");
                    propietarioActual = response.body();
                    mPropietario.setValue(propietarioActual);
                    mActualizacionExitosa.setValue(true);
                    mMensaje.setValue("Perfil actualizado correctamente");
                    cambiarModoEdicion();
                } else {
                    Log.e("PerfilViewModel", "Error al actualizar: " + response.code() + " - " + response.message());
                    mMensaje.setValue("Error al actualizar el perfil: " + response.code());
                    mActualizacionExitosa.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                mCargando.setValue(false);
                Log.e("PerfilViewModel", "Error de conexión: " + t.getMessage());
                mMensaje.setValue("Error de conexión: " + t.getMessage());
                mActualizacionExitosa.setValue(false);
            }
        });
    }

    // Método para cancelar edición
    public void cancelarEdicion() {
        Log.d("PerfilViewModel", "Cancelando edición");
        mErrorValidacion.setValue(null);
        cambiarModoEdicion();
    }

    // Getters para datos del propietario actual (para el Fragment)
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
}