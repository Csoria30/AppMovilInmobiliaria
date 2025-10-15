package com.ulp.appinmobiliaria.ui.logout;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;

public class LogoutViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mLogoutExitoso = new MutableLiveData<>();
    private MutableLiveData<String> mMensaje = new MutableLiveData<>();
    private MutableLiveData<Boolean> mMostrarDialogo = new MutableLiveData<>();
    private Context context;

    /** === Constructor === */
    public LogoutViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    /** === Getters LiveData === */

    public LiveData<Boolean> getLogoutExitoso() {
        return mLogoutExitoso;
    }

    public LiveData<String> getMensaje() {
        return mMensaje;
    }

    public LiveData<Boolean> getMostrarDialogo() {
        return mMostrarDialogo;
    }

    /** === Logica === */
    public void inicializar(){
        try{
            if(TokenHelper.tieneSesionActiva(context)){
                mMostrarDialogo.setValue(true);
            }else{
                mMensaje.setValue("No hay session activa");
                mLogoutExitoso.setValue(true);
            }
        }catch (Exception e){
            mMensaje.setValue("Error al verificar Session");
            mLogoutExitoso.setValue(false);
        }
    }

    public void realizarLogout(){
        try{
            boolean sesionLimpiada = TokenHelper.limpiarSesion(context);

            if(sesionLimpiada){
                mMensaje.setValue("Sesión cerrada exitosamente");
                mLogoutExitoso.setValue(true);
            }else{
                mMensaje.setValue("Error al cerrar sesión");
                mLogoutExitoso.setValue(false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}