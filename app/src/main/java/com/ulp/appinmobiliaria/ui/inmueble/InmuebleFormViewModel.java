package com.ulp.appinmobiliaria.ui.inmueble;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.errorprone.annotations.InlineMe;
import com.google.gson.Gson;
import com.ulp.appinmobiliaria.R;
import com.ulp.appinmobiliaria.helpers.ErrorHelper;
import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.helpers.ValidationHelper;
import com.ulp.appinmobiliaria.model.InmuebleCrearDTO;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class InmuebleFormViewModel extends AndroidViewModel {
    private MutableLiveData<InmuebleModel> mInmueble = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.FormUIState> mUIState = new MutableLiveData<>();
    private MutableLiveData<Uri> mURI = new MutableLiveData<>();
    private final Context context;
    private InmuebleModel inmuebleActual;


    // ===== CONSTRUCTOR =====
    public InmuebleFormViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    // ===== GETTERS PARA LIVEDATA =====

    public LiveData<InmuebleModel> getmInmueble() {
        return mInmueble;
    }

    public LiveData<UIStateHelper.FormUIState> getmUIState() {
        return mUIState;
    }

    public LiveData<Uri> getmURI() {
        return mURI;
    }

    /**  ===== METODOS PÚBLICOS PRINCIPALES ===== */

    public void obtenerInmuebleActual(Bundle inmuebleActual) {
        if(inmuebleActual != null && inmuebleActual.containsKey("inmueble")){
            InmuebleModel i = (InmuebleModel) inmuebleActual.getSerializable("inmueble");

            if(i != null)
                this.mInmueble.setValue(i);
        }else{
            //Creacion
            InmuebleModel nuevo = new InmuebleModel();
            nuevo.setDisponible(true);
            mInmueble.setValue(nuevo);
            cambiarModoEdicion();
        }
    }

    /** Si esta en modo edicion, guarda cambios | si no pasa a modo edicion */
    public void manejarAccionBotonPrincipal(InmuebleModel inmueble) {
        UIStateHelper.FormUIState uiState = mUIState.getValue();
        boolean modoEdicion = uiState != null && uiState.mostrarCamposEditables;

        if (modoEdicion) {
            guardarCambios(inmueble);
        } else {
            cambiarModoEdicion();
        }
    }

    public void cambiarModoEdicion() {
        UIStateHelper.FormUIState state = UIStateHelper.PerfilUIStates.modoEdicion(false);
        state.mostrarCamposEditables = true;
        mUIState.setValue(state);
    }

    public void cancelarEdicion() {
        UIStateHelper.FormUIState state = UIStateHelper.PerfilUIStates.modoVista(false);
        state.mostrarCamposEditables = false;
        mUIState.setValue(state);
    }

    public void guardarCambios(InmuebleModel inmueble) {
        if (!validarCamposConHelpers(inmueble)) {
            return;
        }
        actualizarInmuble(inmueble);
    }

    /** =====  VALIDACIONES ===== */
    private boolean validarCamposConHelpers(InmuebleModel inmueble) {
        // Helper Validacion: Direccion
        if (inmueble.getDireccion() == null || inmueble.getDireccion().trim().length() < 2) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La direccion es obligatorio", "direccion"));
            return false;
        }

        // Helper Validacion: Tipo
        if (inmueble.getTipo() == null || inmueble.getTipo().trim().length() < 2) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El Tipo debe tener al menos 2 caracteres", "tipo"));
            return false;
        }

        // Validación: Precio (mayor a 0)
        if (inmueble.getValor() <= 0) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El precio debe ser mayor a 0", "precio"));
            return false;
        }

        // Validación: Ambientes (mayor a 0)
        if (inmueble.getAmbientes() <= 0) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La cantidad de ambientes debe ser mayor a 0", "ambiente"));
            return false;
        }

        // Validacion OK - volver al modo edición normal
        actualizarUIState();
        return true;
    }
    private boolean validarCrearInmuebleDTO(InmuebleCrearDTO inmuebleCrearDTO){
        if (inmuebleCrearDTO.getDireccion() == null || inmuebleCrearDTO.getDireccion().trim().length() < 2) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La direccion es obligatorio", "direccion"));
            return false;
        }

        // Helper Validacion: Tipo
        if (inmuebleCrearDTO.getTipo() == null || inmuebleCrearDTO.getTipo().trim().length() < 2) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El Tipo debe tener al menos 2 caracteres", "tipo"));
            return false;
        }

        // Validación: Precio (mayor a 0)
        if (inmuebleCrearDTO.getValor() <= 0) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("El precio debe ser mayor a 0", "precio"));
            return false;
        }

        // Validación: Ambientes (mayor a 0)
        if (inmuebleCrearDTO.getAmbientes() <= 0) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("La cantidad de ambientes debe ser mayor a 0", "ambiente"));
            return false;
        }

        // Validacion OK - volver al modo edición normal
        actualizarUIState();
        return true;
    }
    // ===== MÉTODOS PRIVADOS - UTILIDADES =====
    private void actualizarUIState() {
        UIStateHelper.FormUIState uiState = mUIState.getValue();
        boolean modoEdicion = uiState != null && uiState.mostrarCamposEditables;

        if (modoEdicion) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.modoEdicion(false));
        } else {
            mUIState.setValue(UIStateHelper.PerfilUIStates.modoVista(false));
        }
    }

    private InmuebleModel crearInmuebleActualizado(InmuebleModel nuevosDatos) {
        if (mInmueble == null) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("Error: No se pudo obtener los datos actuales del propietario", "general"));
            return null;
        }

        InmuebleModel inmuebleActualizado = new InmuebleModel();
        inmuebleActualizado.setIdInmueble(mInmueble.getValue().getIdInmueble());
        inmuebleActualizado.setDireccion(nuevosDatos.getDireccion().trim());
        inmuebleActualizado.setTipo(nuevosDatos.getTipo().trim());
        inmuebleActualizado.setAmbientes(nuevosDatos.getAmbientes());
        inmuebleActualizado.setValor(nuevosDatos.getValor());
        inmuebleActualizado.setDisponible(nuevosDatos.getDisponible());

        return inmuebleActualizado;
    }

    /** === Actualiza inmueble === **/
    private void actualizarInmuble(InmuebleModel inmueble){
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(getApplication());

        if (!validacion.esValido) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.error(validacion.mensajeError));
            return;
        }

        if (inmueble == null) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion(
                    "Error: Datos del propietario inválidos", "propietario"
            ));
            return;
        }

        mUIState.setValue(UIStateHelper.PerfilUIStates.guardandoCambios());

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<InmuebleModel> call = api.actualizarInmueble(validacion.tokenBearer, inmueble);

        call.enqueue(new Callback<InmuebleModel>() {
            @Override
            public void onResponse(Call<InmuebleModel> call, Response<InmuebleModel> response) {
                if(response.isSuccessful() && response.body() != null){
                    mInmueble.setValue(response.body());

                    //Cambiar a modo vista con exito
                    UIStateHelper.FormUIState state = UIStateHelper.PerfilUIStates.exitoGuardado();
                    state.actualizacionExitosa = true;
                    state.mostrarCamposEditables = false;
                    mUIState.setValue(state);
                }else{
                    Log.e("InmuebleViewModel", "Error al actualizar: " + response.code());
                    String mensaje = ErrorHelper.obtenerMensajeError(response.code());
                    UIStateHelper.FormUIState state = UIStateHelper.PerfilUIStates.error(mensaje);
                    state.actualizacionExitosa = false;
                    mUIState.setValue(state);
                }
            }

            @Override
            public void onFailure(Call<InmuebleModel> call, Throwable t) {
                String mensaje = ErrorHelper.obtenerMensajeConexion(t);
                UIStateHelper.FormUIState state = UIStateHelper.PerfilUIStates.error(mensaje);
                state.actualizacionExitosa = false;
                mUIState.setValue(state);
                Log.e("InmuebleViewModel", "Error de conexión: " + t.getMessage());
            }
        });
    }

    /** ===== CARGAR ===== */
    public void recibirFoto(ActivityResult result){
        if(result.getResultCode() == RESULT_OK){
            Intent data = result.getData();

            if (data != null) {
                Uri uri = data.getData();
                Log.d("imagenout", uri.toString());
                mURI.setValue(uri);
            }
        }
    }

    public void cargarInmueble(String direccion, String uso, String tipo, String ambientes, String precio){

        try {
            InmuebleCrearDTO inmuebleNuevo = new InmuebleCrearDTO();
            // Setear los valores al modelo
            inmuebleNuevo.setDireccion(direccion.trim());
            inmuebleNuevo.setUso(uso.trim());
            inmuebleNuevo.setTipo(tipo.trim());
            inmuebleNuevo.setAmbientes(Integer.parseInt(ambientes));
            inmuebleNuevo.setValor(Double.parseDouble(precio));
            inmuebleNuevo.setDisponible(true); // Por defecto disponible


            if(mURI.getValue() == null){
                Toast.makeText(getApplication(), "Debe elegir una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            if (validarCrearInmuebleDTO(inmuebleNuevo)) {
                byte[] imagen = transformarImagen();
                if (imagen == null || imagen.length == 0) {
                    Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                    return;
                }

                String inmuebleJson = new Gson().toJson(inmuebleNuevo);

                RequestBody inmuebleBody = RequestBody.create(MediaType.parse("application/json; chatset=utf-8"), inmuebleJson);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), imagen);

                //Multipart
                MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestFile);

                //Obteniedo informacion del Token
                TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

                //Intancia de API
                ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
                Call<InmuebleCrearDTO> call = api.cargarInmueble(validacion.tokenBearer, imagenPart, inmuebleBody);

                call.enqueue(new Callback<InmuebleCrearDTO>() {
                    @Override
                    public void onResponse(Call<InmuebleCrearDTO> call, Response<InmuebleCrearDTO> response) {
                        if(response.isSuccessful()){
                            if (response.body() != null){
                                UIStateHelper.FormUIState state = UIStateHelper.PerfilUIStates.exitoGuardado();
                                state.actualizacionExitosa = true;
                                mUIState.setValue(state);
                            }else{
                                Log.e("API", "Response body es null");
                                mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("Error: Respuesta vacía del servidor", "general"));
                            }
                        }else{
                            Toast.makeText(context, "ERROR Creando Inmueble -_-", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<InmuebleCrearDTO> call, Throwable t) {

                    }
                });



            }
        } catch (NumberFormatException e) {
            mUIState.setValue(UIStateHelper.PerfilUIStates.errorValidacion("Los valores numéricos son inválidos", "general"));
        }
    }

    private byte[] transformarImagen(){
        try{
            Uri uri = mURI.getValue();
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();

        } catch (FileNotFoundException e) {
            Toast.makeText(context, "No selecciono una foto", Toast.LENGTH_SHORT).show();
            return new byte[]{};
        }
    }


    /** ===== GETTERS PARA DATOS DEL PROPIETARIO =====
     *  Nota: Evitamos retornos nulos
     * */
    public String getDireccion(){
        return mInmueble.getValue() != null ? mInmueble.getValue().getDireccion() : "";
    }

    public String getTipo() {
        return mInmueble.getValue() != null ? mInmueble.getValue().getTipo() : "";
    }
    public String getUso(){
        return mInmueble.getValue() != null ? mInmueble.getValue().getUso() : "";
    }

    public String getAmbientes() {
        return mInmueble.getValue() != null ? String.valueOf(mInmueble.getValue().getAmbientes()) : "";
    }

    public String getValor() {
        return mInmueble.getValue() != null ? String.valueOf(mInmueble.getValue().getValor()) : "";
    }

    public boolean getDisponible() {
        return mInmueble.getValue() != null && mInmueble.getValue().getDisponible();
    }
}