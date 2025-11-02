package com.ulp.appinmobiliaria.ui.inmueble;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.TokenHelper;
import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.PropietarioModel;
import com.ulp.appinmobiliaria.request.ApiClient;

import retrofit2.Call;

public class InmuebleFormViewModel extends AndroidViewModel {
    private MutableLiveData<InmuebleModel> mInmueble = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.FormUIState> mUIState = new MutableLiveData<>();
    private final Context context;

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

    /**  ===== METODOS PÚBLICOS PRINCIPALES ===== */


    /**  ===== Extras ===== */
    public void obtenerInmuebleActual(Bundle inmuebleActual) {
        InmuebleModel i = (InmuebleModel) inmuebleActual.getSerializable("inmueble");

        if(i != null)
            this.mInmueble.setValue(i);

    }

}