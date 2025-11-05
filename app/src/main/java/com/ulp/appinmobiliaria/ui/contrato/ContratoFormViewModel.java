package com.ulp.appinmobiliaria.ui.contrato;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ulp.appinmobiliaria.helpers.UIStateHelper;
import com.ulp.appinmobiliaria.helpers.ValidationHelper;
import com.ulp.appinmobiliaria.model.ContratoModel;
import com.ulp.appinmobiliaria.model.InmuebleModel;
import com.ulp.appinmobiliaria.model.InquilinoModel;

public class ContratoFormViewModel extends AndroidViewModel {
    private MutableLiveData<ContratoModel> mContrato = new MutableLiveData<>();
    private MutableLiveData<UIStateHelper.FormUIState> mUIState = new MutableLiveData<>();
    private final Context context;

    // ===== CONSTRUCTOR =====
    public ContratoFormViewModel(@NonNull Application application) {
        super(application);
        context = getApplication();
    }

    // ===== GETTERS PARA LIVEDATA =====

    public LiveData<ContratoModel> getmContrato() {
        return mContrato;
    }

    public LiveData<UIStateHelper.FormUIState> getmUIState() {
        return mUIState;
    }

    /**  ===== METODOS PÚBLICOS PRINCIPALES ===== */
    public void obtenerContratoActual(Bundle contratoActual){
        if(contratoActual != null && contratoActual.containsKey("contrato")){
            //progessBar cargando perfil
            mUIState.setValue(UIStateHelper.FormUIState.cargando());

            ContratoModel c = (ContratoModel) contratoActual.getSerializable("contrato");

            if(c != null)
                this.mContrato.setValue(c);

            UIStateHelper.FormUIState state = UIStateHelper.InmuebleUIState.inicial();
            state.conCarga(false);
            state.conEsNuevo(false);
            mUIState.setValue(state);

        }
    }


    /** ===== GETTERS PARA DATOS DEL PROPIETARIO =====
     *  Nota: Evitamos retornos nulos
     * */
    public String getDireccion() {
        ContratoModel contrato = mContrato.getValue();
        InmuebleModel inmueble = contrato != null ? contrato.getInmueble() : null;
        String valor = (inmueble != null && inmueble.getDireccion() != null) ? inmueble.getDireccion().trim() : "-";
        return "Dirección: " + valor;
    }

    public String getTipo() {
        ContratoModel contrato = mContrato.getValue();
        InmuebleModel inmueble = contrato != null ? contrato.getInmueble() : null;
        String valor = (inmueble != null && inmueble.getTipo() != null) ? inmueble.getTipo().trim() : "-";
        return "Tipo: " + valor;
    }

    public String getFechaInicio() {
        ContratoModel contrato = mContrato.getValue();
        String raw = contrato != null && contrato.getFechaInicio() != null ? contrato.getFechaInicio() : "";
        String fecha =  ValidationHelper.formatearFecha(raw);
        return "Fecha inicio: " + fecha;
    }

    public String getFechaFin() {
        ContratoModel contrato = mContrato.getValue();
        String raw = contrato != null && contrato.getFechaFinalizacion() != null ? contrato.getFechaFinalizacion() : "";
        String fecha =  ValidationHelper.formatearFecha(raw);
        return "Fecha finalización: " + fecha;
    }

    public String getMontoAlquiler() {
        ContratoModel contrato = mContrato.getValue();
        double monto = contrato != null ? contrato.getMontoAlquiler() : 0d;
        return "Monto alquiler: " + String.format("$ %.2f", monto);
    }

    public String getEstadoTexto() {
        ContratoModel contrato = mContrato.getValue();
        if (contrato == null) return "Estado: -";
        return "Estado: " + (contrato.isEstado() ? "Activo" : "Finalizado");
    }

    public String getInquilinoResumen() {
        ContratoModel contrato = mContrato.getValue();
        if (contrato == null || contrato.getInquilino() == null) return "Inquilino: -";
        String nombre = contrato.getInquilino().getNombre() != null ? contrato.getInquilino().getNombre() : "";
        String apellido = contrato.getInquilino().getApellido() != null ? contrato.getInquilino().getApellido() : "";
        String full = (apellido + " " + nombre).trim();
        return "Inquilino: " + (full.isEmpty() ? "-" : full);
    }

}