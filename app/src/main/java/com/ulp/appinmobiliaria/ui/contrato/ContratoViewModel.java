package com.ulp.appinmobiliaria.ui.contrato;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.InmuebleConContratoModel;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContratoViewModel extends AndroidViewModel {
    private MutableLiveData<List<InmuebleConContratoModel>> mListaInmueblesCon = new MutableLiveData<>();
    private MutableLiveData<List<ContratoModel>> mListaContrato = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.ListUIState> mUIStateLista = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.FormUIState> mUIStateForm = new MutableLiveData<>();
    private Context context;
    private List<Integer> idsInmueblesConContrato = new ArrayList<>();
    private List<ContratoModel> listaContratosVigentes = new ArrayList<>();


    // ===== CONSTRUCTOR =====
    public ContratoViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    // ===== GETTERS PARA LIVEDATA =====

    public LiveData<List<InmuebleConContratoModel>> getmListaInmueblesCon() {
        return mListaInmueblesCon;
    }

    public LiveData<List<ContratoModel>> getmListaContrato() {
        return mListaContrato;
    }

    public LiveData<UIStateHelper.ListUIState> getmUIStateLista() {
        return mUIStateLista;
    }

    public LiveData<UIStateHelper.FormUIState> getmUIStateForm() {
        return mUIStateForm;
    }

    public List<ContratoModel> getListaContratosVigentes() {
        return listaContratosVigentes;
    }

    //  ===== METODOS  PRINCIPALES =====
    public void obtenerInmueblesConContratos(){
        //progessBar cargando perfil
        mUIStateForm.setValue(UIStateHelper.FormUIState.cargando());

        // Obtener token
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

        if (!validacion.esValido) {
            // mUIState.setValue(UIStateHelper.InmueblesUIStates.error(validacion.mensajeError));
            return;
        }

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<List<InmuebleConContratoModel>> call = api.obtenerInmueblesConContratos(validacion.tokenBearer);

        call.enqueue(new Callback<List<InmuebleConContratoModel>>() {
            @Override
            public void onResponse(Call<List<InmuebleConContratoModel>> call, Response<List<InmuebleConContratoModel>> response) {
                if(response.isSuccessful() && response.body() != null){
                    idsInmueblesConContrato.clear();

                    for(InmuebleConContratoModel inmueble : response.body()){
                        if (inmueble.isTieneContratoVigente()) { //Verificacion que contratoVigente sea True
                            idsInmueblesConContrato.add(inmueble.getIdInmueble());
                        }
                    }

                    //LLamar a la segunda funcion de la API
                    if (!idsInmueblesConContrato.isEmpty()) {
                        cargarContratosVigentes();
                    } else {
                        mListaContrato.postValue(new ArrayList<>());
                    }

                    //UI
                    UIStateHelper.FormUIState state = UIStateHelper.InmuebleUIState.inicial();
                    state.conCarga(false);
                    mUIStateForm.postValue(state);

                }
            }

            @Override
            public void onFailure(Call<List<InmuebleConContratoModel>> call, Throwable t) {
                UIStateHelper.FormUIState state = UIStateHelper.FormUIState.error("Error al cargar contratos");
                state.conCarga(false);
                mUIStateForm.postValue(state);
            }
        });
    }

    public void cargarContratosVigentes(){
        // Obtener token
        TokenHelper.ResultadoValidacion validacion = TokenHelper.validarToken(context);

        if (!validacion.esValido) {
            // mUIState.setValue(UIStateHelper.InmueblesUIStates.error(validacion.mensajeError));
            return;
        }

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();

        for (int idInmueble : idsInmueblesConContrato) {
            Call<ContratoModel> call = api.obtenerContratoPorInmueble(validacion.tokenBearer, idInmueble);

            call.enqueue(new Callback<ContratoModel>() {
                @Override
                public void onResponse(Call<ContratoModel> call, Response<ContratoModel> response) {
                    if (response.isSuccessful() && response.body() != null){
                        listaContratosVigentes.add(response.body());
                        mListaContrato.postValue(new ArrayList<>(listaContratosVigentes));
                    }
                }

                @Override
                public void onFailure(Call<ContratoModel> call, Throwable t) {
                    UIStateHelper.FormUIState state = UIStateHelper.FormUIState.error("Error al cargar contratos");
                    state.conCarga(false);
                    mUIStateForm.postValue(state);
                }
            });
        }

    }

}