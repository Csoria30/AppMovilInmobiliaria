package com.ulp.appinmobiliaria.ui.login;

import android.app.Application;
import android.content.Context;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> loginExitoso = new MutableLiveData<>();
    private MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private Context context;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }
    public LiveData<Boolean> getLoginExitoso() {
        return loginExitoso;
    }

    public void login(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            mensajeError.setValue("El email es requerido");
            return;
        }

        // Validar formato de email
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            mensajeError.setValue("El formato del email no es válido");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            mensajeError.setValue("La contraseña es requerida");
            return;
        }

        ApiClient.InmobiliariaService api = ApiClient.getApiInmobiliaria();
        Call<String> llamada = api.login(email,password);
        llamada.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    String token = response.body();
                    ApiClient.guardarToken(context, token);
                    loginExitoso.setValue(true);

                }else{
                    mensajeError.postValue("Usuario y contraseña incorrecto");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

}
