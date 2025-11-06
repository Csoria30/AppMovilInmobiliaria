package com.ulp.appinmobiliaria.ui.inquilino;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.InquilinoModel;

public class InquilinoViewModel extends AndroidViewModel {
    private MutableLiveData<InquilinoModel> mInquilino = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.FormUIState> mUIState = new MutableLiveData<>();
    private final Context context;

    // ===== CONSTRUCTOR =====
    public InquilinoViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    // ===== GETTERS PARA LIVEDATA =====


    public LiveData<InquilinoModel> getmInquilino() {
        return mInquilino;
    }

    public LiveData<UIStateHelper.FormUIState> getmUIState() {
        return mUIState;
    }

    /**  ===== METODOS PÚBLICOS PRINCIPALES ===== */
    public void obtenerInquilinoActual(Bundle inquilinoActual){
        if(inquilinoActual != null && inquilinoActual.containsKey("inquilino")){
            //progessBar cargando perfil
            mUIState.setValue(UIStateHelper.FormUIState.cargando());

            InquilinoModel inquilinoModel = (InquilinoModel) inquilinoActual.getSerializable("inquilino");

            if(inquilinoModel != null)
                this.mInquilino.setValue(inquilinoModel);

            UIStateHelper.FormUIState state = UIStateHelper.InmuebleUIState.inicial();
            state.conCarga(false);
            state.conEsNuevo(false);
            mUIState.setValue(state);
        }
    }

    /** ===== GETTERS PARA DATOS DEL PROPIETARIO =====
     *  Nota: Evitamos retornos nulos
     * */
    public String getInquilinoNombreCompleto() {
        InquilinoModel inq = mInquilino.getValue();
        if (inq == null) return "Inquilino: -";
        String nombre = inq.getNombre() != null ? inq.getNombre().trim() : "";
        String apellido = inq.getApellido() != null ? inq.getApellido().trim() : "";
        String full = (apellido + " " + nombre).trim();
        return "Inquilino: " + (full.isEmpty() ? "-" : full);
    }

    public String getInquilinoDni() {
        InquilinoModel inq = mInquilino.getValue();
        String dni = (inq != null && inq.getDni() != null) ? inq.getDni().trim() : "";
        return "DNI: " + (dni.isEmpty() ? "-" : dni);
    }

    public String getInquilinoTelefono() {
        InquilinoModel inq = mInquilino.getValue();
        String tel = (inq != null && inq.getTelefono() != null) ? inq.getTelefono().trim() : "";
        return "Teléfono: " + (tel.isEmpty() ? "-" : tel);
    }

    public String getInquilinoEmail() {
        InquilinoModel inq = mInquilino.getValue();
        String email = (inq != null && inq.getEmail() != null) ? inq.getEmail().trim() : "";
        return "Email: " + (email.isEmpty() ? "-" : email);
    }

}