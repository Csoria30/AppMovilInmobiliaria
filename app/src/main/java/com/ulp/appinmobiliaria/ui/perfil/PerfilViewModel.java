package com.ulp.appinmobiliaria.ui.perfil;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private MutableLiveData<PropietarioModel> mPropietario = new MutableLiveData<>();
    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PropietarioModel> getmPropietario() {
        return mPropietario;
    }

    public void obtenerPerfil(){
        String token = ApiClient.leerToken(getApplication());

        if(token == null || token.isEmpty()){
            Toast.makeText(getApplication(), "Token no encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<PropietarioModel> call = api.obtenerPerfil("Bearer " + token);

        call.enqueue(new Callback<PropietarioModel>() {
            @Override
            public void onResponse(Call<PropietarioModel> call, Response<PropietarioModel> response) {
                if(response.isSuccessful() && response.body() != null){
                    mPropietario.setValue(response.body());
                }else{
                    Log.d("PerfilViewModel", "Error en respuesta: " + response.code());
                    Toast.makeText(getApplication(), "Error al obtener el perfil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PropietarioModel> call, Throwable t) {
                Log.d("PerfilViewModel", "Error de conexión: " + t.getMessage());
                Toast.makeText(getApplication(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}