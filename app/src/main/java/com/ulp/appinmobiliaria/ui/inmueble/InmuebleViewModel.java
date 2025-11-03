package com.ulp.appinmobiliaria.ui.inmueble;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.common.api.Api;
import com.ulp.appinmobiliaria.helpers.ErrorHelper;
import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmuebleViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<List<InmuebleModel>> listaInmuebles = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.ListUIState> mUIState = new MutableLiveData<>();
    private List<InmuebleModel> listaInmueblesCache = new ArrayList<>();

    public InmuebleViewModel(@NonNull Application application) {
        super(application);
        this.context = getApplication();
    }

    public LiveData<List<InmuebleModel>> getListaInmuebles() {
        return listaInmuebles;
    }

    public void cargarInmuebles(){
        //progessBar cargando perfil
        mUIState.setValue(UIStateHelper.ListUIState.cargando());

        // Obtener token
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

        if (!validacion.esValido) {
            // mUIState.setValue(UIStateHelper.InmueblesUIStates.error(validacion.mensajeError));
            return;
        }

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<List<InmuebleModel>> call = api.obtenerInmuebles(validacion.tokenBearer);

        call.enqueue(new Callback<List<InmuebleModel>>() {
            @Override
            public void onResponse(Call<List<InmuebleModel>> call, Response<List<InmuebleModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //int idPropietario = TokenHelper.obtenerIdPropietario(context);
                    int idPropietario = TokenHelper.obtenerIdPropietario(context);
                    List<InmuebleModel> inmuebles = response.body();
                    List<InmuebleModel> inmueblesPropios = new ArrayList<>();

                    for (InmuebleModel i : inmuebles) {
                        if (i.getIdPropietario() == idPropietario)
                            inmueblesPropios.add(i);
                    }

                    inmueblesPropios.sort((a, b) -> a.getDireccion().compareToIgnoreCase(b.getDireccion()));

                    //Guardando en memoria
                    listaInmueblesCache.clear();
                    listaInmueblesCache.addAll(inmueblesPropios);

                    listaInmuebles.postValue(inmueblesPropios);
                    mUIState.postValue(UIStateHelper.ListUIState.conDatos(inmueblesPropios.size()));
                } else {
                    String mensaje = ErrorHelper.obtenerMensajeError(response.code());
                    // mUIState.setValue(UIStateHelper.InmueblesUIStates.error(mensaje));
                }
            }

            @Override
            public void onFailure(Call<List<InmuebleModel>> call, Throwable t) {
                String mensaje = ErrorHelper.obtenerMensajeConexion(t);
                // mUIState.setValue(UIStateHelper.InmueblesUIStates.error(mensaje));
            }
        });
    }
}